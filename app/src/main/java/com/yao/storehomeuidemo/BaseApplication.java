package com.yao.storehomeuidemo;

import android.app.Application;

/**
 * Created by szh on 2017/9/6.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
