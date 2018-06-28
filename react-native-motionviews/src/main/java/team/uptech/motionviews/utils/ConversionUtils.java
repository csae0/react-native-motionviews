package team.uptech.motionviews.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

public class ConversionUtils {

    public static int getDensity() {
        return (int)Resources.getSystem().getDisplayMetrics().density;
    }

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

    public static int mixColors(int color1, int color2, float ratio) {
        return ColorUtils.blendARGB(color1, color2, ratio);
    }

    public static boolean shouldLighten (int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    public static double colorDistance (int color1, int color2) {
        double [] labColor1 = new double[3];
        double [] labColor2 = new double[3];

        ColorUtils.colorToLAB (color1, labColor1);
        ColorUtils.colorToLAB (color2, labColor2);

        return ColorUtils.distanceEuclidean(labColor1, labColor2);
    }

    public static int getToolSelectionIndicatorColor(int toolColor, int baseTint) {
        boolean lighten = shouldLighten(baseTint);
        double colorDistance = colorDistance(toolColor, baseTint);
        double distanceThreshold = 40;
        float colorMixingThreshold = 0.5f;

        return colorDistance > distanceThreshold ? toolColor : lighten == true ? mixColors(toolColor, Color.WHITE, colorMixingThreshold) : mixColors(toolColor, Color.BLACK, colorMixingThreshold);
    }
}
