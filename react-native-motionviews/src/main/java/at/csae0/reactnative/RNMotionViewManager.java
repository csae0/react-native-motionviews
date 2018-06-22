package at.csae0.reactnative;

import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import team.uptech.motionviews.ui.adapter.MotionViewContainer;

public class RNMotionViewManager extends SimpleViewManager<MotionViewContainer> {

    public static final String REACT_CLASS = "RNMotionView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MotionViewContainer createViewInstance(ThemedReactContext context) {
        return new MotionViewContainer(context);
    }

}