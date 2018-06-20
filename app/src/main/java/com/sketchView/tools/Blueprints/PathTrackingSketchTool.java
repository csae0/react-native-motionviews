package com.sketchView.tools.Blueprints;

import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by keshav on 08/04/17.
 */

public abstract class PathTrackingSketchTool extends SketchTool {

    protected Path path = new Path();

    public PathTrackingSketchTool(View touchView) {
        super(touchView);
    }

    @Override
    public void clear() {
        path.reset();
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        path.moveTo(event.getX(), event.getY());
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        path.lineTo(event.getX(), event.getY());
        touchView.invalidate();
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        path.lineTo(event.getX(), event.getY());
        touchView.invalidate();
    }

    @Override
    public void onTouchCancel(MotionEvent event) {
        onTouchUp(event);
    }
}
