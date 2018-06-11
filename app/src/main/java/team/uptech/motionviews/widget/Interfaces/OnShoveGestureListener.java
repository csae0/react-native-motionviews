package team.uptech.motionviews.widget.Interfaces;


import com.almeros.android.multitouch.ShoveGestureDetector;

/**
 * Listener which must be implemented which is used by ShoveGestureDetector
 * to perform callbacks to any implementing class which is registered to a
 * ShoveGestureDetector via the constructor.
 *
 * @see ShoveGestureDetector.SimpleOnShoveGestureListener
 */
public interface OnShoveGestureListener {
    boolean onShove(ShoveGestureDetector detector);
    boolean onShoveBegin(ShoveGestureDetector detector);
    void onShoveEnd(ShoveGestureDetector detector);
}
