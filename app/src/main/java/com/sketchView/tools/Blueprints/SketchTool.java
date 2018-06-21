package com.sketchView.tools.Blueprints;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by keshav on 08/04/17.
 */

public abstract class SketchTool implements View.OnTouchListener {

    public static final int TYPE_PEN = 0;
    public static final int TYPE_ERASE = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final int TYPE_ARROW = 3;

    protected View touchView;
    public SketchTool(View touchView) {
        this.touchView = touchView;
    }

    public abstract void render(Canvas canvas);

    public abstract void clear();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        registerDetector(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event);
                break;
        }
        return true;
    }

    protected void registerDetector (MotionEvent event) {
        // Override to use
    }

    public abstract void onTouchDown(MotionEvent event);

    public abstract void onTouchMove(MotionEvent event);

    public abstract void onTouchUp(MotionEvent event);

    public abstract void onTouchCancel(MotionEvent event);

}
