package at.csae0.reactnative.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
import at.csae0.reactnative.interfaces.MergeConfig;
import at.csae0.reactnative.interfaces.SetSizeAction;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ButtonConfigs;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.DeleteDialog;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.PickerConfig;
import at.csae0.reactnative.model.SavePermissionDialog;
import at.csae0.reactnative.model.ScreenConfig;
import at.csae0.reactnative.model.SizeConfig;
import team.uptech.motionviews.utils.RessourceUtils;
import team.uptech.motionviews.utils.UIUtils;

/**
 * EXAMPLE CONFIG
 *
 const defaultOptions = {
 generalConfig: {
 backgroundImage: 'path to image',
 imageSaveName: 'path where image is saved to / image name ?',
 fontFamily: 'pero_regular.otf',
 initialToolSelection: 'penTool',
 initialText: ''
 },
 buttonConfigs: [{
 screen: 'allScreens',
 configs: [
 {
 id: 'cancelButtonConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }, {
 id: 'clearButtonConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }, {
 id: 'saveButtonConfig',
 enabled: true,
 icon: {},
 label: null,
 tint: '#000000'
 }, {
 id: 'penToolConfig',
 enabled: true,
 icon: {},
 label: null,
 tint: '#000000'
 }, {
 id: 'eraseToolConfig',
 enabled: true,
 icon: {},
 label: null,
 tint: '#000000'
 }, {
 id: 'circleToolConfig',
 enabled: true,
 icon: {},
 label: null,
 tint: '#000000'
 }, {
 id: 'arrowToolConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }, {
 id: 'createTextConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }, {
 id: 'createSketchConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }, {
 id: 'createStickerConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#000000'
 }
 ]
 }, {
 screen: 'textEntityScreen',
 configs: [
 {
 id: 'cancelButtonConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#FFFFFF'
 }, {
 id: 'clearButtonConfig',
 enabled: true,
 icon: {},
 label: null,
 tint: '#FFFFFF'
 }, {
 id: 'saveButtonConfig',
 enabled: true,
 icon: {},
 label: '',
 tint: '#FFFFFF'
 }
 ]
 }],
 colorConfig: [
 {
 screen: 'allScreens',
 enabled: true,
 initialColor: '#000000',
 colors: ['#000000', '#20BBFC', '#2DFD2F', '#FD28F9', '#EA212E', '#FD7E24', '#FFFA38', '#FFFFFF'],
 pickerConfig: {
 enabled: true,
 icon: {},
 pickerLabel: 'Some label',
 cancelText: 'cancel',
 submitText: 'ok'
 }
 }
 ],
 sizeConfig: [
 {
 screen: 'sketchEntityScreen',
 enabled: true,
 backgroundColor: null,
 progressColor: null,
 initialValue: 5,
 min: 2,
 max: 40,
 step: 1
 },
 {
 screen: 'textEntityScreen',
 enabled: true,
 backgroundColor: null,
 progressColor: null,
 initialValue: 30,
 min: 15,
 max: 70,
 step: 1
 }
 ]
 }
 */

public class ConfigManager implements ConfigManagerActions {
    private static ConfigManager instance = null;

    private GeneralConfig generalConfig;
    private ArrayList<ButtonConfigs> buttonConfigs;
    private ArrayList<ColorConfig> colorConfigs;
    private ArrayList<SizeConfig> sizeConfigs;

    public ConfigManager (Bundle bundle, @Nullable Bundle injectedBundle) {
        generalConfig = null;
        buttonConfigs = new ArrayList<>();
        colorConfigs = new ArrayList<>();
        sizeConfigs = new ArrayList<>();
        init(bundle, injectedBundle);
    }

    @Nullable
    public static ConfigManager getInstance () {
        return instance;
    }

    public static void create (@Nullable Bundle bundle, @Nullable Bundle injectedBundle) {
        if (bundle != null) {
            instance = new ConfigManager(bundle, injectedBundle);
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

    private void init (Bundle bundle, @Nullable Bundle injectedBundle) {
        Integer buttonDefaultSideLength = null;
        if (injectedBundle != null) {
            buttonDefaultSideLength = injectedBundle.getInt("sideLength");
        }

        for (String key : bundle.keySet()) {
            Bundle tempBundle = bundle.getBundle(key);

            if (CONFIG_TYPE.get(key) == CONFIG_TYPE.GENERAL_CONFIG) { // GENERAL CONFIG
                if (tempBundle != null) {
                    generalConfig = new GeneralConfig(
                            tempBundle.getString("originalBackgroundImagePath", null),
                            tempBundle.getString("editedBackgroundImagePath", null),
                            tempBundle.getString("imageSaveName", null),
                            tempBundle.getString("fontFamily", null),
                            tempBundle.getString("initialToolSelection", null),
                            tempBundle.getString("initialText", null),
                            tempBundle.getString("backgroundColor", null),
                            new SavePermissionDialog(tempBundle.getBundle("savePermission")),
                            new DeleteDialog(tempBundle.getBundle("deleteDialog"))
                    );
                } else {
                    generalConfig = new GeneralConfig(
                            null,
                            null,
                            null,
                            null,
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
                                                    buildIconNameArrayFromBundle(buttonsBundle.getBundle("icon")),
                                                    buttonsBundle.getString("label", null),
                                                    buttonsBundle.getString("tint", null),
                                                    buttonsBundle.getInt("sideLength", buttonDefaultSideLength),
                                                    new int[]{
                                                            buttonsBundle.getInt("paddingLeft", 0),
                                                            buttonsBundle.getInt("paddingTop", 0),
                                                            buttonsBundle.getInt("paddingRight", 0),
                                                            buttonsBundle.getInt("paddingBottom", 0)
                                                    }
                                            );
                                        } else {
                                            buttonsConfig = new ButtonConfig(
                                                    CONFIG_TYPE.get(key),
                                                    false,
                                                    null,
                                                    null,
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
    private String[] buildIconNameArrayFromBundle(Bundle icon) {
        ArrayList<String> iconStrings = new ArrayList<>();
        if (icon != null) {
            ArrayList<Bundle> icons = (ArrayList<Bundle>) BundleConverter.bundleToArrayList(icon);
            if (icons != null) {
                for(Bundle b: icons) {
                    String iconString = buildIconNameFromBundle(b);
                    if (iconString != null) {
                        iconStrings.add(iconString);
                    }
                }
            } else {
                String iconString = buildIconNameFromBundle(icon);
                if(iconString != null) {
                    iconStrings.add(iconString);
                }
            }
        }
        if (iconStrings.isEmpty()) {
            return null;
        }
        return iconStrings.toArray(new String[iconStrings.size()]);
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

    @Override
    public void injectDefaultIcons (@Nullable ButtonConfig config, @Nullable String defaultDrawable, @Nullable String optionalDefaultDrawable) {
        // RessourceUtils.getImageRessource();
        if (!config.hasIcon(true) && defaultDrawable != null) {
            config.setIconName(defaultDrawable, true);
        }
        if (!config.hasIcon(false) && optionalDefaultDrawable != null) {
            config.setIconName(optionalDefaultDrawable, false);
        }
    }

    @Override
    public void configureButton(@Nullable Button tempButton, @Nullable ButtonConfig config, @Nullable Drawable defaultDrawable) {
        if (tempButton != null && config != null) {
            if (config.hasEnabled() && config.isEnabled()) {
                if (config.hasLabel() && !config.hasIcon()) {
                    tempButton.setText(config.getLabel());
                    tempButton.setBackground(null);
                }
                if (config.hasIcon() || (!config.hasLabel() && defaultDrawable != null)) {
                    tempButton.setText("");
                    tempButton.setBackground(config.hasIcon() ? config.getIcon() : defaultDrawable);

                    ViewGroup.LayoutParams layoutParams = tempButton.getLayoutParams();
                    if (config.hasSideLength()) {
                        layoutParams.width = layoutParams.height = config.getSideLength();
                    }

                    if (layoutParams instanceof LinearLayout.LayoutParams) {
                        LinearLayout.LayoutParams castedLayoutParams = ((LinearLayout.LayoutParams)tempButton.getLayoutParams());
                        castedLayoutParams.leftMargin = config.getPaddingLeft();
                        castedLayoutParams.topMargin = config.getPaddingTop();
                        castedLayoutParams.rightMargin = config.getPaddingRight();
                        castedLayoutParams.bottomMargin = config.getPaddingBottom();
                    }

                    tempButton.setLayoutParams(layoutParams);
                    tempButton.setPadding(0, 0, 0, 0);
                }

                if (config.hasTint()) {
                    Integer tintColor = config.getTintColor();
                    UIUtils.setButtonTint(tempButton, ColorStateList.valueOf(tintColor));
//                    tempButton.setBackgroundTintList(ColorStateList.valueOf(tintColor));
                    tempButton.setTextColor(tintColor);
                }
            } else if (tempButton.getParent() != null) {
                ((ViewGroup) tempButton.getParent()).removeView(tempButton);
            }
        }
    }

    @Override
    public void configureSize(@Nullable BoxedVertical boxedVertical, @Nullable SizeConfig config, SetSizeAction setSizeAction) {
        if (config != null) {
            if (boxedVertical != null) {
                if (config.hasBackgroundColor()) {
                    boxedVertical.setBackgroundColor(config.getBackgroundColor());
                }
                if (config.hasProgrssColor()) {
                    boxedVertical.setProgressPaint(config.getProgressColor());
                }
                if (config.hasMax()) {
                    boxedVertical.setMax(config.getMax());
                }
                if (config.hasMin()) {
                    boxedVertical.setMin(config.getMin());
                }
                if (config.hasStep()) {
                    boxedVertical.setStep(config.getStep());
                }
            }
            if (config.hasInitialValue() && setSizeAction != null) {
                setSizeAction.setSize(config.getInitialValue());
            }
        }
    }
}
