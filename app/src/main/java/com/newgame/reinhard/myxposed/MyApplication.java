package com.newgame.reinhard.myxposed;

import android.app.Application;

import timber.log.Timber;

/**
 * Desc.
 *
 * @author 李剑波
 * @date 17/3/14
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化Timber（log工具类）
        Timber.plant(new Timber.DebugTree());
    }

}
