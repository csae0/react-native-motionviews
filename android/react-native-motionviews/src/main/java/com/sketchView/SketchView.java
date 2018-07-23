package com.sketchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.sketchView.model.MultiPoint;
import com.sketchView.tools.ArrowSketchTool;
import com.sketchView.tools.CircleSketchTool;
import com.sketchView.tools.EraseSketchTool;
import com.sketchView.tools.PenSketchTool;
import com.sketchView.tools.Blueprints.SketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;

import java.util.ArrayList;

import team.uptech.motionviews.utils.ConversionUtils;

import static com.sketchView.SketchView.DIRECTION.BOTTOM;
import static com.sketchView.SketchView.DIRECTION.LEFT;
import static com.sketchView.SketchView.DIRECTION.RIGHT;
import static com.sketchView.SketchView.DIRECTION.TOP;

/**
 * Created by keshav on 05/04/17.
 */

public class SketchView extends View {

    enum DIRECTION { HORIZONTAL, VERTICAL, LEFT, TOP, RIGHT, BOTTOM, UNDEFINED }

    private static SketchView instance = null;

    SketchTool currentTool;
    SketchTool penTool;
    SketchTool eraseTool;
    SketchTool circleTool;
    SketchTool arrowTool;

    Bitmap incrementalImage;
    Bitmap imageCopy;
    private boolean blockEditedUpdates;
    private Rect croppedImageBounds;
    // TODO: remove debug variables
//    public boolean showBounds = false;

    /**
     *  Constructor (allowing only one instance)
     */
    public static SketchView getInstance(Context context) {
        if (instance == null) {
            instance = new SketchView(context);
        }
        return instance;
    }
    private SketchView(Context context) {
        super(context);

        blockEditedUpdates = false;
        croppedImageBounds = null;

        penTool = new PenSketchTool(this);
        eraseTool = new EraseSketchTool(this);
        circleTool = new CircleSketchTool(this);
        arrowTool = new ArrowSketchTool(this);
        setToolType(SketchTool.TYPE_PEN); // TYPE_PEN
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     *  Crop image correctly with good performance
     */
    // TODO: use paths instead of bitmap
    // TODO: remember min toolThickness which was used on sketchView (to assure that step is small enough)
    @Nullable
    private Rect getImageBounds() {
        if (incrementalImage != null) {
            if (imageCopy != null) {
                imageCopy.recycle();
                imageCopy = null;
            }
            imageCopy = incrementalImage.copy(incrementalImage.getConfig(), incrementalImage.isMutable());

            int density = ConversionUtils.getDensity();
            int toolThickness = (int)((ToolThickness)currentTool).getToolThickness();
            int step = (toolThickness > 5 ? 5 : toolThickness > 1 ? toolThickness - 1 : 1) * density;

            MultiPoint<Integer, ArrayList<Integer>> maxRightP = MultiPoint.createMultiYPoint(Integer.MIN_VALUE);
            MultiPoint<Integer, ArrayList<Integer>> minLeftP = MultiPoint.createMultiYPoint(Integer.MAX_VALUE);
            MultiPoint<ArrayList<Integer>, Integer> maxBottomP = MultiPoint.createMultiXPoint(Integer.MIN_VALUE);
            MultiPoint<ArrayList<Integer>, Integer> minTopP = MultiPoint.createMultiXPoint(Integer.MAX_VALUE);

            int width = imageCopy.getWidth() - 1;
            int height = imageCopy.getHeight() - 1;
            boolean leftTop, leftBottom, rightTop, rightBottom;

            for (int left = 0; left < width / 2; left += step) {
                for (int top = 0; top < height / 2; top += step) {
                    leftTop = !isTransparent(left, top);
                    rightTop = !isTransparent(width - left, top);
                    leftBottom = !isTransparent(left, height - top);
                    rightBottom = !isTransparent(width - left, height - top);

                    if (leftTop || leftBottom) {
                        if (left <= minLeftP.getPoint()) {
                            minLeftP.setPoint(left);
                            minLeftP.add(leftTop ? top : height - top);
                        }
                        if (left >= maxRightP.getPoint()) {
                            maxRightP.setPoint(left);
                            maxRightP.add(leftTop ? top : height - top);
                        }
                    }
                    if (leftTop || rightTop) {
                        if (top <= minTopP.getPoint()) {
                            minTopP.setPoint(top);
                            minTopP.add(leftTop ? left : width - left);
                        }
                        if (top >= maxBottomP.getPoint()) {
                            maxBottomP.setPoint(top);
                            maxBottomP.add(leftTop ? left : width - left);
                        }
                    }
                    if (leftBottom || rightBottom) {
                        if (height - top <= minTopP.getPoint()) {
                            minTopP.setPoint(height - top);
                            minTopP.add(leftBottom ? left : width - left);
                        }
                        if (height - top >= maxBottomP.getPoint()) {
                            maxBottomP.setPoint(height - top);
                            maxBottomP.add(leftBottom ? left : width - left);
                        }
                    }
                    if (rightTop || rightBottom) {
                        if (width - left <= minLeftP.getPoint()) {
                            minLeftP.setPoint(width - left);
                            minLeftP.add(rightTop ? top : height - top);
                        }
                        if (width - left >= maxRightP.getPoint()) {
                            maxRightP.setPoint(width - left);
                            maxRightP.add(rightTop ? top : height - top);
                        }
                    }
                }
            }

            ArrayList<Point> minLefts = new ArrayList<>();
            ArrayList<Point> minTops = new ArrayList<>();
            ArrayList<Point> maxRights = new ArrayList<>();
            ArrayList<Point> maxBottoms = new ArrayList<>();
            int fillColor = Color.argb(255, 217, 179, 140); // Color brown with transparency (should be likely not to be chosen) // TODO: remember colors which are drawn on sketchView
            for (Point p: minLeftP.getPairs()) {
                minLefts.add(findExactBounds(LEFT, p, 1, fillColor));
            }
            for (Point p: minTopP.getPairs()) {
                minTops.add(findExactBounds(TOP, p, 1, fillColor));
            }
            for (Point p: maxRightP.getPairs()) {
                maxRights.add(findExactBounds(RIGHT, p, 1, fillColor));
            }
            for (Point p: maxBottomP.getPairs()) {
                maxBottoms.add(findExactBounds(BOTTOM, p, 1, fillColor));
            }

            Point minLeft = getMinOrMax(minLefts.toArray(new Point[minLefts.size()]), true, true, -1);
            Point minTop = getMinOrMax(minTops.toArray(new Point[minTops.size()]), false, true, -1);
            Point maxRight = getMinOrMax(maxRights.toArray(new Point[maxRights.size()]), true, false, -1);
            Point maxBottom = getMinOrMax(maxBottoms.toArray(new Point[maxBottoms.size()]), false, false, -1);
            
            if (minLeftP != null && minTopP != null && maxRightP != null && maxBottomP != null) {
                return new Rect((minLeft != null ? minLeft.x : minLeftP.getPoint()),
                        (minTop != null ? minTop.y : minTopP.getPoint()),
                        (maxRight != null ? maxRight.x : maxRightP.getPoint()),
                        (maxBottom != null ? maxBottom.y : maxBottomP.getPoint()));
            }
        }
        return null;
    }
    private Point findExactBounds (DIRECTION direction, Point curr, int step, int color) {

        Point[] newPoints = new Point[4];

        if (isTransparentOrColor(curr.x, curr.y, color, true)) {
            return null;
        }

//        if (showBounds) {
//            incrementalImage.setPixel(curr.x, curr.y, Color.GREEN);
//        }
        imageCopy.setPixel(curr.x, curr.y, color);

        if (direction != RIGHT) {
            newPoints[0] = findExactBounds(direction, new Point(curr.x - step, curr.y), step, color); // LEFT
        }
        if (direction != BOTTOM) {
            newPoints[1] = findExactBounds(direction, new Point(curr.x ,curr.y - step), step, color); // TOP
        }
        if (direction != LEFT) {
            newPoints[2] = findExactBounds(direction, new Point(curr.x + step ,curr.y), step, color); // RIGHT
        }
        if (direction != TOP) {
            newPoints[3] = findExactBounds(direction, new Point(curr.x ,curr.y + step), step, color); // BOTTOM
        }

        Point p;
        switch(direction) {
            case LEFT:
                p = getMinOrMax(newPoints, true, true, 2);
                return p != null && p.x < curr.x ? p : curr;
            case TOP:
                p = getMinOrMax(newPoints, false, true, 3);
                return p != null && p.y < curr.y ? p : curr;
            case RIGHT:
                p = getMinOrMax(newPoints, true, false, 0);
                return p != null && p.x > curr.x ? p : curr;
            case BOTTOM:
                p = getMinOrMax(newPoints, false, false, 1);
                return p != null && p.y > curr.y ? p : curr;
            default:
                return curr;
        }
    }
    private Point getMinOrMax (Point[] points, boolean x, boolean min, int ignoreIndex) {
        Point result = null;
        int index = 0;
        for (Point p: points) {
            if (index != ignoreIndex && p != null) {
                if (result == null) {
                    result = p;
                } else {
                    if (min) {
                        if (x) {
                            if (p.x < result.x) {
                                result = p;
                            }
                        } else {
                            if (p.y < result.y) {
                                result = p;
                            }
                        }
                    } else {
                        if (x) {
                            if (p.x > result.x) {
                                result = p;
                            }
                        } else {
                            if (p.y > result.y) {
                                result = p;
                            }
                        }
                    }
                }
            }
            index++;
        }
        return result;
    }
    private boolean isTransparent(int x, int y) {
        return  isTransparent(x,y,true);
    }
    private boolean isTransparent(int x, int y, boolean checkCoordinates) {
        int width = imageCopy.getWidth() - 1;
        int height = imageCopy.getHeight() - 1;
        if (checkCoordinates && !(x >= 0 && x < width && y >= 0 && y < height)) {
            return true;
        }
        int pixel = imageCopy.getPixel(x, y);
        return Color.alpha(pixel) == 0;
    }
    private boolean isTransparentOrColor(int x, int y, int color, boolean checkCoordinates) {
        int width = imageCopy.getWidth() - 1;
        int height = imageCopy.getHeight() - 1;
        if (checkCoordinates && !(x >= 0 && x < width && y >= 0 && y < height)) {
            return true;
        }
        int pixel = imageCopy.getPixel(x, y);
        return Color.alpha(pixel) == 0 || pixel == color;
    }


    /**
     *  Render
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (incrementalImage != null) {
            canvas.drawBitmap(incrementalImage, 0, 0, null);
        }
        if (currentTool != null) {
            currentTool.render(canvas);
        }

        // TODO: REMOVE
//        if (showBounds) {
//            Rect rect = getImageBounds();
//            if (rect != null) {
//                Paint p = new Paint();
//                p.setColor(Color.RED);
//
//                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, p);
//                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, p);
//                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, p);
//                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, p);
//            }
//        }
    }

    Bitmap drawBitmap() {
        Bitmap viewBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);
        draw(canvas);
        return viewBitmap;
    }

    /**
     *  eventListeners
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!blockEditedUpdates) {
            blockEditedUpdates = true;
        }
        boolean value = currentTool.onTouch(this, event);
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            setViewImage(drawBitmap());
            currentTool.clear();
            blockEditedUpdates = false;
        }

        return value;
    }

    /**
     *  cleanup
     */
    public void clear() {
        if (incrementalImage != null) {
            incrementalImage.recycle();
            incrementalImage = null;
        }
        if (imageCopy != null) {
            imageCopy.recycle();
            imageCopy = null;
        }
        currentTool.clear();
        invalidate();
    }

    public void destroy() {
        clear();
        instance = null;
    }
    /**
     *  getters and setters
     */
    public void setToolType(@Nullable Integer toolType) {
        if (toolType != null) {
            switch (toolType) {
                case SketchTool.TYPE_PEN:
                    currentTool = penTool;
                    return;
                case SketchTool.TYPE_ERASE:
                    currentTool = eraseTool;
                    return;
                case SketchTool.TYPE_CIRCLE:
                    currentTool = circleTool;
                    return;
                case SketchTool.TYPE_ARROW:
                    currentTool = arrowTool;
                    return;
            }
        }
        currentTool = penTool;
    }
    public void setToolColor(int toolColor) {
        ((ToolColor) penTool).setToolColor(toolColor);
        ((ToolColor) circleTool).setToolColor(toolColor);
        ((ToolColor) arrowTool).setToolColor(toolColor);
    }
    public void setToolThickness(float toolThickness) {
        ((ToolThickness) penTool).setToolThickness(toolThickness);
        ((ToolThickness) eraseTool).setToolThickness(toolThickness);
        ((ToolThickness) circleTool).setToolThickness(toolThickness);
        ((ToolThickness) arrowTool).setToolThickness(toolThickness);
    }
    public void setViewImage(Bitmap bitmap) {
        incrementalImage = bitmap;
        }

    public int getSelectedTool() {
        return currentTool.getTypeId();
    }
    public int getToolColor() {
        return ((ToolColor) penTool).getToolColor();
    }
    public float getToolThickness() {
        return ((ToolThickness) penTool).getToolThickness();
    }
    public Bitmap getImage() {
        croppedImageBounds = getImageBounds();

        if (incrementalImage != null) {
            return Bitmap.createBitmap(incrementalImage, croppedImageBounds.left, croppedImageBounds.top, croppedImageBounds.right - croppedImageBounds.left, croppedImageBounds.bottom - croppedImageBounds.top);
        }
        return null;
    }
    public Rect getCroppedImageBounds () {
        return croppedImageBounds;
    }
}
