package com.sketchView.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.almeros.android.multitouch.EditGestureDetector;
import com.sketchView.tools.Blueprints.GestureDetectorSketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;
import com.sketchView.utils.ToolUtils;

import team.uptech.motionviews.utils.MathUtils;
import team.uptech.motionviews.widget.Interfaces.Limits;

public class CircleSketchTool extends GestureDetectorSketchTool implements ToolColor, ToolThickness {

    private static final float DEFAULT_THICKNESS = 5;
    private static final float DEFAULT_RADIUS = (DEFAULT_THICKNESS / 2) * 5;
    private static final int DEFAULT_COLOR = Limits.INITIAL_FONT_COLOR; // Color.BLACK;

    private float toolThickness;
    private int toolColor;

    private PointF center;
    private float radiusX, radiusY, minRadius;
    private Paint paint = new Paint();

    public CircleSketchTool (View touchView) {
        super(touchView);

        setToolColor(DEFAULT_COLOR);
        setToolThickness(DEFAULT_THICKNESS);

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        center = new PointF(0f,0f);
        radiusX = radiusY = minRadius = DEFAULT_RADIUS;
    }

    // TODO: Add and fix editGestureDetector to make circle to oval with second finger gesture
    @Override
    public EditGestureDetector createDetector() {
        return null;
    }
//        return new EditGestureDetector(touchView.getContext(), new OnEditGestureListener() {
//            @Override
//            public boolean onEdit(EditGestureDetector detector) {
//                clear();
//                MotionEvent event = detector.getmCurrEvent();
//                float currSpan = detector.getCurrentSpan();
//                float prevSpan = detector.getPreviousSpan();
//                center = new PointF(event.getX(), event.getY());
//                radiusY += (prevSpan - currSpan) / 100;
//                path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
//                touchView.invalidate();
//                return false;
//            }
//
//            @Override
//            public boolean onEditBegin(EditGestureDetector detector) {
//                clear();
//                MotionEvent event = detector.getmCurrEvent();
//                float currSpan = detector.getCurrentSpan();
//                float prevSpan = detector.getPreviousSpan();
//                radiusY += (prevSpan - currSpan) / 100;
//                path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
//                touchView.invalidate();
//                return true;
//            }
//
//            @Override
//            public void onEditEnd(EditGestureDetector detector) {
//                clear();
//                MotionEvent event = detector.getmCurrEvent();
//                float currSpan = detector.getCurrentSpan();
//                float prevSpan = detector.getPreviousSpan();
//                radiusY += (prevSpan - currSpan) / 100;
//                path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
//                touchView.invalidate();
//            }
//        });
//    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        center = new PointF(event.getX(), event.getY());
        path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        clear();
        radiusX = radiusY = getNewRadius(event);
        path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
        touchView.invalidate();
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        clear();
        radiusX = radiusY = getNewRadius(event);
        path.addOval(new RectF(center.x + radiusX, center.y + radiusY, center.x - radiusX, center.y - radiusY), Path.Direction.CW);
        touchView.invalidate();
    }

    private float getNewRadius(MotionEvent event) {
        float newRadius = MathUtils.vectorLength(center, new PointF(event.getX(), event.getY()));
        return newRadius > minRadius ? newRadius : minRadius;
    }

    @Override
    public void setToolThickness(float toolThickness) {
        this.toolThickness = toolThickness;
        minRadius = (toolThickness / 2) * 5;
        paint.setStrokeWidth(ToolUtils.ConvertDPToPixels(touchView.getContext(), toolThickness));
    }

    @Override
    public float getToolThickness() {
        return toolThickness;
    }

    @Override
    public void setToolColor(int toolColor) {
        this.toolColor = toolColor;
        paint.setColor(toolColor);
    }

    @Override
    public int getToolColor() {
        return toolColor;
    }
}
