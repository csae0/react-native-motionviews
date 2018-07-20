package at.csae0.reactnative.interfaces;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.Button;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.Config;
import at.csae0.reactnative.model.ScreenConfig;
import at.csae0.reactnative.model.SizeConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public interface ConfigManagerActions {
    @Nullable
    ScreenConfig getScreenConfig(CONFIG_TYPE screenType, CONFIG_TYPE configType);
    void configureButton(@Nullable Button button, @Nullable ButtonConfig config, @Nullable Drawable defaultDrawable);
    void configureSize(@Nullable BoxedVertical boxedVertical, @Nullable SizeConfig config, SetSizeAction setSizeAction);
    void injectDefaultIcons(@Nullable ButtonConfig config, @Nullable String defaultDrawable, @Nullable String optionalDefaultDrawable);
}
