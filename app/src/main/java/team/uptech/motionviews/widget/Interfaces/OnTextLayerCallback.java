package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import team.uptech.motionviews.ui.TextEditorDialogFragment;
import team.uptech.motionviews.utils.FontProvider;

/**
 * Callback that passes all user input through the method
 * {@link OnTextLayerCallback#textChanged(String)}
 */
public interface OnTextLayerCallback {
    void textChanged(@NonNull String text);
    void textColorChanged(@NonNull int color);
    void textSizeChanged(@NonNull int size);
    void multiTextChange(@Nullable String text, @Nullable Integer color, @Nullable Integer size, @Nullable Integer maxWidth);
    @Nullable FontProvider getFontProvider();
}
