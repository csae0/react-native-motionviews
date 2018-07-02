package at.csae0.reactnative.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
import at.csae0.reactnative.interfaces.MergeConfig;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ButtonConfigs;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.Config;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.PickerConfig;
import at.csae0.reactnative.model.ScreenConfig;
import at.csae0.reactnative.model.SizeConfig;
import team.uptech.motionviews.utils.RessourceUtils;

public class ConfigManager implements ConfigManagerActions {
    private static ConfigManager instance = null;

    private GeneralConfig generalConfig;
    private ArrayList<ButtonConfigs> buttonConfigs;
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
        configActions.applyButtonConfigs(this); // needs to be called before colorConfig
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
                            Bundle pickerConfigBundle = colorConfigBundle.getBundle("pickerConfig");
                            if (pickerConfigBundle != null) {
                                pickerConfig = new PickerConfig(
                                        pickerConfigBundle.getBoolean("enabled", true),
                                        buildIconNameFromBundle(pickerConfigBundle.getBundle("icon")),
                                        pickerConfigBundle.getString("pickerLabel", null),
                                        pickerConfigBundle.getString("submitText", null),
                                        pickerConfigBundle.getString("cancelText", null),
                                        colorConfigBundle.getString("initialColor", "#FFFFFF")

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
                                    colorConfigBundle.getBoolean("enabled", true),
                                    colorConfigBundle.getString("screen", null),
                                    colorConfigBundle.getString("initialColor", "#FFFFFF"),
                                    (ArrayList<String>) BundleConverter.bundleToArrayList(colorConfigBundle.getBundle("colors")),
                                    pickerConfig
                            );
                        } else { //TODO: do not create config if null/ not existing pass null instead of "empty" PickerConfig
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
            } else if (CONFIG_TYPE.get(key) == CONFIG_TYPE.BUTTON_CONFIGS) { // BUTTON CONFIGS
                if (tempBundle != null) {
                    ArrayList<Bundle> buttonBundlesArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(tempBundle);
                    for (Bundle buttonBundles : buttonBundlesArray) {
                        ButtonConfigs buttonConfig = null;
                        if (buttonBundles != null) {
                            Bundle buttonsConfigBundle = buttonBundles.getBundle("configs");
                            if (buttonsConfigBundle != null) {
                                ArrayList<Bundle> buttonsBundleArray = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(buttonsConfigBundle);
                                if (buttonsBundleArray != null) {
                                    ArrayList<ButtonConfig> buttonsConfigArray = new ArrayList<>();
                                    for (Bundle buttonsBundle : buttonsBundleArray) {
                                        ButtonConfig buttonsConfig = null;
                                        if (buttonsBundle != null) {
                                            buttonsConfig = new ButtonConfig(
                                                    CONFIG_TYPE.get(buttonsBundle.getString("id")),
                                                    buttonsBundle.getBoolean("enabled", true),
                                                    buildIconNameFromBundle(buttonsBundle.getBundle("icon")),
                                                    buttonsBundle.getString("label", null),
                                                    buttonsBundle.getString("tint", null)
                                            );
                                        } else {
                                            buttonsConfig = new ButtonConfig(
                                                    CONFIG_TYPE.get(key),
                                                    false,
                                                    null,
                                                    null,
                                                    null
                                            );
                                        }
                                        if (buttonsConfig != null) {
                                            buttonsConfigArray.add(buttonsConfig);
                                        }
                                    }
                                    if (buttonsConfigArray != null && buttonsConfigArray.size() > 0) {
                                        buttonConfig = new ButtonConfigs(
                                                buttonBundles.getString("screen", null),
                                                buttonsConfigArray
                                        );
                                    }
                                }
                            }
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
            case BUTTON_CONFIGS:
                configs = buttonConfigs;
                break;
        }

        if (configs != null) {
            for (ScreenConfig tempConfig : configs) {
                if (tempConfig.isScreenType(screenType)) {
                    if (config == null) {
                        config = tempConfig;
                    } else {
                        if (config instanceof MergeConfig) {
                            config = (ScreenConfig) ((MergeConfig) config).mergeConfig(tempConfig);
                        } else {
                            config = tempConfig;
                        }
                    }
                }
            }
        }

        return config;
    }
}
