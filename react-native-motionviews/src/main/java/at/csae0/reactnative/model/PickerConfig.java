package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.CONFIG_TYPE;

public class PickerConfig extends Config {

    private Integer initialColor;
    private String icon, pickerLabel, cancelText, submitText;

    public PickerConfig (@Nullable Boolean enabled, @Nullable String icon, @Nullable String pickerLabel, @Nullable String cancelText, @Nullable String submitText) {
        super(CONFIG_TYPE.PICKER_CONFIG);
        if (enabled != null) {
            setEnabled(enabled);
        }

        this.icon = icon;
        this.pickerLabel = pickerLabel;
        this.cancelText = cancelText;
        this.submitText = submitText;
    }

    public Integer getInitialColor() {
        return initialColor;
    }

    public void setInitialColor(Integer initialColor) {
        this.initialColor = initialColor;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPickerLabel() {
        return pickerLabel;
    }

    public void setPickerLabel(String pickerLabel) {
        this.pickerLabel = pickerLabel;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public String getSubmitText() {
        return submitText;
    }

    public void setSubmitText(String submitText) {
        this.submitText = submitText;
    }

    public boolean hasInitialColor() {
        return initialColor != null;
    }
    public boolean hasIcon() {
        return icon != null;
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
