package at.csae0.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.csae0.reactnative.utils.BundleConverter;
import at.csae0.reactnative.utils.CONFIG_TYPE;
import at.csae0.reactnative.utils.TOOL_TYPE;
import team.uptech.motionviews.ui.MotionViewsActivity;

import static at.csae0.reactnative.utils.TOOL_TYPE.ARROW_TOOL;

public class RNMotionViewModule extends ReactContextBaseJavaModule {

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String STATUS_KEY = "status";
    private static final String EDITED_KEY = "edited";
    public static final String OPTIONS_ID = "OPTIONS";

    private static final String EVENT_CLEAR = "clearEvent";
    private static final String EVENT_DELETE = "deleteEvent";
    private static final String EVENT_CHECK_PERMISSION = "checkPermissionEvent";

    private static final String TOOLS = "tools";
    private static final String CONFIG = "config";
    private static final String SCREENS = "screens";
    private static final String EVENTS = "events";
    private static final String RESULTS = "results";

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

    @Override
    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put(TOOLS, getToolConstants());
                put(CONFIG, getSettingConstants());
                put(SCREENS, getScreenConstants());
                put(EVENTS, getEventConstants());
                put(RESULTS, getResultConstants());
            }

            private Map<String, Object> getToolConstants() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put(TOOL_TYPE.PEN_TOOL.getName(), TOOL_TYPE.PEN_TOOL.getName());
                        put(TOOL_TYPE.ERASER_TOOL.getName(), TOOL_TYPE.ERASER_TOOL.getName());
                        put(TOOL_TYPE.CIRCLE_TOOL.getName(), TOOL_TYPE.CIRCLE_TOOL.getName());
                        put(TOOL_TYPE.ARROW_TOOL.getName(), TOOL_TYPE.ARROW_TOOL.getName());
                    }
                });
            }

            private Map<String, Object> getSettingConstants() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        //CONFIG CATEGORIES
                        put(CONFIG_TYPE.GENERAL_CONFIG.getName(), CONFIG_TYPE.GENERAL_CONFIG.getName());
                        put(CONFIG_TYPE.COLOR_CONFIG.getName(), CONFIG_TYPE.COLOR_CONFIG.getName());
                        put(CONFIG_TYPE.PICKER_CONFIG.getName(), CONFIG_TYPE.PICKER_CONFIG.getName());
                        put(CONFIG_TYPE.SIZE_CONFIG.getName(), CONFIG_TYPE.SIZE_CONFIG.getName());
                        put(CONFIG_TYPE.BUTTON_CONFIGS.getName(), CONFIG_TYPE.BUTTON_CONFIGS.getName());

                        //CONFIG NAMES
                        put(CONFIG_TYPE.PEN_TOOL_CONFIG.getName(), CONFIG_TYPE.PEN_TOOL_CONFIG.getName());
                        put(CONFIG_TYPE.ERASE_TOOL_CONFIG.getName(), CONFIG_TYPE.ERASE_TOOL_CONFIG.getName());
                        put(CONFIG_TYPE.CIRCLE_TOOL_CONFIG.getName(), CONFIG_TYPE.CIRCLE_TOOL_CONFIG.getName());
                        put(CONFIG_TYPE.ARROW_TOOL_CONFIG.getName(), CONFIG_TYPE.ARROW_TOOL_CONFIG.getName());
                        put(CONFIG_TYPE.TRASH_BUTTON_CONFIG.getName(), CONFIG_TYPE.TRASH_BUTTON_CONFIG.getName());
                        put(CONFIG_TYPE.SAVE_BUTTON_CONFIG.getName(), CONFIG_TYPE.SAVE_BUTTON_CONFIG.getName());
                        put(CONFIG_TYPE.CLEAR_BUTTON_CONFIG.getName(), CONFIG_TYPE.CLEAR_BUTTON_CONFIG.getName());
                        put(CONFIG_TYPE.CANCEL_BUTTON_CONFIG.getName(), CONFIG_TYPE.CANCEL_BUTTON_CONFIG.getName());
                        put(CONFIG_TYPE.DELETE_BUTTON_CONFIG.getName(), CONFIG_TYPE.DELETE_BUTTON_CONFIG.getName());
                        put(CONFIG_TYPE.CREATE_TEXT_CONFIG.getName(), CONFIG_TYPE.CREATE_TEXT_CONFIG.getName());
                        put(CONFIG_TYPE.CREATE_SKETCH_CONFIG.getName(), CONFIG_TYPE.CREATE_SKETCH_CONFIG.getName());
                        put(CONFIG_TYPE.CREATE_STICKER_CONFIG.getName(), CONFIG_TYPE.CREATE_STICKER_CONFIG.getName());
                    }
                });
            }

            private Map<String, Object> getScreenConstants() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put(CONFIG_TYPE.ALL_SCREENS.getName(), CONFIG_TYPE.ALL_SCREENS.getName());
                        put(CONFIG_TYPE.MAIN_SCREEN.getName(), CONFIG_TYPE.MAIN_SCREEN.getName());
                        put(CONFIG_TYPE.TEXT_ENTITY_SCREEN.getName(), CONFIG_TYPE.TEXT_ENTITY_SCREEN.getName());
                        put(CONFIG_TYPE.IMAGE_ENTITY_SCREEN.getName(), CONFIG_TYPE.IMAGE_ENTITY_SCREEN.getName());
                        put(CONFIG_TYPE.SKETCH_ENTITY_SCREEN.getName(), CONFIG_TYPE.SKETCH_ENTITY_SCREEN.getName());
                    }
                });
            }

            private Map<String, Object> getEventConstants() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put(EVENT_DELETE, EVENT_DELETE);
                        put(EVENT_CHECK_PERMISSION, EVENT_CHECK_PERMISSION);
                        put(EVENT_CLEAR, EVENT_CLEAR);
                    }
                });
            }

            private Map<String, Object> getResultConstants() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put("submittedResult", MotionViewsActivity.RESULT_SUBMITTED);
                        put("deletedResult", MotionViewsActivity.RESULT_DELETED);
                        put("canceledResult", MotionViewsActivity.RESULT_CANCELED);
                        put("clearedResult", MotionViewsActivity.RESULT_CLEARED);
                    }
                });
            }
        });
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

    public void sendEvent (String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private class OnActivityResultListener extends BaseActivityEventListener {
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == MotionViewsActivity.START_MOTION_VIEW_REQUEST_CODE) {
                if (resultCode == MotionViewsActivity.RESULT_SUBMITTED || resultCode == MotionViewsActivity.RESULT_CLEARED) {
                    Bundle extras = data.getExtras();

                    Bundle resultImage = extras.getBundle(MotionViewsActivity.RESULT_IMAGE_KEY);
                    WritableMap writableMap = BundleConverter.sketchFileBundleToWritableMap(resultImage);
                    writableMap.putBoolean(EDITED_KEY, extras.getBoolean(MotionViewsActivity.RESULT_EDITED_KEY, false));
                    writableMap.putInt(STATUS_KEY, resultCode);

                    if (promise != null) {
                            promise.resolve(writableMap);
                    }
                    release();
                } else if (resultCode == MotionViewsActivity.RESULT_CANCELED || resultCode == MotionViewsActivity.RESULT_DELETED) {
                    if (promise != null) {
                        WritableMap writableMap = Arguments.createMap();
                        writableMap.putInt(STATUS_KEY, resultCode);
                        promise.resolve(writableMap);
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