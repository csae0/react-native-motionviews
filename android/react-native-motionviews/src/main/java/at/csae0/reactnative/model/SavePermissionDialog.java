package at.csae0.reactnative.model;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SavePermissionDialog extends Dialog {

    private boolean granted;

    public SavePermissionDialog(@Nullable Bundle bundle) {
        super(bundle);

        if (bundle != null) {
            granted = bundle.getBoolean("granted", false);
        }
    }

    @Override
    protected void setAlertButtonData(@Nullable Bundle bundle) {
        positiveLabel = "OK";
    }

    @Override
    public void showDialog(Context context) {
        checkPermission(context);
    }

    public boolean getPermission () {
        return granted;
    }

    private boolean checkPermission (Context context) {
        if (!granted) {
            super.showDialog(context);
        }
        return granted;
    }

    @Override
    protected void reset() {
        granted = false;
    }
}
