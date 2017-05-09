package com.cxb.familytree;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 * Created by lenovo on 17/5/6.
 */

public class MyApplication extends Application {

    private static MyApplication INStANCE;

    public MyApplication() {
        INStANCE = this;
    }

    public static MyApplication getInstance() {
        if (INStANCE == null) {
            synchronized (MyApplication.class) {
                if (INStANCE == null) {
                    INStANCE = new MyApplication();
                }
            }
        }
        return INStANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init(getString(R.string.app_name));
    }
}
