package com.liu.alibrarytest.keeplive.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.AppUtils;

/**
 * Created by liu on 2018/10/25.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService
{
//    private Handler handler = new Handler(new Handler.Callback()
//    {
//        @Override
//        public boolean handleMessage(Message msg)
//        {
//
//            jobFinished((JobParameters) msg.obj, false);
//            return true;
//        }
//    });

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params)
    {
        createJob(this);
        if (!AppUtils.isServiceExist(this, GoKLService.class))
        {
            Intent intent = new Intent(this, GoKLService.class);
            startService(intent);
        }
        Toast.makeText(this, "Job执行", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {
        handler.removeMessages(0x11);
        return false;
    }

    public static void createJob(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)return;
        JobInfo.Builder b = new JobInfo.Builder(0x2, new ComponentName(context, AliveJobService.class));
        if (Build.VERSION.SDK_INT >= 24)
        {
            b.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            b.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            b.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);
        }else
        {
            b.setPeriodic(3000);
        }
        b.setPersisted(true);
        b.setRequiresCharging(true);
        JobScheduler sch = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        try
        {
            if (sch.schedule(b.build()) <= 0)
            {
                Toast.makeText(context, "创建job失败", Toast.LENGTH_LONG).show();
                LogUtils.LOGE(AliveJobService.class, "create job err");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
