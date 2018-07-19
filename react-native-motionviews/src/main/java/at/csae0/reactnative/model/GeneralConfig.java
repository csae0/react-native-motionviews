package at.csae0.reactnative.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import at.csae0.reactnative.R;
import at.csae0.reactnative.utils.CONFIG_TYPE;
import at.csae0.reactnative.utils.TOOL_TYPE;
import team.uptech.motionviews.utils.ConversionUtils;

public class GeneralConfig extends Config {

    private String originalBackgroundImagePath, editedBackgroundImagePath, imageSaveName, fontFamily, initialText;
    private TOOL_TYPE initialToolSelection;
    private Integer backgroundColor;
    private int[] imageBounds;
    public GeneralConfig (@Nullable String originalBackgroundImagePath, @Nullable String editedBackgroundImagePath, @Nullable String imageSaveName, @Nullable String fontFamily, @Nullable String initialToolSelection, @Nullable String initialText, @Nullable String backgroundColor) {
        super(CONFIG_TYPE.GENERAL_CONFIG, true);

        setBackgroundImagePath(originalBackgroundImagePath, true);
        setBackgroundImagePath(editedBackgroundImagePath, false);
        setImageSaveName(imageSaveName);
        setFontFamily(fontFamily);
        setInitialToolSelection(initialToolSelection);
        setInitialText(initialText);
        setBackgroundColor(backgroundColor);
        setImageBounds();
    }

    @Nullable
    public String getBackgroundImagePath(boolean original) {
        return original ? originalBackgroundImagePath : editedBackgroundImagePath;
    }

    @Nullable
    public int[] getImageBounds() {
        return imageBounds;
    }

    @Nullable
    public Bitmap getBackgroundBitmap(@Nullable BitmapFactory.Options options, boolean original) {
        Bitmap bitmap = BitmapFactory.decodeFile(original ? originalBackgroundImagePath : editedBackgroundImagePath, options);

        if (bitmap != null) {
            return bitmap;
        }
        return null;
    }
    @Nullable
    public Drawable getBackgroundDrawable(Context context, @Nullable BitmapFactory.Options options, boolean original) {
        Bitmap bitmap = BitmapFactory.decodeFile(original ? originalBackgroundImagePath : editedBackgroundImagePath, options);

        if (bitmap != null) {
            return new BitmapDrawable(context.getResources(), bitmap);
        }
        return null;
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
    @Nullable
    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundImagePath(@Nullable String backgroundImagePath, boolean original) {
        if (original) {
            originalBackgroundImagePath = backgroundImagePath;
        } else {
            editedBackgroundImagePath = backgroundImagePath;
        }
    }

    public void setImageBounds () {
        Bitmap backgroundImage = getBackgroundBitmap(null, true);
        int[] screenDimensions = ConversionUtils.getScreenDimensions();

        if (backgroundImage != null) {
            imageBounds = ConversionUtils.transformImageSizeToFitScreen(backgroundImage.getWidth(), backgroundImage.getHeight(), screenDimensions[0], screenDimensions[1], true);
        } else {
            imageBounds = screenDimensions;
        }
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

    public void setBackgroundColor(@Nullable Integer backgroundColor) {
        this.backgroundColor = backgroundColor != null ? backgroundColor : null;
    }

    public void setBackgroundColor(@Nullable String backgroundColor) {
        this.backgroundColor = backgroundColor != null ? Color.parseColor(backgroundColor) : null;
    }
    public boolean hasImageBounds() {
        return imageBounds != null;
    }
    public boolean hasBackgroundImagePath(boolean original) {

        return original ? originalBackgroundImagePath != null : editedBackgroundImagePath != null;
    }
    public boolean hasBackgroundDrawable(boolean original) {
        if (hasBackgroundImagePath(original)) {
            return original ? BitmapFactory.decodeFile(originalBackgroundImagePath) != null : BitmapFactory.decodeFile(editedBackgroundImagePath) != null;
        }
        return false;
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
    public boolean hasBackgroundColor() {
        return backgroundColor != null;
    }
}
