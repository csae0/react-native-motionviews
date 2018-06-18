package team.uptech.motionviews.widget.Interfaces;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;

public interface SketchViewCallback {
    void closeAndCreateEntity(@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel);
}
