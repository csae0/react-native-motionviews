package team.uptech.motionviews.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class ConversionUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int transformAlpha(int color, float minAlpha, float maxAlpha){
        float currentAlpha = Color.alpha(color) / 255f;
        float transformedAlpha = ((maxAlpha - minAlpha) * currentAlpha) + minAlpha;
        int roundedTransformedAlpha = (int)((transformedAlpha * 255f) + 0.5f);
        return Color.argb(roundedTransformedAlpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int transformAlphaUpperTwoThirds(int color) {
        return transformAlpha(color, 0.3f, 1.0f);
    }
}
