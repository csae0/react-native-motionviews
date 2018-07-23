package team.uptech.motionviews.widget.Interfaces;

import com.almeros.android.multitouch.MoveGestureDetector;

/**
 * Listener which must be implemented which is used by MoveGestureDetector
 * to perform callbacks to any implementing class which is registered to a
 * MoveGestureDetector via the constructor.
 *
 * @see MoveGestureDetector.SimpleOnMoveGestureListener
 */
public interface OnMoveGestureListener {
    boolean onMove(MoveGestureDetector detector);
    boolean onMoveBegin(MoveGestureDetector detector);
    void onMoveEnd(MoveGestureDetector detector);
}
