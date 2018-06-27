package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.CONFIG_TYPE;

public class GeneralConfig extends Config {

    private String backgroundImage, imageSaveName, fontFamily;

    public GeneralConfig (@Nullable String backgroundImage, @Nullable String imageSaveName, @Nullable String fontFamily) {
        super(CONFIG_TYPE.GENERAL_CONFIG, true);
        this.backgroundImage = backgroundImage;
        this.imageSaveName = imageSaveName;
        this.fontFamily = fontFamily;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getImageSaveName() {
        return imageSaveName;
    }

    public void setImageSaveName(String imageSaveName) {
        this.imageSaveName = imageSaveName;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public boolean hasbackgroundImage() {
        return backgroundImage != null;
    }
    public boolean hasImageSaveName() {
        return imageSaveName != null;
    }
    public boolean hasfontFamily() {
        return fontFamily != null;
    }
}
