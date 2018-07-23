package at.csae0.reactnative.utils;

import android.support.annotation.Nullable;

import com.sketchView.tools.Blueprints.SketchTool;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum TOOL_TYPE {
    PEN_TOOL("penTool"),
    ERASER_TOOL("eraserTool"),
    CIRCLE_TOOL("circleTool"),
    ARROW_TOOL("arrowTool");

    private String name;

    private static final Map<String, TOOL_TYPE> ENUM_MAP;

    TOOL_TYPE(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Integer getInt() {
        switch (this) {
            case PEN_TOOL:
                return SketchTool.TYPE_PEN;
            case ERASER_TOOL:
                return SketchTool.TYPE_ERASE;
            case CIRCLE_TOOL:
                return SketchTool.TYPE_CIRCLE;
            case ARROW_TOOL:
                return SketchTool.TYPE_ARROW;
        }
        return null;
    }

    public Set<String> getKeySet() {
        return ENUM_MAP.keySet();
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, TOOL_TYPE> map = new ConcurrentHashMap<String, TOOL_TYPE>();
        for (TOOL_TYPE instance : TOOL_TYPE.values()) {
            map.put(instance.getName(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    @Nullable
    public static TOOL_TYPE get (String name) {
        TOOL_TYPE type = ENUM_MAP.get(name);
        return type;
    }
}