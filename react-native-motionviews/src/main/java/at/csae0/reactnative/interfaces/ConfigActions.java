package at.csae0.reactnative.interfaces;

import java.util.ArrayList;

import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.SizeConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public interface ConfigActions {
    CONFIG_TYPE COLOR = CONFIG_TYPE.COLOR_CONFIG;
    CONFIG_TYPE SIZE = CONFIG_TYPE.SIZE_CONFIG;

    void applyGeneralConfig(GeneralConfig config);
    void applyColorConfig(ConfigManagerActions manager);
    void applySizeConfig(ConfigManagerActions manager);
    void applyButtonConfigs(ArrayList<ButtonConfig> configs);
}
