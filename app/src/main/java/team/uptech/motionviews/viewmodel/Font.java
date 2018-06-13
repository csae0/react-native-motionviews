package team.uptech.motionviews.viewmodel;

public class Font extends Stroke {

    private interface Limits {
        float MIN_FONT_SIZE = 0.01F;
    }

    /**
     * name of the font
     */
    protected String typeface;

    public void decreaseSize(float diff) {
        if (size - diff >= Limits.MIN_FONT_SIZE) {
            size = size - diff;
        }
    }

    public String getTypeface() {
        return typeface;
    }

    public void setTypeface(String typeface) {
        this.typeface = typeface;
    }
}