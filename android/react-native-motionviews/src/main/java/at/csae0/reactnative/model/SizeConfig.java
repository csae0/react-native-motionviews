package at.csae0.reactnative.model;

import android.graphics.Color;
import android.support.annotation.Nullable;

import at.csae0.reactnative.utils.CONFIG_TYPE;

public class SizeConfig extends ScreenConfig {

    private Integer backgroundColor, progressColor, min, max, initialValue, step;

    public SizeConfig (@Nullable Boolean enabled, @Nullable String screenType, @Nullable String backgroundColor, @Nullable String progressColor, @Nullable Integer min, @Nullable Integer max, @Nullable Integer initialValue, @Nullable Integer step) {
        super(CONFIG_TYPE.SIZE_CONFIG, enabled, screenType);

        String c = backgroundColor;
        setBackgroundColor(c);

        c = progressColor;
        setProgressColor(c);

        setMin(min);
        setMax(max);
        setInitialValue(initialValue);
        setStep(step);
    }

    @Nullable
    public Integer getBackgroundColor() {
        return backgroundColor;
    }
    @Nullable
    public Integer getProgressColor() {
        return progressColor;
    }
    @Nullable
    public Integer getMin() {
        return min;
    }
    @Nullable
    public Integer getMax() {
        return max;
    }
    @Nullable
    public Integer getInitialValue() {
        return initialValue;
    }
    @Nullable
    public Integer getStep() {
        return step;
    }


    public void setBackgroundColor(@Nullable Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColor(@Nullable String backgroundColor) {
        this.backgroundColor = backgroundColor != null ? Color.parseColor(backgroundColor) : null;
    }

    public void setProgressColor(@Nullable Integer progressColor) {
        this.progressColor = progressColor;
    }
    public void setProgressColor(@Nullable String progressColor) {
        this.progressColor = progressColor != null ? Color.parseColor(progressColor) : null;
    }

    public void setMin(@Nullable Integer min) {
        this.min = min;
    }

    public void setMax(@Nullable Integer max) {
        this.max = max;
    }

    public void setInitialValue(@Nullable Integer initialValue) {
        this.initialValue = initialValue;
    }

    public void setStep(@Nullable Integer step) {
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
    public boolean hasInitialValue() {
        return initialValue != null;
    }
}
