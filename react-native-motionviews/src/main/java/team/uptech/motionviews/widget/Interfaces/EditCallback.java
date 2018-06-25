package team.uptech.motionviews.widget.Interfaces;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.FontProvider;

public interface EditCallback {
    @Nullable FontProvider getFontProvider();
    void updateTextEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer size, @Nullable Integer maxWidth);
    void updateSketchEntity(@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel);
    void cancelAction();
}