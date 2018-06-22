package com.almeros.android.multitouch;

import android.content.Context;
import android.view.MotionEvent;

import team.uptech.motionviews.widget.Interfaces.OnEditGestureListener;

public class EditGestureDetector extends TwoFingerGestureDetector {

    private final OnEditGestureListener mListener;
    private boolean mSloppyGesture;
    public EditGestureDetector(Context context, OnEditGestureListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {

//        switch (actionCode) {
//            case MotionEvent.ACTION_POINTER_DOWN:
//                // At least the second finger is on screen now
//
//                resetState(); // In case we missed an UP/CANCEL event
//                mPrevEvent = MotionEvent.obtain(event);
//                mTimeDelta = 0;
//                updateStateByEvent(event);
//                mGestureInProgress = mListener.onEditBegin(this);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mGestureInProgress = mListener.onEditBegin(this);
//        }


        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:
                // At least the second finger is on screen now

                resetState(); // In case we missed an UP/CANCEL event
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;

                updateStateByEvent(event);

                // See if we have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event);
                if (!mSloppyGesture) {
                    // No, start gesture now
                    mGestureInProgress = mListener.onEditBegin(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mSloppyGesture) {
                    break;
                }

                // See if we still have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event);
                if (!mSloppyGesture) {
                    // No, start normal gesture now
                    mGestureInProgress = mListener.onEditBegin(this);
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (!mSloppyGesture) {
                    break;
                }

                break;
        }
    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_UP:
                // Gesture ended but
                updateStateByEvent(event);

                if (!mSloppyGesture) {
                    mListener.onEditEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!mSloppyGesture) {
                    mListener.onEditEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = mListener.onEdit(this);
                    if (updatePrevious) {
                        mPrevEvent.recycle();
                        mPrevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    protected void resetState() {
        super.resetState();
    }

    public float getDeltaSpan () {
        float curr = getCurrentSpan();
        float prev = getPreviousSpan();

        if (curr != -1 && prev != -1) {
            return curr - prev;
        }
        return 0.0f;
    }
}