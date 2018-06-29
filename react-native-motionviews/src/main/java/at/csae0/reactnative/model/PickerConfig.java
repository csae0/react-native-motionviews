package at.csae0.reactnative.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import at.csae0.reactnative.utils.CONFIG_TYPE;
import team.uptech.motionviews.utils.RessourceUtils;

public class PickerConfig extends Config {

    private Integer initialColor;
    private String iconName, pickerLabel, cancelText, submitText;

    public PickerConfig (@Nullable Boolean enabled, @Nullable String iconName, @Nullable String pickerLabel, @Nullable String submitText, @Nullable String cancelText, @Nullable String initialColor) {
        super(CONFIG_TYPE.PICKER_CONFIG, enabled);

        seticonName(iconName);
        setPickerLabel(pickerLabel);
        setCancelText(cancelText);
        setSubmitText(submitText);
        String s = initialColor;
        setInitialColor(s);
    }

    @Nullable
    public Drawable geticon() {
        if (this.iconName != null) {
            String[] iconNameArray = this.iconName.split("\\.");
            if (iconNameArray.length == 2) {
                return RessourceUtils.getImageAsset(iconNameArray[0], iconNameArray[1]);
            }
        }
        return null;
    }

    @Nullable
    public String getPickerLabel() {
        return pickerLabel;
    }
    @Nullable
    public String getCancelText() {
        return cancelText;
    }
    @Nullable
    public String getSubmitText() {
        return submitText;
    }
    @Nullable
    public Integer getInitialColor() {
        return initialColor;
    }

    public void setInitialColor(@Nullable Integer initialColor) {
        this.initialColor = initialColor != null ? initialColor : null;
    }

    public void setInitialColor(@Nullable String initialColor) {
        this.initialColor = initialColor != null ? Color.parseColor(initialColor) : null;
    }


    public void seticonName(@Nullable String iconName) {
        this.iconName = iconName;
    }

    public void setPickerLabel(@Nullable String pickerLabel) {
        this.pickerLabel = pickerLabel;
    }

    public void setCancelText(@Nullable String cancelText) {
        this.cancelText = cancelText;
    }


    public void setSubmitText(@Nullable String submitText) {
        this.submitText = submitText;
    }

    public boolean hasInitialColor() {
        return initialColor != null;
    }
    public boolean hasicon() {
        if (this.iconName != null) {
            String[] iconNameArray = this.iconName.split("\\.");
            if (iconNameArray.length == 2) {
                return RessourceUtils.getImageAsset(iconNameArray[0], iconNameArray[1]) != null;
            }
        }
        return false;
    }

    public boolean hasPickerLabel() {
        return pickerLabel != null;
    }
    public boolean hasCancelText() {
        return cancelText != null;
    }
    public boolean hasSubmitText() {
        return submitText != null;
    }
}
