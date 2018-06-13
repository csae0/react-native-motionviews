package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.Nullable;

public interface TextEntityActions extends EntityActions {
    void updateState(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel,  @Nullable Integer maxWidth);
}
