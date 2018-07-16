package at.csae0.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import at.csae0.reactnative.utils.BundleConverter;
import team.uptech.motionviews.ui.MotionViewsActivity;

public class RNMotionViewModule extends ReactContextBaseJavaModule {

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    public static final String OPTIONS_ID = "OPTIONS";
    private static ReactApplicationContext reactApplicationContext = null;
    public static ReactApplicationContext getReactContext () {
        return reactApplicationContext;
    }

    private Promise promise;
    private OnActivityResultListener onActivityResultListener;

    public RNMotionViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactApplicationContext = reactContext;

    }

    @ReactMethod
    public void startWithOptions (ReadableMap params, final Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            this.promise = promise;
            this.onActivityResultListener = new OnActivityResultListener();
            this.reactApplicationContext.addActivityEventListener(onActivityResultListener);

            try {
                Intent intent = new Intent(activity, MotionViewsActivity.class);
                Bundle options = BundleConverter.toBundle(params);

                intent.putExtra(RNMotionViewModule.OPTIONS_ID, options);
                activity.startActivityForResult(intent, MotionViewsActivity.START_MOTION_VIEW_REQUEST_CODE);
            } catch (Exception e) {
                promise.reject(E_FAILED_TO_SHOW_PICKER, e.getMessage());
            }
        } else {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity is null");
        }
    }

    @Override
    public String getName() {
        return "RNMotionViews";
    }

    private void release () {
        reactApplicationContext.removeActivityEventListener(onActivityResultListener);
        //        reactApplicationContext = null;
        onActivityResultListener = null;
        promise = null;
    }

    private class OnActivityResultListener extends BaseActivityEventListener {
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == MotionViewsActivity.START_MOTION_VIEW_REQUEST_CODE) {
                if (resultCode == MotionViewsActivity.RESULT_SUBMITTED) {
                    Bundle resultImage = data.getExtras().getBundle(MotionViewsActivity.RESULT_IMAGE_KEY);
                    WritableMap writableMap = BundleConverter.sketchFileBundleToWritableMap(resultImage);
                    if (promise != null) {
//                        if (writableMap != null) {
                            promise.resolve(writableMap);
//                        } else {
//                            promise.se(E_NO_IMAGE_DATA_FOUND, "No image data found.");
//                        }
                    }
                    release();
                } else if (resultCode == MotionViewsActivity.RESULT_CANCELED) {
                    if (promise != null) {
                        promise.resolve(null);
                    }
                    release();
                }
            }
        }
    }
}



/**
 *  TODO LIST init params:
 *
 * - ("back to easily" button callback) ?
 * - config for shown settings/ entity buttons, colors + color options + initial color, appTheme, min/max font size/stroke thickness, placeholder text (textEntity), default opened "create entity action" (e.g. sketchView)
 * - button/ label text
 *
 */