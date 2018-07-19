package at.csae0.reactnative.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class SavePermission extends Permission {

    public SavePermission (@Nullable Bundle bundle) {
        super(bundle);
        setAlertButtonData();
    }

    @Override
    public void setAlertButtonData() {
        positiveLabel = "OK";
    }
}
