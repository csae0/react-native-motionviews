package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import at.csae0.reactnative.utils.CONFIG_TYPE;
import at.csae0.reactnative.utils.TOOL_TYPE;

public class GeneralConfig extends Config {

    private String backgroundImage, imageSaveName, fontFamily, initialText;
    private TOOL_TYPE initialToolSelection;

    public GeneralConfig (@Nullable String backgroundImage, @Nullable String imageSaveName, @Nullable String fontFamily, @Nullable String initialToolSelection, @Nullable String initialText) {
        super(CONFIG_TYPE.GENERAL_CONFIG, true);

        setBackgroundImage(backgroundImage);
        setImageSaveName(imageSaveName);
        setFontFamily(fontFamily);
        setInitialToolSelection(initialToolSelection);
        setInitialText(initialText);
    }

    @Nullable
    public String getBackgroundImage() {
        return backgroundImage;
    }
    @Nullable
    public String getImageSaveName() {
        return imageSaveName;
    }
    @Nullable
    public String getFontFamily() {
        return fontFamily;
    }
    @Nullable
    public TOOL_TYPE getInitialToolSelection() {
        return initialToolSelection;
    }
    @Nullable
    public String getInitialText() {
            return initialText;
    }

    public void setBackgroundImage(@Nullable String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setImageSaveName(@Nullable String imageSaveName) {
        this.imageSaveName = imageSaveName;
    }

    public void setFontFamily(@Nullable String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setInitialToolSelection(@Nullable TOOL_TYPE initialToolSelection) {
        this.initialToolSelection = initialToolSelection;
    }
    public void setInitialToolSelection(@Nullable String initialToolSelection) {
        this.initialToolSelection = TOOL_TYPE.get(initialToolSelection);
    }
    public void setInitialText(@Nullable String initialText) {
        this.initialText = initialText;
    }


    public boolean hasbackgroundImage() {
        return backgroundImage != null;
    }
    public boolean hasImageSaveName() {
        return imageSaveName != null;
    }
    public boolean hasFontFamily() {
        return fontFamily != null;
    }
    public boolean hasInitialToolSelection() {
        return initialToolSelection != null;
    }
    public boolean hasInitialText() {
        return initialText != null;
    }
}
