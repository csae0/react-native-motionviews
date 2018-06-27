package at.csae0.reactnative.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.RessourceUtils;
import team.uptech.motionviews.utils.CONFIG_TYPE;

public class ButtonConfig extends Config {

    private String icon;
    private String label;
    private String tint;

    public ButtonConfig (CONFIG_TYPE id, @Nullable Boolean enabled, @Nullable String icon, @Nullable String label, @Nullable String tint) {
        super(id);
        if (enabled != null) {
            setEnabled(enabled);
        }
        this.icon = icon;
        this.label = label;
        this.tint = tint;
    }

    public String getIconName() {
        return icon;
    }

    public void setIconName(String icon) {
        this.icon = icon;
    }

    @Nullable
    public Drawable getIcon() {
        Drawable drawable = RessourceUtils.getImageRessource(this.icon);
        return drawable;
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

