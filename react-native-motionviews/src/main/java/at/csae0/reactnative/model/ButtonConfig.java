package at.csae0.reactnative.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.RessourceUtils;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public class ButtonConfig extends Config {

    private String iconName;
    private String label;
    private String tint;

    public ButtonConfig (CONFIG_TYPE id, @Nullable Boolean enabled, @Nullable String icon, @Nullable String label, @Nullable String tint) {
        super(id, enabled);

        setIconName(icon);
        setLabel(label);
        setTint(tint);
    }

    @Nullable
    public String getIconName() {
        return iconName;
    }

    @Nullable
    public Drawable getIcon() {
        if (this.iconName != null) {
            String[] icon = this.iconName.split("\\.");
            if (icon.length == 2) {
                return RessourceUtils.getImageAsset(icon[0], icon[1]);
            }
        }
        return null;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    @Nullable
    public Integer getTintColor() {
        if (tint != null) {
            return Color.parseColor(tint);
        }
        return null;
    }

    @Nullable
    public String getTint() {
        return tint;
    }

    public void setIconName(@Nullable String icon) {
        this.iconName = icon;
    }

    public void setLabel(@Nullable String label) {
        if (label != null && label.length() > 0) {
            this.label = label;
        } else {
            this.label = null;
        }
    }

    public void setTint(@Nullable String tint) {
        this.tint = tint;
    }

    public boolean hasIcon() {
        if (this.iconName != null) {
            String[] icon = this.iconName.split("\\.");
            if (icon.length == 2) {
                return RessourceUtils.getImageAsset(icon[0], icon[1]) != null;
            }
        }
        return false;
    }

    public boolean hasLabel() {
        return label != null;
    }
    public boolean hasTint() {
        return tint != null;
    }
}

