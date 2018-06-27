package at.csae0.reactnative.model;

import team.uptech.motionviews.utils.CONFIG_TYPE;

public abstract class Config {

    private CONFIG_TYPE id;
    private Boolean enabled;

    public Config (CONFIG_TYPE id) {
        this(id, true);
    }
    public Config (CONFIG_TYPE id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public CONFIG_TYPE getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasEnabled() {
        return enabled != null;
    }
}
