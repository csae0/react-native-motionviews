package team.uptech.motionviews.widget.Worker;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;

import team.uptech.motionviews.viewmodel.Layer;
import team.uptech.motionviews.widget.entity.MotionEntity;

public class ShrinkWorker {
    public interface Constants {
        int SHRINK_DURATION = 500;
        float ACCELERATE_FACTOR = 1.5f;

    }
    private static ShrinkWorker instance = null;
    private static MotionEntity selectedEntity = null;

    private static boolean shrink = true;
    private static boolean finished = false;
    private static float minScale = 0;
    private static float initialScale = 0;

    private ValueAnimator shrinkAnimator;


    private ShrinkWorker (MotionEntity entity) {
        shrink = true;
        finished = false;
        selectedEntity = entity;
        Layer layer = entity.getLayer();
        minScale = layer.getMinScale();
        initialScale = layer.getScale();

        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(Constants.ACCELERATE_FACTOR);
        shrinkAnimator = ValueAnimator.ofFloat(initialScale, minScale);
        shrinkAnimator.setDuration(Constants.SHRINK_DURATION);
        shrinkAnimator.setInterpolator(accelerateInterpolator);
        shrinkAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finished = true;
//                reverse();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                finished = true;
                //                reverse();
//                start();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public static ShrinkWorker getInstance (MotionEntity entity) {
        if (instance == null) {
            instance = new ShrinkWorker(entity);
        } else if (selectedEntity != entity) {
            selectedEntity = entity;
            shrink = true;
            finished = false;
            Layer layer = entity.getLayer();
            minScale = layer.getMinScale();
            initialScale = layer.getScale();
        }
        return instance;
    }

    public void setUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        this.shrinkAnimator.addUpdateListener(updateListener);
    }

    public void reverseStart () {
        if (finished) {
            finished = false;
            shrink = !shrink;
            shrinkAnimator.reverse();
            start();
        } else {
            shrinkAnimator.cancel();
            reverseStart();
        }
    }

    public void start () {
        if (!shrinkAnimator.isRunning() && !finished) {
            shrinkAnimator.start();
        }
    }
    public boolean isShrink() {
        return shrink;
    }
}