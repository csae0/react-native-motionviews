package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

public class ButtonConfig extends Config {

    private String icon;
    private String label;
    private String tint;

    public ButtonConfig (TYPE id, @Nullable Boolean enabled, @Nullable String icon, @Nullable String label, @Nullable String tint) {
        super(id);
        if (enabled != null) {
            setEnabled(enabled);
        }
        this.icon = icon;
        this.label = label;
        this.tint = tint;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTint() {
        return tint;
    }

    public void setTint(String tint) {
        this.tint = tint;
    }

    public boolean hasIcon() {
        return icon != null;
    }
    public boolean hasLabel() {
        return label != null;
    }
    public boolean hasTint() {
        return tint != null;
    }
}

