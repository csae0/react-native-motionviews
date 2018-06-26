package at.csae0.reactnative.model;

import android.graphics.Color;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ColorConfig extends Config {

    private Integer initialColor;
    private ArrayList<Integer> colors;
    private PickerConfig pickerconfig;

    public ColorConfig (@Nullable Boolean enabled, @Nullable String initialColor, @Nullable ArrayList<String> colors, @Nullable PickerConfig pickerConfig) {
        super(TYPE.COLOR_CONFIG);
        if (enabled != null) {
            setEnabled(enabled);
        }
        this.initialColor = initialColor != null ? Color.parseColor(initialColor) : null;
        this.colors = new ArrayList<>();
        if (colors != null) {
            for (String color: colors) {
                if (color != null) {
                    this.colors.add(Color.parseColor(color));
                }
            }
        }
        this.pickerconfig = pickerConfig;
    }

    public Integer getInitialColor() {
        return initialColor;
    }

    public void setInitialColor(Integer initialColor) {
        this.initialColor = initialColor;
    }

    public PickerConfig getPickerconfig() {
        return pickerconfig;
    }

    public void setPickerconfig(PickerConfig pickerconfig) {
        this.pickerconfig = pickerconfig;
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
