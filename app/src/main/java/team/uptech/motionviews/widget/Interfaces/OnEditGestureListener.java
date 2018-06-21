package team.uptech.motionviews.widget.Interfaces;

import com.almeros.android.multitouch.EditGestureDetector;

public interface OnEditGestureListener {
    boolean onEdit(EditGestureDetector detector);
    boolean onEditBegin(EditGestureDetector detector);
    void onEditEnd(EditGestureDetector detector);
}
