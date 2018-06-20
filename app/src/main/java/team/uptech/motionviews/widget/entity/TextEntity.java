package team.uptech.motionviews.widget.entity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import team.uptech.motionviews.ui.TextEditorDialogFragment;
import team.uptech.motionviews.utils.FontProvider;
import team.uptech.motionviews.viewmodel.Font;
import team.uptech.motionviews.viewmodel.TextLayer;
import team.uptech.motionviews.widget.Interfaces.TextEntityActions;

public class TextEntity extends MotionEntity implements TextEntityActions {

    private final TextPaint textPaint;
    private final FontProvider fontProvider;
    private int maxWidth;
    private boolean newCreated;
    @Nullable
    private Bitmap bitmap;

    public TextEntity(@NonNull TextLayer textLayer,
                      @IntRange(from = 1) int canvasWidth,
                      @IntRange(from = 1) int canvasHeight,
                      @NonNull FontProvider fontProvider) {
        this(textLayer, canvasWidth, canvasHeight, fontProvider, true);
    }

    public TextEntity(@NonNull TextLayer textLayer,
                      @IntRange(from = 1) int canvasWidth,
                      @IntRange(from = 1) int canvasHeight,
                      @NonNull FontProvider fontProvider,
                      boolean visible) {
        super(textLayer, canvasWidth, canvasHeight, visible);
        this.fontProvider = fontProvider;
        this.textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.maxWidth = Integer.MIN_VALUE;
        newCreated = true;
        updateEntity(false);
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
        holyScale = 1; // Math.min(widthAspect, heightAspect);
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

        if (moveToPreviousCenter && newCreated) {
            // move to previous center
            moveCenterTo(oldCenter);
        }
    }

    /**
     * If reuseBmp is not null, and size of the new bitmap matches the size of the reuseBmp,
     * new bitmap won't be created, reuseBmp it will be reused instead
     *
     * @param textLayer text to draw
     * @param reuseBmp  the bitmap that will be reused
     * @return bitmap with the text
     */
    @NonNull
    private Bitmap createBitmap(@NonNull TextLayer textLayer, @Nullable Bitmap reuseBmp) {

        // init params - size, color, typeface
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textLayer.getFont().getSize());
        textPaint.setColor(textLayer.getFont().getColor());
        textPaint.setTypeface(fontProvider.getTypeface(textLayer.getFont().getTypeface()));

        String text = textLayer.getText();
        String [] lines = text.split("\n");
        int boundsWidth = 0;
        for(String s : lines) {
             int lineWidth = (int)(textPaint.measureText(s) + 0.5f);
             if (lineWidth > boundsWidth) {
                 boundsWidth = lineWidth;
             }
        }
        if (maxWidth > 0 && boundsWidth > maxWidth) {
            boundsWidth = maxWidth;
        }
        // drawing text guide : http://ivankocijan.xyz/android-drawing-multiline-text-on-canvas/
        // Static layout which will be drawn on canvas
        StaticLayout sl = new StaticLayout(
                textLayer.getText(), // - text which will be drawn
                textPaint,
                boundsWidth, // - width of the layout
                Layout.Alignment.ALIGN_CENTER, // - layout alignment
                1, // 1 - text spacing multiply
                1, // 1 - text spacing add
                true); // true - include padding

        // calculate height for the entity, min - Limits.MIN_BITMAP_HEIGHT
        int boundsHeight = sl.getHeight();
        // create bitmap where text will be drawn
        Bitmap bmp;
        if (reuseBmp != null && reuseBmp.getWidth() == boundsWidth && reuseBmp.getHeight() == boundsHeight) {
            // if previous bitmap exists, and it's width/height is the same - reuse it
            bmp = reuseBmp;
            bmp.eraseColor(Color.TRANSPARENT); // erase color when reusing
        } else {
            bmp = Bitmap.createBitmap(boundsWidth, boundsHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bmp);
        canvas.save();

        //draws static layout on canvas
        sl.draw(canvas);
        canvas.restore();

        return bmp;
    }

    @Override
    @NonNull
    public TextLayer getLayer() {
        return (TextLayer) layer;
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

    public void updateEntity() {
        updateEntity(true);
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
        TextLayer textLayer = getLayer();
        Font font = textLayer.getFont();
        String text = textLayer.getText();
        int size = (int)(font.getSize() + 0.5f);
        int color = font.getColor();
        String typefaceName = font.getTypeface();
        TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(text, size, color, typefaceName);
        fragment.show(activity.getFragmentManager(), TextEditorDialogFragment.class.getName());
    }

    @Override
    public void updateState(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth) {
        TextLayer textLayer = getLayer();
        Font font = textLayer.getFont();

        // Set text
        if (text != null && text.length() > 0 && !text.equals(textLayer.getText())) {
            textLayer.setText(text);
        }
        // Set color
        if (color != null && color != font.getColor()) {
            font.setColor(color);
        }
        // Set size
        if (sizeInPixel != null && sizeInPixel > 0 && sizeInPixel != font.getSize()) {
            font.setSize((float)sizeInPixel);
        }
        // Set maxWidth
        if (maxWidth != null && maxWidth > 0 && getMaxWidth() != maxWidth) {
            setMaxWidth(maxWidth);
        }
        setVisible(true);
        updateEntity();

        if (newCreated == true) {
            newCreated = false;
        }
    }
}