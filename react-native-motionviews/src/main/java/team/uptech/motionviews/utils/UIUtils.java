package team.uptech.motionviews.utils;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

public class UIUtils {

    @SuppressLint("RestrictedApi")
    public static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint); // Lint error
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }
}
