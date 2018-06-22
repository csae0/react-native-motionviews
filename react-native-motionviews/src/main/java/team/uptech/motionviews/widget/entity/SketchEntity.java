package team.uptech.motionviews.widget.entity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.sketchView.SketchView;
import com.sketchView.SketchViewContainer;

import team.uptech.motionviews.viewmodel.SketchLayer;
import team.uptech.motionviews.viewmodel.Stroke;
import team.uptech.motionviews.widget.Interfaces.EditCallback;
import team.uptech.motionviews.widget.Interfaces.SketchEntityActions;
import team.uptech.motionviews.R;
import team.uptech.motionviews.widget.Interfaces.SketchViewCallback;

public class SketchEntity extends MotionEntity implements SketchEntityActions {

    private final Paint sketchPaint;

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
        this.bitmap = null;
    }

    @Override
    public Bitmap getEntityBitmap() {
        return bitmap;
    }

    public void updateEntity() {
        updateEntity(true);
    }

    private void updateEntity(boolean moveToPreviousCenter) {
        // save previous center
        PointF oldCenter = canvasCenter();

        if (bitmap == null) {
            return;
        }

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
     * @param bitmap  the bitmap that will be reused
     * @return bitmap with the text
     */
    @Nullable
    private void setBitmap(@Nullable Bitmap bitmap) {
        // recycle previous bitmap (if not reused) as soon as possible
        if (this.bitmap != null && this.bitmap != bitmap && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }

        this.bitmap = bitmap;
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

    @Override
    public void startEditing(final Activity activity) {
        if (bitmap == null) {
            setVisible(false);
            callEntityCallback(true);
            SketchLayer sketchLayer = getLayer();

            Stroke stroke = sketchLayer.getStroke();
            int size = (int) (stroke.getSize() + 0.5f);
            int color = stroke.getColor();
            // TODO: set size and color for sketchView!
            RelativeLayout main = activity.findViewById(R.id.activity_main);
            if (main == null) {
                return;
            }

            final SketchViewContainer sketchViewContainer = SketchViewContainer.getInstance(main.getContext());
            sketchViewContainer.setCallback(new SketchViewCallback() {
                @Override
                public void closeAndCreateEntity (@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel) {
                    RelativeLayout main = activity.findViewById(R.id.activity_main);
                    if (main != null && sketchViewContainer != null && main.indexOfChild(sketchViewContainer) >= 0) {
                        main.removeView(sketchViewContainer);
                    }
                    ((EditCallback) activity).updateEntity(bitmap, position, color, sizeInPixel);
                }
            });

            if (sketchViewContainer != null && main.indexOfChild(sketchViewContainer) < 0) {
                main.addView(sketchViewContainer);
            }
        }
    }

    @Override
    public void updateState(@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel) {

        SketchLayer sketchLayer = getLayer();
        Stroke stroke = sketchLayer.getStroke();

        // Set image
        if (bitmap != null && this.bitmap != bitmap) {
            setBitmap(bitmap);
        }

        // Set image position
        if (position != null) {
            float[] center = entityCenter();
            float topLeftX = layer.getX() + position.left;
            float topLeftY = layer.getY() + position.top;

            layer.postTranslate(1.0F * (topLeftX - center[0]) / canvasWidth,
                    1.0F * (topLeftY - center[1]) / canvasHeight);
        }

        // Set color
        if (color != null && color != stroke.getColor()) {
            stroke.setColor(color);
        }
        // Set size
        if (sizeInPixel != null && sizeInPixel > 0 && sizeInPixel != stroke.getSize()) {
            stroke.setSize((float)sizeInPixel);
        }

        setVisible(true);
        callEntityCallback(false);
        updateEntity(false);
    }
}