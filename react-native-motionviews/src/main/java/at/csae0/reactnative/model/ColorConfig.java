package at.csae0.reactnative.model;

import android.graphics.Color;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import at.csae0.reactnative.utils.CONFIG_TYPE;

public class ColorConfig extends ScreenConfig {

    private Integer initialColor;
    private ArrayList<String> colors;
    private PickerConfig pickerconfig;

    public ColorConfig (@Nullable Boolean enabled, @Nullable String screenType, @Nullable String initialColor, @Nullable ArrayList<String> colors, @Nullable PickerConfig pickerConfig) {
        super(CONFIG_TYPE.COLOR_CONFIG, enabled, screenType);

        String s = initialColor;
        setInitialColor(s);

        setColors(colors);

        setPickerconfig(pickerConfig);
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

    @Nullable
    public PickerConfig getPickerconfig() {
        return pickerconfig;
    }

    public void setPickerconfig(@Nullable PickerConfig pickerconfig) {
        this.pickerconfig = pickerconfig;
    }

    @Nullable
    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(@Nullable ArrayList<String> colors) {
        if (colors != null) {
            ArrayList<String> newColors = new ArrayList<>();
            for (String color: colors) {
                if (color != null) {
                    newColors.add(color);
                }
            }
            this.colors = newColors;
        }
    }

    public boolean hasInitialColor() {
        return initialColor != null;
    }
    public boolean hasColors() {
        return colors != null;
    }
    public boolean hasPickerConfig() {
        return pickerconfig != null;
    }
}
