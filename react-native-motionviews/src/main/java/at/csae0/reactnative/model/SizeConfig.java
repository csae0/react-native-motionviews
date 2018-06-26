package at.csae0.reactnative.model;

import android.graphics.Color;
import android.support.annotation.Nullable;

public class SizeConfig extends Config {

    private Integer backgroundColor, progressColor, min, max, initialValue, step;

    public SizeConfig (@Nullable Boolean enabled, @Nullable String backgroundColor, @Nullable String progressColor, @Nullable Integer min, @Nullable Integer max, @Nullable Integer initialValue, @Nullable Integer step) {
        super(TYPE.SIZE_CONFIG);
        if (enabled != null) {
            setEnabled(enabled);
        }
        this.backgroundColor = backgroundColor != null ? Color.parseColor(backgroundColor) : null;
        this.progressColor = progressColor != null ? Color.parseColor(progressColor) : null;
        this.min = min;
        this.max = max;
        this.initialValue = initialValue;
        this.step = step;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(Integer progressColor) {
        this.progressColor = progressColor;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public boolean hasBackgroundColor() {
        return backgroundColor != null;
    }
    public boolean hasProgrssColor() {
        return progressColor != null;
    }
    public boolean hasMin() {
        return min != null;
    }
    public boolean hasMax() {
        return max != null;
    }
    public boolean hasStep() {
        return step != null;
    }
    public boolean hasinitialValue() {
        return initialValue != null;
    }
}
