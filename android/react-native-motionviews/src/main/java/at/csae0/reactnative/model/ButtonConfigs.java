package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import at.csae0.reactnative.interfaces.MergeConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public class ButtonConfigs extends ScreenConfig implements MergeConfig{
    private ArrayList<ButtonConfig> buttonsConfig;

    public ButtonConfigs(@Nullable String screenType, @Nullable ArrayList<ButtonConfig> buttonsConfig) {
        super(CONFIG_TYPE.BUTTON_CONFIGS, true, screenType);
        this.buttonsConfig = buttonsConfig;
    }

    @Nullable
    public ArrayList<ButtonConfig> getButtonsConfig() {
        return buttonsConfig;
    }

    public void setButtonsConfig(@Nullable ArrayList<ButtonConfig> buttonsConfig) {
        this.buttonsConfig = buttonsConfig;
    }

    @Override
    @Nullable
    public Config mergeConfig(@Nullable Config buttonsConfigs) {
        ArrayList<ButtonConfig> buttonsConfig = ((ButtonConfigs)buttonsConfigs).getButtonsConfig();
        ArrayList<ButtonConfig> mergedButtonsConfig = new ArrayList<>();

        if ((this.buttonsConfig == null || this.buttonsConfig.isEmpty()) && (buttonsConfig == null || buttonsConfig.isEmpty())) {
            mergedButtonsConfig = null;
        } else if (this.buttonsConfig != null && !this.buttonsConfig.isEmpty()) {
            mergedButtonsConfig.addAll(this.buttonsConfig);
            for (ButtonConfig newButtonConfig : buttonsConfig) {
                boolean replaced = false;
                for (int i = 0; i < this.buttonsConfig.size(); i++) {
                    ButtonConfig oldButtonConfig = this.buttonsConfig.get(i);
                    if (oldButtonConfig != null && newButtonConfig != null && newButtonConfig.getId() == oldButtonConfig.getId()) {
                        mergedButtonsConfig.set(i, newButtonConfig);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    mergedButtonsConfig.add(newButtonConfig);
                }
            }
        } else if (buttonsConfig != null && !buttonsConfig.isEmpty()) {
            mergedButtonsConfig.addAll(buttonsConfig);
        }

        return new ButtonConfigs(getScreenType().getName(), mergedButtonsConfig);
    }

    public boolean hasButtonsConfig() {
        return buttonsConfig != null;
    }

}

