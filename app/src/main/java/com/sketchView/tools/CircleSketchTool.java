package com.sketchView.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.sketchView.tools.Blueprints.PathTrackingSketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;
import com.sketchView.utils.ToolUtils;

import team.uptech.motionviews.utils.MathUtils;
import team.uptech.motionviews.widget.Interfaces.Limits;
import team.uptech.motionviews.widget.entity.MotionEntity;

public class CircleSketchTool extends PathTrackingSketchTool implements ToolColor, ToolThickness {

    private static final float DEFAULT_THICKNESS = 5;
    private static final float DEFAULT_RADIUS = 10;
    private static final int DEFAULT_COLOR = Limits.INITIAL_FONT_COLOR; // Color.BLACK;

    private float toolThickness;
    private int toolColor;

    private PointF center;
    private float radius;
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
        radius = DEFAULT_RADIUS;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        center = new PointF(event.getX(), event.getY());
        path.addCircle(center.x, center.y, radius, Path.Direction.CW);
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        clear();
        radius = getNewRadius(event);
        path.addCircle(center.x, center.y, radius, Path.Direction.CW);
        touchView.invalidate();
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        clear();
        radius = getNewRadius(event);
        path.addCircle(center.x, center.y, radius, Path.Direction.CW);
        touchView.invalidate();
    }

    private float getNewRadius(MotionEvent event) {
        return MathUtils.hypotenuse(center, new PointF(event.getX(), event.getY()));
    }

    @Override
    public void setToolThickness(float toolThickness) {
        this.toolThickness = toolThickness;
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
