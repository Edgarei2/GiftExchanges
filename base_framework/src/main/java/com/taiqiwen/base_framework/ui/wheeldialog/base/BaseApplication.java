package com.taiqiwen.base_framework.ui.wheeldialog.base;

import android.app.Application;
import android.content.Context;


public class BaseApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * 获取上下文
     * @return Context
     */
    public static Context getContext() {
        return context;
    }
}
