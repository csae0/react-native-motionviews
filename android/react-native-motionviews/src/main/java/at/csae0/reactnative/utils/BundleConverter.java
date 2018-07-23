package at.csae0.reactnative.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.sketchView.SketchFile;

import java.util.ArrayList;

public class BundleConverter {
    private static final String LOCAL_FILE_PATH_KEY ="localFilePath";
    private static final String WIDTH_KEY ="width";
    private static final String HEIGHT_KEY ="height";

    public static Bundle toBundle(ReadableMap map) {
        Bundle bundle = new Bundle();
        ReadableMapKeySetIterator it = map.keySetIterator();
        while (it.hasNextKey()) {
            String key = it.nextKey();
            switch (map.getType(key)) {
                case Null:
                    break;
                case Boolean:
                    bundle.putBoolean(key, map.getBoolean(key));
                    break;
                case Number:
                    putNumber(bundle, map, key);
                    break;
                case String:
                    bundle.putString(key, map.getString(key));
                    break;
                case Map:
                    bundle.putBundle(key, toBundle(map.getMap(key)));
                    break;
                case Array:
                    bundle.putBundle(key, toBundle(map.getArray(key)));
                    break;
                default:
                    break;
            }
        }
        return bundle;
    }

    private static void putNumber(Bundle bundle, ReadableMap map, String key) {
        try {
            bundle.putInt(key, map.getInt(key));
        } catch (Exception e) {
            bundle.putDouble(key, map.getDouble(key));
        }
    }

    public static Bundle toBundle(ReadableArray array) {
        Bundle bundle = new Bundle();
        for (int i = 0; i < array.size(); i++) {
            String key = String.valueOf(i);
            switch (array.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    bundle.putBoolean(key, array.getBoolean(i));
                    break;
                case Number:
                    bundle.putDouble(key, array.getDouble(i));
                    break;
                case String:
                    bundle.putString(key, array.getString(i));
                    break;
                case Map:
                    bundle.putBundle(key, toBundle(array.getMap(i)));
                    break;
                case Array:
                    bundle.putBundle(key, toBundle(array.getArray(i)));
                    break;
                default:
                    break;
            }
        }
        return bundle;
    }

    @Nullable
    public static ArrayList<?> bundleToArrayList(Bundle bundle) { // bundle needs to be a converted array (index 0 - n of same type)
        Object check = bundle.get("0");
        if (check != null) {

            // check.getClass() == int.class || check.getClass() == Integer.class check for primitive type
            if (check instanceof String) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (String key : bundle.keySet()) {
                    String value = bundle.getString(key);
                    if (value != null) {
                        arrayList.add(value);
                    }
                }
                return arrayList;
            } else if (check instanceof Bundle) {
                ArrayList<Bundle> arrayList = new ArrayList<>();
                for (String key : bundle.keySet()) {
                    Bundle value = bundle.getBundle(key);
                    if (value != null) {
                        arrayList.add(value);
                    }
                }
                return arrayList;
            }
        }
        return null;
    }

    @Nullable
    public static Bundle sketchFileToBundle (@Nullable SketchFile sketchFile) {
        if (sketchFile == null) {
            return null;
        }

        Bundle bundle = new Bundle();
        bundle.putString(LOCAL_FILE_PATH_KEY, sketchFile.localFilePath);
        bundle.putInt(WIDTH_KEY, sketchFile.width);
        bundle.putInt(HEIGHT_KEY, sketchFile.height);
        return bundle;
    }
    
    public static WritableMap sketchFileBundleToWritableMap (@Nullable Bundle bundle) {
        if (bundle == null) {
            return Arguments.createMap();
        }

        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(LOCAL_FILE_PATH_KEY, bundle.getString("localFilePath", ""));
        writableMap.putInt(WIDTH_KEY, bundle.getInt("width", -1));
        writableMap.putInt(HEIGHT_KEY, bundle.getInt("height",-1));
        return writableMap;
    }
}