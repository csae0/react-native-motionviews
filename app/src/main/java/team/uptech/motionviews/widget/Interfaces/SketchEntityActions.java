package team.uptech.motionviews.widget.Interfaces;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface SketchEntityActions {
    void updateState(@Nullable Bitmap bitmap, @Nullable Integer color, @Nullable Integer sizeInPixel);
}
