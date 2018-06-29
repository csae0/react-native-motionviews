package at.csae0.reactnative.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import at.csae0.reactnative.utils.CONFIG_TYPE;

public enum RESSOURCE_TYPE {
    DRAWABLE("drawable"),
    FONT("font");

    private String name;

    private static final Map<String, CONFIG_TYPE> ENUM_MAP;

    RESSOURCE_TYPE(String name) {
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

    public static CONFIG_TYPE get (String name) {
        return ENUM_MAP.get(name);
    }
}
