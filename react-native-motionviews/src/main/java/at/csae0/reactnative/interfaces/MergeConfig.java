package at.csae0.reactnative.interfaces;

import android.support.annotation.Nullable;

import at.csae0.reactnative.model.Config;

public interface MergeConfig {
    @Nullable
    Config mergeConfig(@Nullable Config config);
}
