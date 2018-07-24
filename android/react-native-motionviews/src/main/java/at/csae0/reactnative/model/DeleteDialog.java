package at.csae0.reactnative.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class DeleteDialog extends Dialog {

    public DeleteDialog(@Nullable Bundle bundle) {
        super(bundle);
    }

    @Override
    public void setAlertButtonData(@Nullable Bundle bundle) {
        if (bundle != null) {
            positiveLabel = bundle.getString("positiveLabel", "");
            negativeLabel = bundle.getString("negativeLabel", "");
        }
    }
}
