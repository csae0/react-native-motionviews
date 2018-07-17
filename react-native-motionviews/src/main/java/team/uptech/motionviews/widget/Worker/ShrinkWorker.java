package team.uptech.motionviews.widget.Worker;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;

import team.uptech.motionviews.viewmodel.Layer;
import team.uptech.motionviews.widget.Interfaces.ShrinkWorkerCallback;
import team.uptech.motionviews.widget.entity.MotionEntity;

public class ShrinkWorker {
    public interface Constants {
        int SHRINK_DURATION = 500;
        float ACCELERATE_FACTOR = 1.5f;
    }

    // Static variables
    private static ShrinkWorker instance = null;
    private static MotionEntity selectedEntity = null;

    private static boolean delete = false;
    private static boolean started = false;
    private static boolean hasCallback = false;
    private static boolean hasUpdateListener = false;

    private static float minScale = 0;
    private static float initialScale = 0;

    private static ShrinkWorkerCallback callback = null;
    private static ValueAnimator.AnimatorUpdateListener updateListener = null;

    private ValueAnimator shrinkAnimator;

    private ShrinkWorker (MotionEntity entity) {
        selectedEntity = entity;
        started = false;

        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(Constants.ACCELERATE_FACTOR);
        shrinkAnimator = ValueAnimator.ofFloat(initialScale, minScale);
        shrinkAnimator.setDuration(Constants.SHRINK_DURATION);
        shrinkAnimator.setInterpolator(accelerateInterpolator);
        if (hasUpdateListener) {
            shrinkAnimator.addUpdateListener(updateListener);
        }
        shrinkAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (hasCallback && delete) {
                    delete = false;
                    callback.deleteAfterAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    public static ShrinkWorker getInstance(MotionEntity entity) {
        if (entity != null) {
            if (instance == null) {
                instance = new ShrinkWorker(entity);
            } else if (selectedEntity != entity) {
                selectedEntity = entity;
                started = false;
            }

            Layer layer = entity.getLayer();
            minScale = layer.getMinScale();
            initialScale = layer.getScale();
            return instance;
        }
        return null;
    }

    public static void setUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        if (updateListener != null) {
            hasUpdateListener = true;
        }
        ShrinkWorker.updateListener = updateListener;
    }

    public static void setCallback(ShrinkWorkerCallback callback) {
        if (callback != null) {
            hasCallback = true;
        }
        ShrinkWorker.callback = callback;
    }

    public static boolean hasUpdateListener () {
        return hasUpdateListener;
    }

    public static boolean hasCallback () {
        return hasCallback;
    }


    public void reverseStart () {
        if (isStarted()) {
            started = false;
            shrinkAnimator.reverse();
        }
    }

    public void start () {
        if (!isRunning() && !isStarted()) {
            started = true;
            instance.shrinkAnimator.setFloatValues(initialScale, minScale);
            shrinkAnimator.start();
        }
    }

    public static void reset () {
        instance = null;
        selectedEntity = null;
        callback = null;
        updateListener = null;

        delete = false;
        started = false;
        hasCallback = false;
        hasUpdateListener = false;

        minScale = 0;
        initialScale = 0;
    }

    public static void release() {
        reset();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isRunning() {
        if (shrinkAnimator != null) {
            return shrinkAnimator.isRunning();
        }
        return false;
    }

    public void deleteSelectedEntity() {
        delete = true;
    }
}