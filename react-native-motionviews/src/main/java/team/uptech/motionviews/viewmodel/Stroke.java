package team.uptech.motionviews.viewmodel;

public class Stroke {

    protected interface Limits {
        float MIN_STROKE_SIZE = 0.01F;
    }

    /**
     * color value (ex: 0xFF00FF)
     */
    protected int color;
    /**
     * size of the font, relative to parent
     */
    protected float size;

    public void increaseSize(float diff) {
        size = size + diff;
    }

    public void decreaseSize(float diff) {
        if (size - diff >= Limits.MIN_STROKE_SIZE) {
            size = size - diff;
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}