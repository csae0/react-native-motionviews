package at.csae0.reactnative.utils;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum CONFIG_TYPE {
    // general
    GENERAL_CONFIG("generalConfig"),
    // settings
    COLOR_CONFIG("colorConfig"),
    PICKER_CONFIG("pickerConfig"),
    SIZE_CONFIG("sizeConfig"),

    // BUTTONS config key
    BUTTONS_CONFIG("buttonsConfig"),
    BUTTON_CONFIGS("buttonConfigs"),

    // general (sketchview, motionview)
    SAVE_BUTTON_CONFIG("saveButtonConfig"),
    CLEAR_BUTTON_CONFIG("clearButtonConfig"),
    CANCEL_BUTTON_CONFIG("cancelButtonConfig"),

    // motionView main actions
    CREATE_TEXT_CONFIG("createTextConfig"),
    CREATE_SKETCH_CONFIG("createSketchConfig"),
    CREATE_STICKER_CONFIG("createStickerConfig"),

    // sketch tools
    PEN_TOOL_CONFIG("penToolConfig"),
    ERASE_TOOL_CONFIG("eraseToolConfig"),
    CIRCLE_TOOL_CONFIG("circleToolConfig"),
    ARROW_TOOL_CONFIG("arrowToolConfig"),

    // screen types
    ALL_SCREENS("allScreens"),
    MAIN_SCREEN("mainScreen"),
    TEXT_ENTITY_SCREEN("textEntityScreen"),
    IMAGE_ENTITY_SCREEN("imageEntityScreen"),
    SKETCH_ENTITY_SCREEN("sketchEntityScreen");

    private String name;

    private static final Map<String, CONFIG_TYPE> ENUM_MAP;

    CONFIG_TYPE(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Set<String> getKeySet() {
        return ENUM_MAP.keySet();
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, CONFIG_TYPE> map = new ConcurrentHashMap<String, CONFIG_TYPE>();
        for (CONFIG_TYPE instance : CONFIG_TYPE.values()) {
            map.put(instance.getName(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    @Nullable
    public static CONFIG_TYPE get (String name) {
        CONFIG_TYPE type = ENUM_MAP.get(name);
        return type;
    }
}