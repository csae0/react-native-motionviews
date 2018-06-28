package com.sketchView.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.almeros.android.multitouch.EditGestureDetector;
import com.sketchView.tools.Blueprints.GestureDetectorSketchTool;
import com.sketchView.tools.Blueprints.ToolColor;
import com.sketchView.tools.Blueprints.ToolThickness;
import com.sketchView.utils.ToolUtils;

import team.uptech.motionviews.utils.MathUtils;
import team.uptech.motionviews.widget.Interfaces.Limits;

public class ArrowSketchTool extends GestureDetectorSketchTool implements ToolColor, ToolThickness {
        private static final float DEFAULT_THICKNESS = 5;
        private static final float DEFAULT_MAGNITUDE_FACTOR = 15;
        private static final float DEFAULT_MAGNITUDE_TOP_RELATION_FACTOR = 3;
        private static final float DEFAULT_MAGNITUDE = DEFAULT_THICKNESS * DEFAULT_MAGNITUDE_FACTOR;
        private static final int DEFAULT_COLOR = Limits.INITIAL_FONT_COLOR; // Color.BLACK;
        private static final int ARROW_TOP_ANGEL_FROM_BODY = 30;
        private float toolThickness;
        private int toolColor;

        private PointF tail;
        private float minBodyMagnitude;
        private Paint paint = new Paint();

        public ArrowSketchTool (View touchView) {
            super(touchView);

            setToolColor(DEFAULT_COLOR);
            setToolThickness(DEFAULT_THICKNESS);

            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);

            tail = new PointF(0f,0f);
            minBodyMagnitude = DEFAULT_MAGNITUDE;
        }

        // TODO: Add and fix editGestureDetector to make circle to oval with second finger gesture
        @Override
        public EditGestureDetector createDetector() {
            return null;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public void onTouchDown(MotionEvent event) {
            tail = new PointF(event.getX(), event.getY());
        }

        @Override
        public void onTouchMove(MotionEvent event) {
            clear();
            createArrowPath(event);
            touchView.invalidate();
        }

        @Override
        public void onTouchUp(MotionEvent event) {
            clear();
            createArrowPath(event);
            touchView.invalidate();
        }

    @Override
    public int getTypeId() {
        return TYPE_ARROW;
    }

    private void createArrowPath (MotionEvent event) {
            PointF top = getNewTop(event);
            PointF leftArrowStrokeTop = getArrowStrokeTop(tail, top, -(ARROW_TOP_ANGEL_FROM_BODY + 90));
            PointF rightArrowStrokeTop = getArrowStrokeTop(tail, top, ARROW_TOP_ANGEL_FROM_BODY - 90);
            path.moveTo(tail.x, tail.y);
            path.lineTo(top.x, top.y);
            path.lineTo(leftArrowStrokeTop.x, leftArrowStrokeTop.y);
            path.moveTo(top.x, top.y);
            path.lineTo(rightArrowStrokeTop.x, rightArrowStrokeTop.y);
        }

        private float getNewMagnitude(MotionEvent event) {
            return MathUtils.vectorLength(tail, new PointF(event.getX(), event.getY()));
        }

        private PointF getNewTop (MotionEvent event) {
            float magnitude = getNewMagnitude(event);
            if (magnitude >= minBodyMagnitude) {
                return new PointF(event.getX(), event.getY());
            } else {
                PointF direction = MathUtils.vectorDirection(tail, new PointF(event.getX(), event.getY()));
                return new PointF(tail.x + (direction.x * minBodyMagnitude), tail.y + (direction.y * minBodyMagnitude));
            }
        }

        private PointF getArrowStrokeTop(PointF arrowTail, PointF arrowTop, int angle) {
            PointF direction = MathUtils.rotatedVectorDirection(arrowTail, arrowTop, angle);
            float arrowBodyMagnitude = MathUtils.vectorLength(arrowTail, arrowTop);
            float arrowTopMagnitude = (arrowBodyMagnitude >= minBodyMagnitude ? arrowBodyMagnitude : minBodyMagnitude) / DEFAULT_MAGNITUDE_TOP_RELATION_FACTOR;

            return new PointF(arrowTop.x + (direction.x * arrowTopMagnitude) , arrowTop.y + (direction.y * arrowTopMagnitude));
        }

        @Override
        public void setToolThickness(float toolThickness) {
            this.toolThickness = toolThickness;
            minBodyMagnitude = toolThickness * DEFAULT_MAGNITUDE_FACTOR;
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

