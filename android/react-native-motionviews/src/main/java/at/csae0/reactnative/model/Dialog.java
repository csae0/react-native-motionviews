package at.csae0.reactnative.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class Dialog {

    private String title, body;

    protected String positiveLabel , negativeLabel;
    protected DialogInterface.OnClickListener positiveAction, negativeAction;

    public Dialog(@Nullable Bundle bundle) {
        basicReset();
        reset();

        if (bundle != null) {
            title = bundle.getString("title", "");
            body = bundle.getString("body", "");
        }
        setAlertButtonData(bundle);
    }

    protected abstract void setAlertButtonData (@Nullable Bundle bundle);

    public void showDialog (Context context, @Nullable DialogInterface.OnClickListener positiveAction, @Nullable DialogInterface.OnClickListener negativeAction) {
        if (positiveAction != null) {
            this.positiveAction = positiveAction;
        }
        if (negativeAction != null) {
            this.negativeAction = negativeAction;
        }
        showDialog(context);
    }

    public void showDialog (Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
//        } else {
//            builder = new AlertDialog.Builder(context);
//        }

        if (title.length() > 0) {
            builder.setTitle(title);
        }
        if (body.length() > 0) {
            builder.setMessage(body);
        }
        if (positiveLabel.length() > 0 && positiveAction != null) {
            builder.setPositiveButton(positiveLabel, positiveAction);
        }
        if (negativeLabel.length() > 0 && negativeAction != null) {
            builder.setNegativeButton(negativeLabel, negativeAction);
        }

        builder.show();
    }

    protected void reset(){
        // May be implemented
    }

    private void basicReset() {
        positiveLabel = negativeLabel = title = body = "";
        positiveAction = negativeAction = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
    }
}
