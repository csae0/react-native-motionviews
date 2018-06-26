package at.csae0.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import at.csae0.reactnative.utils.BundleConverter;
import team.uptech.motionviews.ui.MotionViewsActivity;

public class RNMotionViewModule extends ReactContextBaseJavaModule {
    public static final String OPTIONS_ID = "OPTIONS";

    public RNMotionViewModule(ReactApplicationContext reactContext) {
        super(reactContext);

    }

    @ReactMethod
    public void startWithOptions (ReadableMap params) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, MotionViewsActivity.class);

            Bundle options = BundleConverter.toBundle(params);

            intent.putExtra(RNMotionViewModule.OPTIONS_ID, options);
            activity.startActivity(intent);
        }
    }


    @Override
    public String getName() {
        return "RNMotionViews";
    }
}



/**
 *  TODO LIST init params:
 *
 * - easily font
 * - images for buttons: trash, save, clear, cancel, pen, circle, arrow, erase, textEntity, (imageEntity?), sketchEntity
 * - background image
 * - save path
 * - ("back to easily" button callback) ?
 * - config for shown settings/ entity buttons, colors + color options + initial color, appTheme, min/max font size/stroke thickness, placeholder text (textEntity), default opened "create entity action" (e.g. sketchView)
 * - button/ label text
 *
 */