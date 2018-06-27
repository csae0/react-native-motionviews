package at.csae0.reactnative.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.PickerConfig;
import at.csae0.reactnative.model.SizeConfig;
import team.uptech.motionviews.utils.CONFIG_TYPE;

public class ConfigManager {

    private static ConfigManager instance = null;

    private GeneralConfig generalConfig = null;
    private ColorConfig colorConfig = null;
    private SizeConfig sizeConfig = null;
    private ArrayList<ButtonConfig> buttonConfigs;

    public ConfigManager (Bundle bundle) {
        buttonConfigs = new ArrayList<>();
        init(bundle);
    }

    @Nullable
    public static ConfigManager getInstance () {
        return instance;
    }

    public static void create (@Nullable Bundle bundle) {
        if (bundle != null) {
            instance = new ConfigManager(bundle);
        }
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void reset() {
        instance = null;
    }

    public void apply (ConfigActions configActions) {
        configActions.applyGeneralConfig(generalConfig);
        configActions.applyColorConfig(colorConfig);
        configActions.applySizeConfig(sizeConfig);
        configActions.applyButtonConfigs(buttonConfigs);
    }

    private void init (Bundle bundle) {
        for (String key : bundle.keySet()) {
            Bundle tempBundle = bundle.getBundle(key);

            if (CONFIG_TYPE.get(key) == CONFIG_TYPE.GENERAL_CONFIG) { // GENERAL CONFIG
                if (tempBundle != null) {
                    generalConfig = new GeneralConfig(
                            tempBundle.getString("backgroundImage", null),
                            tempBundle.getString("imageSaveName", null),
                            tempBundle.getString("fontFamily", null)
                    );
                } else {
                    generalConfig = new GeneralConfig(
                            null,
                            null,
                            null
                    );
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.SIZE_CONFIG) { //SIZE CONFIG
                if (tempBundle != null) {
                    sizeConfig = new SizeConfig(
                            tempBundle.getBoolean("enabled", true),
                            tempBundle.getString("backgroundColor", null),
                            tempBundle.getString("progressColor", null),
                            tempBundle.getInt("min", 0),
                            tempBundle.getInt("max", 100),
                            tempBundle.getInt("initialValue", 50),
                            tempBundle.getInt("step", 1)
                    );
                } else {
                    sizeConfig = new SizeConfig(
                            false,
                            null,
                            null,
                            0,
                            100,
                            50,
                            1
                    );
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.COLOR_CONFIG) { // COLOR CONFIG
                if (tempBundle != null) {
                    PickerConfig pickerConfig;
                    Bundle pickerConfigBundle = tempBundle.getBundle("pickerConfig");
                    if (pickerConfigBundle != null) {
                        pickerConfig = new PickerConfig(
                                pickerConfigBundle.getBoolean("enabled", true),
                                pickerConfigBundle.getString("icon", null),
                                pickerConfigBundle.getString("pickerLabel", null),
                                pickerConfigBundle.getString("submitText", null),
                                pickerConfigBundle.getString("cancelText", null)
                        );
                    } else {
                        pickerConfig = new PickerConfig(
                                false,
                                null,
                                null,
                                null,
                                null
                        );
                    }
                    colorConfig = new ColorConfig(
                            tempBundle.getBoolean("enabled", true),
                            tempBundle.getString("initialColor", "#FFFFFF"),
                            (ArrayList<String>) BundleConverter.bundleToArrayList(tempBundle.getBundle("colors")),
                            pickerConfig
                    );
                } else {
                    colorConfig = new ColorConfig(
                            false,
                            "#FFFFFF",
                            null,
                            new PickerConfig(
                                    false,
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.BUTTONS_CONFIG) { // BUTTON CONFIGS
                ArrayList<Bundle> buttonBundlesArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(tempBundle);
                for (Bundle buttonBundle : buttonBundlesArray) {
                    ButtonConfig buttonConfig;
                    if (buttonBundle != null) {
                        buttonConfig = new ButtonConfig(
                                CONFIG_TYPE.get(buttonBundle.getString("id")),
                                buttonBundle.getBoolean("enabled", true),
                                buttonBundle.getString("icon", null),
                                buttonBundle.getString("label", null),
                                buttonBundle.getString("tint", null)
                        );
                    } else {
                        buttonConfig = new ButtonConfig(
                                CONFIG_TYPE.get(key),
                                false,
                                null,
                                null,
                                null
                        );
                    }
                    buttonConfigs.add(buttonConfig);
                }
            }
        }
    }
}
