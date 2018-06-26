package at.csae0.reactnative.interfaces;

import java.util.ArrayList;

import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.SizeConfig;

public interface ConfigActions {
    void applyGeneralConfig(GeneralConfig config);
    void applyColorConfig(ColorConfig config);
    void applySizeConfig(SizeConfig config);
    void applyButtonConfigs(ArrayList<ButtonConfig> buttonConfigs);
}
