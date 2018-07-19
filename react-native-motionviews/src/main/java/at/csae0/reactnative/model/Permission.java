package at.csae0.reactnative.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class Permission {

    private boolean granted;
    private String title, body;

    protected String positiveLabel , negativeLabel;
    protected DialogInterface.OnClickListener positiveAction, negativeAction;

    public Permission (@Nullable Bundle bundle) {
        reset();

        if (bundle != null) {
            granted = bundle.getBoolean("granted", false);
            title = bundle.getString("title", "");
            body = bundle.getString("body", "");
        }
    }

    public abstract void setAlertButtonData ();

    private void showDialog (Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);;

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

    public boolean checkPermission (Context context) {
        if (!granted) {
            showDialog(context);
        }
        return granted;
    }

    private void reset() {
        positiveLabel = negativeLabel = title = body = "";
        positiveAction = negativeAction = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        granted = false;
    }
}
