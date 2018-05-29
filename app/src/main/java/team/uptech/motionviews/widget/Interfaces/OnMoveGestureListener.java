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
    public boolean onMove(MoveGestureDetector detector);
    public boolean onMoveBegin(MoveGestureDetector detector);
    public void onMoveEnd(MoveGestureDetector detector);
}
