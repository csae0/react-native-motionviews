package team.uptech.motionviews.widget.entity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.sketchView.SketchViewContainer;

import team.uptech.motionviews.ui.MainActivity;
import team.uptech.motionviews.viewmodel.SketchLayer;
import team.uptech.motionviews.viewmodel.Stroke;
import team.uptech.motionviews.widget.Interfaces.SketchEntityActions;
import team.uptech.motionviews.R;

public class SketchEntity extends MotionEntity implements SketchEntityActions {

    private final Paint sketchPaint;
    private int maxWidth;

    @Nullable
    private Bitmap bitmap;

    public SketchEntity(@NonNull SketchLayer sketchLayer,
                        @IntRange(from = 1) int canvasWidth,
                        @IntRange(from = 1) int canvasHeight) {
        this(sketchLayer, canvasWidth, canvasHeight, true);
    }

    public SketchEntity(@NonNull SketchLayer sketchLayer,
                        @IntRange(from = 1) int canvasWidth,
                        @IntRange(from = 1) int canvasHeight,
                        boolean visible) {
        super(sketchLayer, canvasWidth, canvasHeight, visible);
        this.sketchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.maxWidth = Integer.MIN_VALUE;
        updateEntity(false);
    }

    public void updateEntity() {
        updateEntity(true);
    }

    private void updateEntity(boolean moveToPreviousCenter) {
        // save previous center
        PointF oldCenter = canvasCenter();

        Bitmap newBmp = createBitmap(getLayer(), bitmap);
        if (newBmp == null) {
            return;
        }
        // recycle previous bitmap (if not reused) as soon as possible
        if (bitmap != null && bitmap != newBmp && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        this.bitmap = newBmp;

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        // fit the smallest size
        holyScale = 1;
        // initial position of the entity
        srcPoints[0] = 0;
        srcPoints[1] = 0;
        srcPoints[2] = width;
        srcPoints[3] = 0;
        srcPoints[4] = width;
        srcPoints[5] = height;
        srcPoints[6] = 0;
        srcPoints[7] = height;
        srcPoints[8] = 0;
        srcPoints[9] = 0;

        if (moveToPreviousCenter) {
            // move to previous center
            moveCenterTo(oldCenter);
        }
    }

    /**
     * If reuseBmp is not null, and size of the new bitmap matches the size of the reuseBmp,
     * new bitmap won't be created, reuseBmp it will be reused instead
     *
     * @param sketchLayer sketch to draw
     * @param reuseBmp  the bitmap that will be reused
     * @return bitmap with the text
     */
    @NonNull
    private Bitmap createBitmap(@NonNull SketchLayer sketchLayer, @Nullable Bitmap reuseBmp) {
        // TODO: REWORK
        // init params - size, color, typeface
//        sketchPaint.setStyle(Paint.Style.FILL);
//        sketchPaint.setTextSize(sketchLayer.getFont().getSize());
//        sketchPaint.setColor(sketchLayer.getFont().getColor());
//        sketchPaint.setTypeface(fontProvider.getTypeface(sketchLayer.getFont().getTypeface()));
//
//        String text = sketchLayer.getText();
//        String [] lines = text.split("\n");
//        int boundsWidth = 0;
//        for(String s : lines) {
//            int lineWidth = (int)(sketchPaint.measureText(s) + 0.5f);
//            if (lineWidth > boundsWidth) {
//                boundsWidth = lineWidth;
//            }
//        }
//        if (maxWidth > 0 && boundsWidth > maxWidth) {
//            boundsWidth = maxWidth;
//        }
//        // drawing text guide : http://ivankocijan.xyz/android-drawing-multiline-text-on-canvas/
//        // Static layout which will be drawn on canvas
//        StaticLayout sl = new StaticLayout(
//                sketchLayer.getText(), // - text which will be drawn
//                sketchPaint,
//                boundsWidth, // - width of the layout
//                Layout.Alignment.ALIGN_CENTER, // - layout alignment
//                1, // 1 - text spacing multiply
//                1, // 1 - text spacing add
//                true); // true - include padding
//
//        // calculate height for the entity, min - Limits.MIN_BITMAP_HEIGHT
//        int boundsHeight = sl.getHeight();
//        // create bitmap where text will be drawn
//        Bitmap bmp;
//        if (reuseBmp != null && reuseBmp.getWidth() == boundsWidth && reuseBmp.getHeight() == boundsHeight) {
//            // if previous bitmap exists, and it's width/height is the same - reuse it
//            bmp = reuseBmp;
//            bmp.eraseColor(Color.TRANSPARENT); // erase color when reusing
//        } else {
//            bmp = Bitmap.createBitmap(boundsWidth, boundsHeight, Bitmap.Config.ARGB_8888);
//        }
//        Canvas canvas = new Canvas(bmp);
//        canvas.save();
//
//        //draws static layout on canvas
//        sl.draw(canvas);
//        canvas.restore();
//
//        return bmp;
        return null;
    }

    @Override
    @NonNull
    public SketchLayer getLayer() {
        return (SketchLayer) layer;
    }

    @Override
    protected void drawContent(@NonNull Canvas canvas, @Nullable Paint drawingPaint) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, matrix, drawingPaint);
        }
    }

    @Override
    public int getWidth() {
        return bitmap != null ? bitmap.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return bitmap != null ? bitmap.getHeight() : 0;
    }

    @Override
    public void release() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    @Override
    public void startEditing(Activity activity) {
        setVisible(false);
        SketchLayer sketchLayer = getLayer();

        Stroke stroke = sketchLayer.getStroke();
        int size = (int) (stroke.getSize() + 0.5f);
        int color = stroke.getColor();
//
//        SketchViewContainer sketchViewContainer = new SketchViewContainer(activity.getApplicationContext());
//        RelativeLayout main = activity.findViewById(R.id.activity_main);
//        main.addView(sketchViewContainer);

        // TODO: OPEN FRGMENT OR SOMETHING
//        TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(text, size, color, typefaceName);
//        fragment.show(activity.getFragmentManager(), TextEditorDialogFragment.class.getName());
    }

    @Override
    public void updateState(@Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth) {
        SketchLayer sketchLayer = getLayer();
        Stroke stroke = sketchLayer.getStroke();

        // Set color
        if (color != null && color != stroke.getColor()) {
            stroke.setColor(color);
        }
        // Set size
        if (sizeInPixel != null && sizeInPixel > 0 && sizeInPixel != stroke.getSize()) {
            stroke.setSize((float)sizeInPixel);
        }
        // Set maxWidth
        if (maxWidth != null && maxWidth > 0 && getMaxWidth() != maxWidth) {
            setMaxWidth(maxWidth);
        }
        setVisible(true);
        updateEntity();
    }
}