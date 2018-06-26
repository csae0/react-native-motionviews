package at.csae0.reactnative.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum TYPE {
    // general
    GENERAL_CONFIG("generalConfig"),
    // settings
    COLOR_CONFIG("colorConfig"),
    PICKER_CONFIG("pickerConfig"),
    SIZE_CONFIG("sizeConfig"),

    // BUTTONS
    // motionView main actions
    CREATE_TEXT_CONFIG("createTextConfig"),
    CREATE_SKETCH_CONFIG("createSketchConfig"),
    CREATE_STICKER_CONFIG("createStickerConfig"),
    BACK_BUTTON_CONFIG("backButtonConfig"),
    // sketchView main actions
    SAVE_BUTTON_CONFIG("saveButtonConfig"),
    CLEAR_BUTTON_CONFIG("clearButtonConfig"),
    CANCEL_BUTTON_CONFIG("cancelButtonConfig"),
    // sketch tools
    PEN_TOOL_CONFIG("penToolConfig"),
    ERASE_TOOL_CONFIG("eraseToolConfig"),
    CIRCLE_TOOL_CONFIG("circleToolConfig"),
    ARROW_TOOL_CONFIG("arrowToolConfig");

    private String name;

    private static final Map<String, TYPE> ENUM_MAP;

    TYPE (String name) {
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
        Map<String, TYPE> map = new ConcurrentHashMap<String, TYPE>();
        for (TYPE instance : TYPE.values()) {
            map.put(instance.getName(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static TYPE get (String name) {
        return ENUM_MAP.get(name);
    }
}