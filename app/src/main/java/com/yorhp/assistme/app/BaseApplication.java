package com.yorhp.assistme.app;

import android.app.Application;

import toast.ToastUtil;

/**
 * @author Tyhj
 * @date 2020-01-13
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
    }

}
