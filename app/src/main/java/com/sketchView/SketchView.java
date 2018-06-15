package com.sketchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sketchView.tools.EraseSketchTool;
import com.sketchView.tools.PenSketchTool;
import com.sketchView.tools.Blueprints.SketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;

import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.widget.Interfaces.SketchViewCallback;

import static com.sketchView.SketchView.DIRECTION.BOTTOM;
import static com.sketchView.SketchView.DIRECTION.LEFT;
import static com.sketchView.SketchView.DIRECTION.RIGHT;
import static com.sketchView.SketchView.DIRECTION.TOP;

import team.uptech.motionviews.R;

/**
 * Created by keshav on 05/04/17.
 */

public class SketchView extends View {

    enum DIRECTION { HORIZONTAL, VERTICAL, LEFT, TOP, RIGHT, BOTTOM, UNDEFINED }

    static SketchView instance = null;

    SketchTool currentTool;
    SketchTool penTool;
    SketchTool eraseTool;
    Bitmap incrementalImage;
    private boolean blockEditedUpdates;

    private SketchViewCallback callback;

    // TODO: remove debug variables
    private boolean showBounds = false;
    public LinearLayout linearLayout;

    public static SketchView getInstance(Context context) {
        if (instance == null) {
            instance = new SketchView(context);
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
        // setToolThickness(20);
        // setToolColor(Color.BLUE);
        setBackgroundColor(Color.TRANSPARENT);
        // setBackgroundColor(Color.RED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Context context = this.getContext();
        linearLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        Button show = new Button(context);
        show.setText("SHOW");
        show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBounds = !showBounds;
                invalidate();
            }
        });

        Button save = new Button(context);
        save.setText("SAVE");
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.closeAndCreateEntity(getImage(), getToolColor(), (int)getToolThickness());
            }
        });

        RelativeLayout parentView = (RelativeLayout) getParent();

        if (parentView != null) {
            linearLayout.addView(show);
            linearLayout.addView(save);
            parentView.addView(linearLayout);
            parentView.invalidate();
        }
        invalidate();
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

    public int getToolColor() {
        return ((ToolColor) penTool).getToolColor();
    }
    public float getToolThickness() {
        return ((ToolThickness) penTool).getToolThickness();
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
//        Rect bounds = getImageBounds();

        if (incrementalImage != null) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.abra);
//            return Bitmap.createBitmap(incrementalImage, bounds.left, bounds.top, bounds.right - bounds.left, bounds.bottom - bounds.top);
        }
        return null;
    }


    @Nullable
    private Rect[] getImageBounds() {
        if (incrementalImage != null) {
            Bitmap imageCopy = Bitmap.createScaledBitmap(incrementalImage, incrementalImage.getWidth(), incrementalImage.getHeight(), false);

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
            Point minLeft = findExactBounds(imageCopy, LEFT, minLeftP, 1);
            Point minTop = findExactBounds(imageCopy, TOP, minTopP, 1);
            Point maxRight = findExactBounds(imageCopy, RIGHT, maxRightP, 1);
            Point maxBottom = findExactBounds(imageCopy, BOTTOM, maxBottomP, 1);
            if (minLeftP != null && minTopP != null && maxRightP != null && maxBottomP != null) {
                return new Rect[]{ new Rect(minLeft == null ? minLeftP.x : minLeft.x,
                        minTop == null ? minTopP.y : minTop.y,
                        maxRight == null ? maxRightP.x : maxRight.x,
                        maxBottom == null ? maxBottomP.y : maxBottom.y),
                new Rect(minLeftP.x, minTopP.y, maxRightP.x, maxBottomP.y)};
            }
        }
        return null;
    }

    private Point findExactBounds (Bitmap image, DIRECTION direction, Point curr, int step) {

        Point[] newPoints = new Point[4];

        if (isTransparentOrColor(image, curr.x, curr.y , Color.GREEN, true)) {
            return null;
        }

        incrementalImage.setPixel(curr.x, curr.y, Color.GREEN);

        if (direction != RIGHT) {
            newPoints[0] = findExactBounds(image, direction, new Point(curr.x - step, curr.y), step); // LEFT
        }
        if (direction != BOTTOM) {
            newPoints[1] = findExactBounds(image, direction, new Point(curr.x ,curr.y - step), step); // TOP
        }
        if (direction != LEFT) {
            newPoints[2] = findExactBounds(image, direction, new Point(curr.x + step ,curr.y), step); // RIGHT
        }
        if (direction != TOP) {
            newPoints[3] = findExactBounds(image, direction, new Point(curr.x ,curr.y + step), step); // BOTTOM
        }

        Point p;
        switch(direction) {
            case LEFT:
                p = getMax(newPoints, true, true, 2);
                return p != null && p.x < curr.x ? p : curr;
            case TOP:
                p = getMax(newPoints, false, true, 3);
                return p != null && p.y < curr.y ? p : curr;
            case RIGHT:
                p = getMax(newPoints, true, false, 0);
                return p != null && p.x > curr.x ? p : curr;
            case BOTTOM:
                p = getMax(newPoints, false, false, 1);
                return p != null && p.y > curr.y ? p : curr;
            default:
                return curr;
        }
    }
    private Point getMax (Point[] points, boolean x, boolean min, int ignoreIndex) {
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
    private boolean isTransparentOrColor(Bitmap image, int x, int y, int color, boolean checkCoordinates) {
        if (checkCoordinates && !(x >= 0 && x < incrementalImage.getWidth() && y >= 0 && y < incrementalImage.getWidth())) {
            return true;
        }
        int pixel = incrementalImage.getPixel(x, y);
        return Color.alpha(pixel) == 0 || pixel == color;
    }

    private void closeSketchView () {
        ((RelativeLayout)getParentForAccessibility()).removeView(this);
        //TODO: close
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

        if (showBounds) {
            Rect[] rect = getImageBounds();
            if (rect != null) {
                Paint p = new Paint();
                Paint pp = new Paint();

                p.setColor(Color.RED);
                pp.setColor(Color.BLUE);

                canvas.drawLine(rect[0].left, rect[0].top, rect[0].left, rect[0].bottom, p);
                canvas.drawLine(rect[0].right, rect[0].top, rect[0].right, rect[0].bottom, p);
                canvas.drawLine(rect[0].left, rect[0].top, rect[0].right, rect[0].top, p);
                canvas.drawLine(rect[0].left, rect[0].bottom, rect[0].right, rect[0].bottom, p);

                canvas.drawLine(rect[1].left, rect[1].top, rect[1].left, rect[1].bottom, pp);
                canvas.drawLine(rect[1].right, rect[1].top, rect[1].right, rect[1].bottom, pp);
                canvas.drawLine(rect[1].left, rect[1].top, rect[1].right, rect[1].top, pp);
                canvas.drawLine(rect[1].left, rect[1].bottom, rect[1].right, rect[1].bottom, pp);
            }
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
        }

        return value;
    }

    public void setCallback(SketchViewCallback callback) {
        this.callback = callback;
    }
}
