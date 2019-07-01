package com.liu.lalibrary.ui;

import android.app.Activity;
import android.widget.TextView;

import com.liu.lalibrary.log.LogUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by liu on 2018/2/26.
 */

public class VerCodeCountDown
{
    private int startCount;
    private int counter;
    private TextView cdView;
    private String cdFormat = "%d";
    private ScheduledExecutorService service;
    private ScheduledFuture<?> task;
    private WeakReference<Activity> ownerActivity;
    private Runnable completeCB;

    public VerCodeCountDown(int counter, String cdFormat, TextView cdView, Activity activity, Runnable completeCB)
    {
        this.counter = counter;
        this.cdView = cdView;
        if (cdFormat != null) this.cdFormat = cdFormat;
        this.completeCB = completeCB;
        if (activity == null) return;
        ownerActivity = new WeakReference<Activity>(activity);
    }

    public void start()
    {
        if (ownerActivity == null)
        {
            LogUtils.LOGE(getClass(), "no activity");
            return;
        }
        if (service == null)
        {
            service = Executors.newSingleThreadScheduledExecutor();
        }
        startCount = counter;
        task = service.scheduleWithFixedDelay(taskRun, 0, 1, TimeUnit.SECONDS);
        cdView.setEnabled(false);
    }

    public void stop(boolean runCB)
    {
        if (task != null)
        {
            task.cancel(true);
            task = null;
        }
        if (service != null)
        {
            service.shutdown();
            service = null;
        }
        cdView.setEnabled(true);
        if (runCB && completeCB != null)
        {
            completeCB.run();
            completeCB = null;
        }
    }

    private Runnable taskRun = new Runnable()
    {
        @Override
        public void run()
        {
            Activity activity = ownerActivity.get();
            if (activity == null)
            {
                stop(false);
                return;
            }
            activity.runOnUiThread(uiRun);
        }
    };

    private Runnable uiRun = new Runnable()
    {
        @Override
        public void run()
        {
            cdView.setText(String.format(cdFormat, startCount--));
            if (startCount == 1)
            {
                stop(true);
                cdView.setEnabled(true);
            }
        }
    };
}
