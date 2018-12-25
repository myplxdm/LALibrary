package com.liu.alibrarytest.keeplive;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.liu.alibrarytest.keeplive.service.AliveJobService;
import com.liu.alibrarytest.keeplive.service.GoKLService;
import com.liu.alibrarytest.keeplive.service.ProtService;
import com.liu.lalibrary.utils.AppUtils;

/**
 * Created by liu on 2018/10/23.
 */

public class KeepliveManager
{
    private ScreenReceiver receiver;

    private static class SingletonHolder
    {
        private static final KeepliveManager INSTANCE = new KeepliveManager();
    }

    public static final KeepliveManager inst()
    {
        return KeepliveManager.SingletonHolder.INSTANCE;
    }

    public ScreenReceiver getReceiver()
    {
        return receiver;
    }

    public void startKepplive(Activity activity)
    {
        Intent intent = new Intent(activity, GoKLService.class);
        activity.startService(intent);
        intent = new Intent(activity, ProtService.class);
        activity.startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AliveJobService.createJob(activity);
        }
        if (receiver == null)
        {
            receiver = new ScreenReceiver(activity);
            receiver.startReceiver(SinglePixelActivity.class, null);
        }
        AppUtils.isIgnoringBatteryOpt(activity, true);
    }

    public void stopKeeplive(Activity activity)
    {
        Intent intent = new Intent(activity, GoKLService.class);
        activity.stopService(intent);
        intent = new Intent(activity, ProtService.class);
        activity.stopService(intent);
        receiver.stopScreenReceiverListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ((JobScheduler)activity.getSystemService(Context.JOB_SCHEDULER_SERVICE)).cancelAll();
        }
    }
}
