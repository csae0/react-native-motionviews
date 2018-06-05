package team.uptech.motionviews.widget.Interfaces;

public interface Limits {
    /**
     * limit text size to view bounds
     * so that users don't put small font size and scale it 100+ times
     */
    float MAX_SCALE = 1.5F; // = RESULTS IN TEXT SIZE 75 (defined in Integers.xml)
    float MIN_SCALE = 0.3F; // = RESULTS IN TEXT SIZE 15 (defined in Integers.xml)
    float INITIAL_SCALE = 1.0F; // set the same to avoid text scaling
    float INITIAL_FONT_SIZE_SCALE = 0.6f;
    float FONT_SIZE_MULTIPLICATOR = 100 * 0.5f; // transform scale into correct font size

    int FONT_SIZE_MAX = (int)(MAX_SCALE * FONT_SIZE_MULTIPLICATOR); // = TEXT SIZE 75 (defined in Integers.xml)
    int FONT_SIZE_MIN = (int)(MIN_SCALE * FONT_SIZE_MULTIPLICATOR); // = TEXT SIZE 15 (defined in Integers.xml)
    int FONT_SIZE_INITIAL_DP = (int)(INITIAL_FONT_SIZE_SCALE * FONT_SIZE_MULTIPLICATOR); // = TEXT SIZE 30 (defined in Integers.xml)




    float MIN_BITMAP_HEIGHT = 0.13F;
    //    float FONT_SIZE_STEP = 0.008F;
    int INITIAL_FONT_COLOR = 0xff000000;
    //float INITIAL_SCALE = 0.8F; // set the same to avoid text scaling

}