package team.uptech.motionviews.widget.entity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sketchView.SketchView;
import com.sketchView.SketchViewContainer;

import team.uptech.motionviews.ui.MainActivity;
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
//        updateEntity(false);
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
    private boolean setBitmap(@Nullable Bitmap bitmap) {
        boolean wasNull = false;

        if (this.bitmap == null) {
            wasNull = true;
        }
        // recycle previous bitmap (if not reused) as soon as possible
        if (this.bitmap != null && this.bitmap != bitmap && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }

        this.bitmap = bitmap;


        return wasNull;
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

        final SketchView sketchView = SketchView.getInstance(main.getContext());
        sketchView.setCallback(new SketchViewCallback() {
            @Override
            public void closeAndCreateEntity(@Nullable Bitmap bitmap, @Nullable Integer color, @Nullable Integer sizeInPixel) {
                RelativeLayout main = activity.findViewById(R.id.activity_main);
                if (main != null && sketchView != null && main.indexOfChild(sketchView) >= 0) {
                    main.removeView(sketchView);
                    if (sketchView.linearLayout != null) {
                        main.removeView(sketchView.linearLayout);
                    }
                }
                ((EditCallback)activity).updateEntity(bitmap, color, sizeInPixel);
            }
        });

        if (sketchView != null && main.indexOfChild(sketchView) < 0) {
            main.addView(sketchView);
        }
    }

    @Override
    public void updateState(@Nullable Bitmap bitmap, @Nullable Integer color, @Nullable Integer sizeInPixel) {

        SketchLayer sketchLayer = getLayer();
        Stroke stroke = sketchLayer.getStroke();
        boolean moveToCenter = false;

        if (bitmap != null && this.bitmap != bitmap) {
            moveToCenter = setBitmap(bitmap);
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
        moveToCanvasCenter();
        updateEntity(moveToCenter);
    }
}