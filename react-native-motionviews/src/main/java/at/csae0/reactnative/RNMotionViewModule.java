package at.csae0.reactnative;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import team.uptech.motionviews.ui.MainActivity;

public class RNMotionViewModule extends ReactContextBaseJavaModule {

    public RNMotionViewModule(ReactApplicationContext reactContext) {
        super(reactContext);

    }

    @ReactMethod
    public void navigateToExample() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }
    }

    @Override
    public String getName() {
        return "RNMotionView";
    }
}