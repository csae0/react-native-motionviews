package team.uptech.motionviews.widget.Interfaces;

public interface Limits {
    /**
     * limit text size to view bounds
     * so that users don't put small font size and scale it 100+ times
     */
    float MAX_SCALE = 10.0F;
    float MIN_SCALE = 0.2F;
    float INITIAL_SCALE = 1.0F;
    float INITIAL_FONT_SIZE_SCALE = 0.6f;
    float FONT_SIZE_MULTIPLICATOR = 100 * 0.5f; // transform scale into correct font size
    int INITIAL_FONT_COLOR = 0xffededed;
    int INITIAL_SKETCH_COLOR = 0xffededed;
    int FONT_SIZE_INITIAL_DP = (int)(INITIAL_FONT_SIZE_SCALE * FONT_SIZE_MULTIPLICATOR); // = TEXT SIZE 30? (defined in Integers.xml)
}