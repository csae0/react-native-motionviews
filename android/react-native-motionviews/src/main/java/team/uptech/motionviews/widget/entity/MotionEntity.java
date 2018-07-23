package team.uptech.motionviews.widget.entity;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import team.uptech.motionviews.utils.MathUtils;
import team.uptech.motionviews.viewmodel.Layer;
import team.uptech.motionviews.widget.Interfaces.EntityActions;
import team.uptech.motionviews.widget.Interfaces.EntityCallback;

@SuppressWarnings({"WeakerAccess"})
public abstract class MotionEntity implements EntityActions {

    /**
     * data
     */
    @NonNull
    protected final Layer layer;

    /**
     * transformation matrix for the entity
     */
    protected final Matrix matrix = new Matrix();
    /**
     * true - entity is selected and need to draw it's border
     * false - not selected, no need to draw it's border
     */
    private boolean isSelected;
    /**
     * true - draw entity
     * false - hide entity
     */
    protected boolean visible;
    /**
     * maximum scale of the initial image, so that
     * the entity still fits within the parent canvas
     */
    protected float holyScale;

    /**
     * width of canvas the entity is drawn in
     */
    @IntRange(from = 0)
    protected int canvasWidth;
    /**
     * height of canvas the entity is drawn in
     */
    @IntRange(from = 0)
    protected int canvasHeight;

    /**
     * Destination points of the entity
     * 5 points. Size of array - 10; Starting upper left corner, clockwise
     * last point is the same as first to close the circle
     * NOTE: saved as a field variable in order to avoid creating array in draw()-like methods
     */
    private final float[] destPoints = new float[10]; // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0
    /**
     * Initial points of the entity
     * @see #destPoints
     */
    protected final float[] srcPoints = new float[10];  // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0


    // Callback to hide/ show all entities
    private EntityCallback entityCallback;

    @NonNull
    private Paint borderPaint = new Paint();

    public MotionEntity(@NonNull Layer layer,
                        @IntRange(from = 1) int canvasWidth,
                        @IntRange(from = 1) int canvasHeight) {
        this.layer = layer;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public MotionEntity(@NonNull Layer layer,
                        @IntRange(from = 1) int canvasWidth,
                        @IntRange(from = 1) int canvasHeight,
                        boolean visible) {
        this(layer, canvasWidth, canvasHeight);
        this.visible = visible;
    }

    private boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * S - scale matrix, R - rotate matrix, T - translate matrix,
     * L - result transformation matrix
     * <p>
     * The correct order of applying transformations is : L = S * R * T
     * <p>
     * See more info: <a href="http://gamedev.stackexchange.com/questions/29260/transform-matrix-multiplication-order">Game Dev: Transform Matrix multiplication order</a>
     * <p>
     * Preconcat works like M` = M * S, so we apply preScale -> preRotate -> preTranslate
     * the result will be the same: L = S * R * T
     * <p>
     * NOTE: postconcat (postScale, etc.) works the other way : M` = S * M, in order to use it
     * we'd need to reverse the order of applying
     * transformations : post holy scale ->  postTranslate -> postRotate -> postScale
     */
    protected void updateMatrix() {
        // init matrix to E - identity matrix
        matrix.reset();

        float topLeftX = layer.getX() * canvasWidth;
        float topLeftY = layer.getY() * canvasHeight;

        float centerX = topLeftX + getWidth() * holyScale * 0.5F;
        float centerY = topLeftY + getHeight() * holyScale * 0.5F;

        // calculate params
        float rotationInDegree = layer.getRotationInDegrees();
        float scaleX = layer.getScale();
        float scaleY = layer.getScale();
        if (layer.isFlipped()) {
            // flip (by X-coordinate) if needed
            rotationInDegree *= -1.0F;
            scaleX *= -1.0F;
        }

        // applying transformations : L = S * R * T

        // scale
        matrix.preScale(scaleX, scaleY, centerX, centerY);

        // rotate
        matrix.preRotate(rotationInDegree, centerX, centerY);

        // translate
        matrix.preTranslate(topLeftX, topLeftY);

        // applying holy scale - S`, the result will be : L = S * R * T * S`
        matrix.preScale(holyScale, holyScale);
    }

    public float[] entityCenter() {
        float topLeftX = layer.getX();
        float topLeftY = layer.getY();
        float centerX = topLeftX + getWidth() * 0.5F;
        float centerY = topLeftY + getHeight() * 0.5F;
        float[] point = { centerX, centerY };

        updateMatrix();
        matrix.mapPoints(point);

        return point;
    }

    public int[] canvasDimensions() {
        return new int[]{canvasWidth, canvasHeight};
    }
    public float absoluteCenterX() {
        float topLeftX = layer.getX() * canvasWidth;
        return topLeftX + getWidth() * holyScale * 0.5F;
    }

    public float absoluteCenterY() {
        float topLeftY = layer.getY() * canvasHeight;

        return topLeftY + getHeight() * holyScale * 0.5F;
    }

    public PointF absoluteCenter() {
        float topLeftX = layer.getX() * canvasWidth;
        float topLeftY = layer.getY() * canvasHeight;

        float centerX = topLeftX + getWidth() * holyScale * 0.5F;
        float centerY = topLeftY + getHeight() * holyScale * 0.5F;

        return new PointF(centerX, centerY);
    }

    public PointF canvasCenter() {
        return new PointF(canvasWidth * 0.5F, canvasHeight * 0.5F);
    }
    public void moveToCanvasCenter() {
        moveCenterTo(new PointF(canvasWidth * 0.5F, canvasHeight * 0.5F));
    }

    public void moveCenterTo(PointF moveToCenter) {
        PointF currentCenter = absoluteCenter();
        layer.postTranslate(1.0F * (moveToCenter.x - currentCenter.x) / canvasWidth,
                1.0F * (moveToCenter.y - currentCenter.y) / canvasHeight);
    }

    private final PointF pA = new PointF();
    private final PointF pB = new PointF();
    private final PointF pC = new PointF();
    private final PointF pD = new PointF();

    /**
     * For more info:
     * <a href="http://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle">StackOverflow: How to check point is in rectangle</a>
     * <p>NOTE: it's easier to apply the same transformation matrix (calculated before) to the original source points, rather than
     * calculate the result points ourselves
     * @param point point
     * @return true if point (x, y) is inside the triangle
     */
    public boolean pointInLayerRect(PointF point) {

        updateMatrix();
        // map rect vertices
        matrix.mapPoints(destPoints, srcPoints);

        pA.x = destPoints[0];
        pA.y = destPoints[1];
        pB.x = destPoints[2];
        pB.y = destPoints[3];
        pC.x = destPoints[4];
        pC.y = destPoints[5];
        pD.x = destPoints[6];
        pD.y = destPoints[7];

        return MathUtils.pointInTriangle(point, pA, pB, pC) || MathUtils.pointInTriangle(point, pA, pD, pC);
    }

    @Nullable
    public abstract Bitmap getEntityBitmap();

    // TODO: Pixel check --> min stroke thickness (to select small thickness e.g. circles more easily)
    public boolean pointHasPixelColor (PointF point) {
        Bitmap image = getEntityBitmap();
        if (image != null) {
            Matrix inverse = new Matrix();
            matrix.invert(inverse);
            float[] transformedPoint = new float[2];
            inverse.mapPoints(transformedPoint, new float[]{point.x, point.y});

            return !isTransparent(transformedPoint, image, true);
        }
        return false;
    }

    private boolean isTransparent(float[] point, Bitmap image, boolean checkCoordinates) {
        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;
        if (checkCoordinates && !(point[0] >= 0 && point[0] < width && point[1] >= 0 && point[1] < height)) {
            return true;
        }
        int pixel = image.getPixel((int)point[0], (int)point[1]);
        return Color.alpha(pixel) == 0;
    }
    /**
     * http://judepereira.com/blog/calculate-the-real-scale-factor-and-the-angle-of-rotation-from-an-android-matrix/
     *
     * @param canvas Canvas to draw
     * @param drawingPaint Paint to use during drawing
     */
    public void draw(@NonNull Canvas canvas, @Nullable Paint drawingPaint) {

        if (visible) {
            updateMatrix();
            canvas.save();
            drawContent(canvas, drawingPaint);

//            float[] center = entityCenter();
//            canvas.drawCircle(center[0], center[1], 5, new Paint(Color.GREEN));

            if (isSelected()) {
                // get alpha from drawingPaint
                int storedAlpha = borderPaint.getAlpha();
                if (drawingPaint != null) {
                    borderPaint.setAlpha(drawingPaint.getAlpha());
                }
                // drawSelectedBg(canvas);
                // restore border alpha
                borderPaint.setAlpha(storedAlpha);
            }

            canvas.restore();
        }
    }


    private void drawSelectedBg(Canvas canvas) {
        matrix.mapPoints(destPoints, srcPoints);
        //noinspection Range
        canvas.drawLines(destPoints, 0, 8, borderPaint);
        //noinspection Range
        canvas.drawLines(destPoints, 2, 8, borderPaint);
    }

    @NonNull
    public Layer getLayer() {
        return layer;
    }

    public void setBorderPaint(@NonNull Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    protected abstract void drawContent(@NonNull Canvas canvas, @Nullable Paint drawingPaint);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void release();

    @Override
    protected void finalize() throws Throwable {
        try {
            release();
        } finally {
            //noinspection ThrowFromFinallyBlock
            super.finalize();
        }
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEntityCallback (EntityCallback callback) {
        entityCallback = callback;
    }

    public boolean hasEntityCallback () {
        return entityCallback != null;
    }

    protected void callEntityCallback (boolean hide) {
        if (hasEntityCallback()) {
            entityCallback.hideAllVisibleEntities(hide);
        }
    }

    @Override
    public void startEditing(Activity activity) {
        // Override in subclasses if there is a special editing logic
    }
}