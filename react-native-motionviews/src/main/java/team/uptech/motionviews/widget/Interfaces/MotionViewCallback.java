package team.uptech.motionviews.widget.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import team.uptech.motionviews.widget.entity.MotionEntity;

public interface MotionViewCallback {
    void onEntitySelected(@Nullable MotionEntity entity);
    void onEntityDoubleTap(@NonNull MotionEntity entity);
    void onEntitySingleTapConfirmed(@NonNull MotionEntity entity);
}