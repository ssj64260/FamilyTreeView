package com.cxb.familytree.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

/**
 * 默认V7 AlertDialog
 */

public class DefaultAlertDialog {

    private AlertDialog alertDialog;

    public DefaultAlertDialog(Context context) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context).create();
        }
    }

    public void showDialog() {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
            customStyle();
        }
    }

    public void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void setTitle(String title) {
        alertDialog.setTitle(title);
    }

    public void setMessage(String message) {
        alertDialog.setMessage(message);
    }

    public void setCancelButton(String text) {
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void setCancelButton(String text, DialogInterface.OnClickListener onClickListener) {
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, text, onClickListener);
    }

    public void setConfirmButton(String text) {
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void setConfirmButton(String text, DialogInterface.OnClickListener onClickListener) {
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, text, onClickListener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        alertDialog.setOnDismissListener(onDismissListener);
    }

    private void customStyle() {
        Button confirm = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button cancel = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

        if (confirm != null) {
            confirm.setTextColor(Color.parseColor("#08A760"));
        }

        if (cancel != null) {
            cancel.setTextColor(Color.parseColor("#525252"));
        }
    }
}
