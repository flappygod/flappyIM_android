package com.flappygo.flappyimdemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决dex大于65535的问题
        MultiDex.install(this);
    }


}
