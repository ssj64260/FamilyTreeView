package com.cxb.familytree.utils;

import android.content.Context;
import android.widget.Toast;

import com.cxb.familytree.MyApplication;


/**
 * toast优化工具
 */

public class ToastMaster {

    private static Toast sToast = null;

    private ToastMaster() {

    }

    public static void toast(String content) {
        showToast(Toast.makeText(MyApplication.getInstance(), content, Toast.LENGTH_SHORT));
    }

    public static void toast(Context context, String content) {
        showToast(Toast.makeText(context, content, Toast.LENGTH_SHORT));
    }

    public static void toast(Context context, String content, int duration) {
        showToast(Toast.makeText(context, content, duration));
    }

    public static void showToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
        sToast.show();
    }

    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }

}
