package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.FontProvider;

public interface EntityCallback {
    @Nullable FontProvider getFontProvider();
    void updateEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer size, @Nullable Integer maxWidth);
    void updateEntity(@Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth);
}