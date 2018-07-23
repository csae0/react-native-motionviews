package com.sketchView.tools.Blueprints;

import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.almeros.android.multitouch.BaseGestureDetector;


public abstract class GestureDetectorSketchTool extends PathTrackingSketchTool {

    BaseGestureDetector detector;

    public GestureDetectorSketchTool(View touchView) {
        super(touchView);
        detector = createDetector();
    }

    public void registerDetector(MotionEvent event) {
        if (detector != null) {
            detector.onTouchEvent(event);
        }
    }

    @Nullable public abstract BaseGestureDetector createDetector();
}
