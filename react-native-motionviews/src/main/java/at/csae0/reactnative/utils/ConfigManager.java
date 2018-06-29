package at.csae0.reactnative.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.PickerConfig;
import at.csae0.reactnative.model.ScreenConfig;
import at.csae0.reactnative.model.SizeConfig;
import team.uptech.motionviews.utils.RessourceUtils;

public class ConfigManager implements ConfigManagerActions {
    private static ConfigManager instance = null;

    private GeneralConfig generalConfig;
    private ArrayList<ButtonConfig> buttonConfigs;
    private ArrayList<ColorConfig> colorConfigs;
    private ArrayList<SizeConfig> sizeConfigs;

    public ConfigManager (Bundle bundle) {
        generalConfig = null;
        buttonConfigs = new ArrayList<>();
        colorConfigs = new ArrayList<>();
        sizeConfigs = new ArrayList<>();
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
        configActions.applyButtonConfigs(buttonConfigs); // needs to be called before colorConfig
        configActions.applyColorConfig(this);
        configActions.applySizeConfig(this);
        configActions.applyGeneralConfig(generalConfig);
    }

    private void init (Bundle bundle) {
        for (String key : bundle.keySet()) {
            Bundle tempBundle = bundle.getBundle(key);

            if (CONFIG_TYPE.get(key) == CONFIG_TYPE.GENERAL_CONFIG) { // GENERAL CONFIG
                if (tempBundle != null) {
                    generalConfig = new GeneralConfig(
                            tempBundle.getString("backgroundImage", null),
                            tempBundle.getString("imageSaveName", null),
                            tempBundle.getString("fontFamily", null),
                            tempBundle.getString("initialToolSelection", null),
                            tempBundle.getString("initialText", null)
                    );
                } else {
                    generalConfig = new GeneralConfig(
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.SIZE_CONFIG) { //SIZE CONFIG
                if (tempBundle != null) {
                    ArrayList<Bundle> sizeConfigBundlesArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(tempBundle);
                    for (Bundle sizeConfigBundle : sizeConfigBundlesArray) {
                        SizeConfig sizeConfig = null;
                        if (sizeConfigBundle != null) {
                            sizeConfig = new SizeConfig(
                                    sizeConfigBundle.getBoolean("enabled", true),
                                    sizeConfigBundle.getString("screen", null),
                                    sizeConfigBundle.getString("backgroundColor", null),
                                    sizeConfigBundle.getString("progressColor", null),
                                    sizeConfigBundle.getInt("min", 0),
                                    sizeConfigBundle.getInt("max", 100),
                                    sizeConfigBundle.getInt("initialValue", 50),
                                    sizeConfigBundle.getInt("step", 1)
                            );
                        } else {
                            sizeConfig = new SizeConfig(
                                    false,
                                    null,
                                    null,
                                    null,
                                    0,
                                    100,
                                    50,
                                    1
                            );
                        }
                        if (sizeConfig != null) {
                            sizeConfigs.add(sizeConfig);
                        }
                    }
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.COLOR_CONFIG) { // COLOR CONFIG
                if (tempBundle != null) {
                    ArrayList<Bundle> colorConfigBundlesArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(tempBundle);
                    for (Bundle colorConfigBundle : colorConfigBundlesArray) {
                        ColorConfig colorConfig = null;

                        if (colorConfigBundle != null) {
                            PickerConfig pickerConfig = null;
                            Bundle pickerConfigBundle = tempBundle.getBundle("pickerConfig");
                            if (pickerConfigBundle != null) {
                                pickerConfig = new PickerConfig(
                                        pickerConfigBundle.getBoolean("enabled", true),
                                        buildIconNameFromBundle(pickerConfigBundle.getBundle("icon")),
                                        pickerConfigBundle.getString("pickerLabel", null),
                                        pickerConfigBundle.getString("submitText", null),
                                        pickerConfigBundle.getString("cancelText", null),
                                        tempBundle.getString("initialColor", "#FFFFFF")

                                );
                            } else {
                                pickerConfig = new PickerConfig(
                                        false,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                );
                            }
                            colorConfig = new ColorConfig(
                                    tempBundle.getBoolean("enabled", true),
                                    tempBundle.getString("screen", null),
                                    tempBundle.getString("initialColor", "#FFFFFF"),
                                    (ArrayList<String>) BundleConverter.bundleToArrayList(tempBundle.getBundle("colors")),
                                    pickerConfig
                            );
                        } else {
                            colorConfig = new ColorConfig(
                                    false,
                                    null,
                                    "#FFFFFF",
                                    null,
                                    new PickerConfig(
                                            false,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                    )
                            );
                        }

                        if (colorConfig != null) {
                            colorConfigs.add(colorConfig);
                        }
                    }
                }
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.BUTTONS_CONFIG) { // BUTTON CONFIGS
                if (tempBundle != null) {
                    ArrayList<Bundle> buttonBundlesArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(tempBundle);
                    for (Bundle buttonBundle : buttonBundlesArray) {
                        ButtonConfig buttonConfig = null;
                        if (buttonBundle != null) {
                            buttonConfig = new ButtonConfig(
                                    CONFIG_TYPE.get(buttonBundle.getString("id")),
                                    buttonBundle.getBoolean("enabled", true),
                                    buildIconNameFromBundle(buttonBundle.getBundle("icon")),
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
                        if (buttonConfig != null) {
                            buttonConfigs.add(buttonConfig);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    private String buildIconNameFromBundle(Bundle icon) {
        String iconString = null;
        if (icon != null) {

            String iconName = icon.getString("name", null);
            if (iconName != null) {
                iconString = RessourceUtils.rename(iconName);
            }

            String iconExtension = icon.getString("extension", null);
            if (iconName != null && iconExtension != null) {
                iconString += "." + iconExtension;
            }
        }
        return iconString;
    }

    @Override
    @Nullable
    public ScreenConfig getScreenConfig(CONFIG_TYPE screenType, CONFIG_TYPE configType) {
        ScreenConfig config = null;
        ArrayList<? extends ScreenConfig> configs = null;

        switch (configType) {
            case COLOR_CONFIG:
                configs = colorConfigs;
                break;
            case SIZE_CONFIG:
                configs = sizeConfigs;
                break;
        }

        if (configs != null) {
            for (ScreenConfig tempConfig : configs) {
                if (tempConfig.isScreenType(screenType)) {
                    config = tempConfig;
                }
            }
        }

        return config;
    }
}
