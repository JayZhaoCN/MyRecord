package com.hfut.zhaojiabao.myrecord;

import android.app.Application;

import com.hfut.zhaojiabao.JayDaoManager;

/**
 * @author zhaojiabao 2017/5/19
 */

public class JayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JayDaoManager.init(this);

    }
}
