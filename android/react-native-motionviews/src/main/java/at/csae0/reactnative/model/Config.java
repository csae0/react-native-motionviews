package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import at.csae0.reactnative.utils.CONFIG_TYPE;

public abstract class Config {
    private final CONFIG_TYPE id;
    private Boolean enabled;

    public Config (CONFIG_TYPE id, @Nullable Boolean enabled) {
        this.id = id;

        setEnabled(enabled);
    }

    public CONFIG_TYPE getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(@Nullable Boolean enabled) {
        if (enabled != null) {
            this.enabled = enabled;
        } else {
            this.enabled = true;
        }
    }

    public boolean hasEnabled() {
        return enabled != null;
    }
}
