package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.NonNull;

import team.uptech.motionviews.ui.TextEditorDialogFragment;

/**
 * Callback that passes all user input through the method
 * {@link OnTextLayerCallback#textChanged(String)}
 */
public interface OnTextLayerCallback {
    void textChanged(@NonNull String text);
}
