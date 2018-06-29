package at.csae0.reactnative.interfaces;

import android.support.annotation.Nullable;

import at.csae0.reactnative.model.ScreenConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;

public interface ConfigManagerActions {
    @Nullable
    ScreenConfig getScreenConfig(CONFIG_TYPE screenType, CONFIG_TYPE configType);
}
