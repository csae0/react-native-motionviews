package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.FontProvider;

public interface EntityCallback {
    void updateEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer size, @Nullable Integer maxWidth);
    @Nullable
    FontProvider getFontProvider();
}