package at.csae0.reactnative.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.RessourceUtils;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public class ButtonConfig extends Config {

    private String baseIconName, optionalIconName;
    private String label;
    private String tint;
    private Integer imageButtonSideLength;
    private int[] padding = new int[]{0,0,0,0};

    public ButtonConfig (CONFIG_TYPE id, @Nullable Boolean enabled, @Nullable String[] icons, @Nullable String label, @Nullable String tint, @Nullable Integer sideLength, @Nullable int[] padding) {
        super(id, enabled);

        setIconNames(icons);
        setLabel(label);
        setTint(tint);
        setSideLength(sideLength);
        setPadding(padding);
    }

    @Nullable
    public String getIconName() {
        return getIconName(true);
    }

    @Nullable
    public String getIconName(boolean baseIcon) {
        if (baseIcon) {
            return baseIconName;
        } else {
            return optionalIconName;
        }
    }

    public int getPaddingLeft() {
        return padding[0];
    }
    public int getPaddingTop() {
        return padding[1];
    }
    public int getPaddingRight() {
        return padding[2];
    }
    public int getPaddingBottom() {
        return padding[3];
    }
    public int[] getPaddings() {
        return padding;
    }

    @Nullable
    public Integer getSideLength() {
        return imageButtonSideLength;
    }

    @Nullable
    public Drawable getIcon() {
        return getIcon(true);
    }

    @Nullable
    public Drawable getIcon(boolean baseIcon) {
        if (getIconName(baseIcon) != null) {
            String[] icon = getIconName(baseIcon).split("\\.");
            if (icon.length == 2) {
                return RessourceUtils.getImageAsset(icon[0], icon[1]);
            } else {
                return RessourceUtils.getImageRessource(icon[0]);
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

    public void setIconNames(@Nullable String[] icons) {
        if (icons != null && icons.length > 0) {
            setIconName(icons[0], true);
            if (icons.length > 1) {
                setIconName(icons[1], false);
            }
        }
    }

    public void setIconName(@Nullable String icon) {
        setIconName(icon, true);
    }
    public void setIconName(@Nullable String icon, boolean baseIcon) {
        if (baseIcon) {
            this.baseIconName = icon;
        } else {
            this.optionalIconName = icon;
        }
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

    public void setPadding(@Nullable int[] paddings) {
        if (paddings != null && paddings.length == 4) {
            for (int i = 0; i < paddings.length; i++) {
                if (paddings[i] > 0) {
                    this.padding[i] = paddings[i];
                } else {
                    this.padding[i] = 0;
                }
            }
        }
    }

    public void setSideLength(@Nullable Integer sideLength) {
        if (sideLength != null && sideLength > 0) {
            imageButtonSideLength = sideLength;
        } else {
            imageButtonSideLength = null;
        }
    }

    public boolean hasIcon() {
        return hasIcon(true);
    }
    public boolean hasIcon(boolean baseIcon) {
        if (getIconName(baseIcon) != null) {
            String[] icon = getIconName(baseIcon).split("\\.");
            if (icon.length == 2) {
                return RessourceUtils.getImageAsset(icon[0], icon[1]) != null;
            } else {
                return RessourceUtils.getImageRessource(icon[0]) != null;
            }
        }
        return false;
    }

    public boolean hasSideLength() {
        return imageButtonSideLength != null;
    }
    public boolean hasLabel() {
        return label != null;
    }
    public boolean hasTint() {
        return tint != null;
    }
}

