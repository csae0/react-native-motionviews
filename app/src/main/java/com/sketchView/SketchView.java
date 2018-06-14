package com.sketchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sketchView.tools.EraseSketchTool;
import com.sketchView.tools.PenSketchTool;
import com.sketchView.tools.Blueprints.SketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;

import team.uptech.motionviews.utils.ConversionUtils;

/**
 * Created by keshav on 05/04/17.
 */

public class SketchView extends View {

    enum DIRECTION {
        HORIZONTAL,
        VERTICAL
    }

    static SketchView instance = null;

    SketchTool currentTool;
    SketchTool penTool;
    SketchTool eraseTool;
    Bitmap incrementalImage;
    Rect imageBounds = null;
    private boolean blockEditedUpdates;

    public static SketchView getInstance(Context context) {
        if (instance == null) {
            instance = new SketchView(context);
        } else {
//            instance.setAlpha(1);
        }
        return instance;
    }

    public SketchView(Context context) {
        super(context);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(500, 500);
//        setLayoutParams(layoutParams);

        blockEditedUpdates = false;
        penTool = new PenSketchTool(this);
        eraseTool = new EraseSketchTool(this);
        setToolType(SketchTool.TYPE_PEN);
        setToolThickness(20);
        // setToolColor(Color.BLUE);
        setBackgroundColor(Color.TRANSPARENT);
//        setBackgroundColor(Color.RED);
    }

    public void setToolType(int toolType) {
        switch (toolType) {
            case SketchTool.TYPE_PEN:
                currentTool = penTool;
                break;
            case SketchTool.TYPE_ERASE:
                currentTool = eraseTool;
                break;
            default:
                currentTool = penTool;
        }
    }

    public void setToolColor(int toolColor) {
        ((ToolColor) penTool).setToolColor(toolColor);
    }

    public void setToolThickness(float toolThickness) {
        ((ToolThickness) penTool).setToolThickness(toolThickness);
        ((ToolThickness) eraseTool).setToolThickness(toolThickness);
    }

    public void setViewImage(Bitmap bitmap) {
        incrementalImage = bitmap;
        invalidate();
    }

    Bitmap drawBitmap() {
        Bitmap viewBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);
        draw(canvas);
        return viewBitmap;
    }

    public void clear() {
        incrementalImage = null;
        currentTool.clear();
        invalidate();
    }

    public Bitmap getImage() {
        Rect bounds = getImageBounds();

        if (incrementalImage != null) {
            return Bitmap.createBitmap(incrementalImage, bounds.left, bounds.top, bounds.right - bounds.left, bounds.bottom - bounds.top);
        }
        return null;
    }


    @Nullable
    private Rect getImageBounds() {
        if (incrementalImage != null) {
            int density = ConversionUtils.getDensity();
            int toolThickness = (int)((ToolThickness)currentTool).getToolThickness();
            int step = (toolThickness > 5 ? 5 : toolThickness) * density;

            Point maxRightP = new Point(Integer.MIN_VALUE,0);
            Point maxBottomP = new Point(0, Integer.MIN_VALUE);
            Point minLeftP = new Point(Integer.MAX_VALUE, 0);
            Point minTopP = new Point(0, Integer.MAX_VALUE);

            int width = incrementalImage.getWidth() - 1;
            int height = incrementalImage.getHeight() - 1;
            boolean leftTop, leftBottom, rightTop, rightBottom;

            for (int left = 0; left < width / 2; left += step) {
                for (int top = 0; top < height / 2; top += step) {
                    leftTop = !isTransparent(left, top);
                    rightTop = !isTransparent(width - left, top);
                    leftBottom = !isTransparent(left, height - top);
                    rightBottom = !isTransparent(width - left, height - top);

                    if (leftTop || leftBottom) {
                        if (left < minLeftP.x) {
                            minLeftP.x = left;
                            minLeftP.y = leftTop ? left : height - top;
                        }
                        if (left > maxRightP.x) {
                            maxRightP.x = left;
                            maxRightP.y = leftTop ? left : height - top;
                        }
                    }
                    if (leftTop || rightTop) {
                        if (top < minTopP.y) {
                            minTopP.y = top;
                            minTopP.x = leftTop ? left : width - left;
                        }
                        if (top > maxBottomP.y) {
                            maxBottomP.y = top;
                            maxBottomP.x = leftTop ? left : width - left;
                        }
                    }
                    if (leftBottom || rightBottom) {
                        if (height - top < minTopP.y) {
                            minTopP.y = height - top;
                            minTopP.x = leftBottom ? left : width - left;
                        }
                        if (height - top > maxBottomP.y) {
                            maxBottomP.y = height - top;
                            maxBottomP.x = leftBottom ? left : width - left;
                        }
                    }
                    if (rightTop || rightBottom) {
                        if (width - left < minLeftP.x) {
                            minLeftP.x = width - left;
                            minLeftP.y = rightTop ? top : height - top;
                        }
                        if (width - left > maxRightP.x) {
                            maxRightP.x = width - left;
                            maxRightP.y = rightTop ? top : height - top;
                        }
                    }
                }
            }

// TODO: LOGIK FUNKTIONIERT, aber nur wenn user nicht nochmal zeichnet und dabei unter der schrittweite bleibt
//            if (imageBounds != null && imageBounds.left == minLeftP.x - step && imageBounds.top == minTopP.y - step && imageBounds.right == maxRightP.x + step && imageBounds.bottom == maxBottomP.y + step) {
//
//            if (imageBounds == null) {
//                imageBounds = new Rect(minLeftP.x - step, minTopP.y - step, maxRightP.x + step, maxBottomP.y + step);
//            } else if (!(imageBounds.left == minLeftP.x && imageBounds.top == minTopP.y && imageBounds.right == maxRightP.x && imageBounds.bottom == maxBottomP.y)) {
//                imageBounds = new Rect(imageBounds.left != minLeftP.x ? minLeftP.x - step : imageBounds.left,
//                        imageBounds.top != minTopP.y ? minTopP.y - step : imageBounds.top,
//                        imageBounds.right != maxRightP.x ? maxRightP.x + step : imageBounds.right,
//                        imageBounds.bottom != maxBottomP.y ? maxBottomP.y + step : imageBounds.bottom);
//
//            }
//            return imageBounds;
        }
        return null;
    }


//    private int[] findExactBounds (DIRECTION checkDirection, Point[] coordinates, int range, int density) {
//
//
////            int[] horizontalBounds = findExactBounds(DIRECTION.HORIZONTAL, new Point[]{minLeftP, minTopP, maxRightP, maxBottomP}, step, 1);
////            int[] verticalBounds = findExactBounds(DIRECTION.VERTICAL, new Point[]{minLeftP, minTopP, maxRightP, maxBottomP}, step, 1);
////           if (horizontalBounds != null && verticalBounds != null) {
////               return new Rect(horizontalBounds[0], verticalBounds[0], horizontalBounds[1], verticalBounds[1]);
////           } else {
//
//        int minLeft;
//        int minTop;
//        int maxRight;
//        int maxBottom;
//        int dimensionRange;
//        boolean leftTop, leftBottom, rightTop, rightBottom;
//
//        switch(checkDirection) {
//            case VERTICAL: {
//                minTop = coordinates[1].y;
//                maxBottom = coordinates[3].y;
//                dimensionRange = (coordinates[2].x - coordinates[0].x) + 2 * range;
//
//                for (int i = 0; i < range; i++) {
//                    for (int j = coordinates[0].x - range; j < dimensionRange / 2; j += density) {
//                        int topY = minTop - range + i;
//                        int bottomY = maxBottom + range - i;
//
//                        leftTop = !isTransparent(j, topY, true);
//                        rightTop = !isTransparent(dimensionRange - j, topY, true);
//                        leftBottom = !isTransparent(j, bottomY, true);
//                        rightBottom = !isTransparent(dimensionRange - j, bottomY, true);
//
//                        if ((leftTop || rightTop) && topY < minTop) {
//                            minTop = topY;
//                        }
//                        if ((leftBottom || rightBottom) && bottomY > maxBottom) {
//                            maxBottom = bottomY;
//                        }
//                    }
//                }
//                System.out.print("TOP: " + coordinates[1].y + " --> " + minTop + " | ");
//                System.out.println("BOTTOM: " + coordinates[3].y + " --> " + maxBottom);
//                return new int[]{minTop, maxBottom};
//            }
//            case HORIZONTAL: {
//                minLeft = coordinates[0].x;
//                maxRight = coordinates[2].x;
//                dimensionRange = (coordinates[3].y - coordinates[1].y) + 2 * range;
//
//                for (int i = 0; i < range; i++) {
//                    for (int j = coordinates[1].y - range; j < dimensionRange / 2; j += density) {
//                        int leftX = minLeft - range + i;
//                        int rightX = maxRight + range - i;
//
//                        leftTop = !isTransparent(leftX, j, true);
//                        leftBottom = !isTransparent(leftX, dimensionRange - j, true);
//                        rightTop = !isTransparent(rightX, j, true);
//                        rightBottom = !isTransparent(rightX, dimensionRange - j, true);
//
//                        if ((leftTop || leftBottom) && leftX < minLeft) {
//                            minLeft = leftX;
//                        }
//                        if ((rightTop || rightBottom) && rightX > maxRight) {
//                            maxRight = rightX;
//                        }
//                    }
//                }
//                System.out.print("LEFT: " + coordinates[0].x + " --> " + minLeft + " | ");
//                System.out.println("RIGHT: " + coordinates[2].x + " --> " + maxRight);
//                return new int[]{minLeft, maxRight};
//            }
//        }
//        return null;
//    }

    private boolean isTransparent(int x, int y) {
        return  isTransparent(x,y, false);
    }

    private boolean isTransparent(int x, int y, boolean checkCoordinates) {
        if (checkCoordinates && !(x >= 0 && x < incrementalImage.getWidth() && y >= 0 && y < incrementalImage.getWidth())) {
            return true;
        }
        return Color.alpha(incrementalImage.getPixel(x, y)) == 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (incrementalImage != null) {
            canvas.drawBitmap(incrementalImage, getLeft(), getTop(), null);
        }
        if (currentTool != null) {
            currentTool.render(canvas);
        }

        Rect rect = getImageBounds();
        if (rect != null) {
            Paint p = new Paint();
            p.setColor(Color.RED);
            
            canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, p);
            canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, p);
            canvas.drawLine(rect.left, rect.top, rect.right, rect.top, p);
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, p);
        }
    }

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

            // getImage();
//            setAlpha(0);
        }

        return value;
    }
}
