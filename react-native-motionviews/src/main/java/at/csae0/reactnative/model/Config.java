package at.csae0.reactnative.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Config {

    private TYPE id;
    private boolean enabled;

    public Config (TYPE id) {
        this(id, true);
    }
    public Config (TYPE id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public TYPE getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
