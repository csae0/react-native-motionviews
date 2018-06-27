package team.uptech.motionviews.utils;

import at.csae0.reactnative.R;
import at.csae0.reactnative.RNMotionViewModule;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;

import java.io.InputStream;

public class RessourceUtils {

    private static final String IMAGE_ASSETS_PATH = "custom/";
    private static final String FONT_ASSETS_PATH = "fonts/";

    @Nullable
    public static Drawable getImageRessource(String imageName) {
        ReactApplicationContext reactContext = RNMotionViewModule.getReactContext();

        int ressourceId = getRessourceId(reactContext, imageName, RESSOURCE_TYPE.DRAWABLE);

        if (ressourceId != 0) {
            return reactContext.getResources().getDrawable(ressourceId);
        }
        return null;
    }

    @Nullable
    public static Drawable getImageAsset(String imageName, String fileExtension) {
        ReactApplicationContext reactContext = RNMotionViewModule.getReactContext();

        try {
            Drawable drawable = Drawable.createFromStream(reactContext.getAssets().open(IMAGE_ASSETS_PATH + imageName + "." + fileExtension), null);
            return drawable;
        } catch(Exception e) {
            return null;
        }
    }

    public static Typeface getFontAsset(String fontName, String fileExtension) {
        ReactApplicationContext reactContext = RNMotionViewModule.getReactContext();
        Typeface tf = Typeface.createFromAsset(reactContext.getAssets(), FONT_ASSETS_PATH + fontName + "." + fileExtension);
        return tf;
    }

    @Nullable
    public static Integer getRessourceId(ReactApplicationContext reactContext, String id, @Nullable RESSOURCE_TYPE type) {

        if (type != null) {
            return reactContext.getResources().getIdentifier(id, type.getName(), reactContext.getPackageName());
        }
        return null;
    }
}
