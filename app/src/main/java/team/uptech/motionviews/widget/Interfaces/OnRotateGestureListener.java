package team.uptech.motionviews.widget.Interfaces;

import com.almeros.android.multitouch.RotateGestureDetector;

/**
 * Listener which must be implemented which is used by RotateGestureDetector
 * to perform callbacks to any implementing class which is registered to a
 * RotateGestureDetector via the constructor.
 *
 * @see RotateGestureDetector.SimpleOnRotateGestureListener
 */
public interface OnRotateGestureListener {
    public boolean onRotate(RotateGestureDetector detector);
    public boolean onRotateBegin(RotateGestureDetector detector);
    public void onRotateEnd(RotateGestureDetector detector);
}
