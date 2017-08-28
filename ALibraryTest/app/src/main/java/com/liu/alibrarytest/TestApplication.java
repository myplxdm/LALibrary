package com.liu.alibrarytest;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liu on 2017/8/28.
 */

public class TestApplication extends Application
{
    private RefWatcher refWatcher;
    @Override
    public void onCreate()
    {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        TestApplication application = (TestApplication) context.getApplicationContext();
        return application.refWatcher;
    }

}
