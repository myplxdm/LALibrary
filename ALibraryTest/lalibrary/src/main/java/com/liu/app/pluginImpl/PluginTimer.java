package com.liu.app.pluginImpl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPlugin;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liu on 2017/11/8.
 */

public class PluginTimer extends PluginBase
{
    public static final String NAME = "timer";
    public static String CMD_START = "start";
    public static String PAM_COUNT = "count";//int
    //
    public static String RES_TIMING_START = "startTiming";
    public static String RES_TIMING_CANCEL = "cancelTiming";
    public static String RES_TIMING_COINTE = "counterTiming";

    private Timer timer;
    private long pauseTime;
    private int counter;//倒计时秒数
    private int countering;//正在倒计时秒数
    private boolean isStartTimer;

    private IPluginEvent event;


    public PluginTimer(AbsActivity activity)
    {
        super(activity);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescribe()
    {
        return NAME;
    }

    @Override
    public boolean exec(String cmd, JSONObject params, IPluginEvent event)
    {
        if (event != null && !TextUtils.isEmpty(cmd) && cmd.equals(CMD_START) && params != null && params.containsKey(PAM_COUNT))
        {
            this.event = event;
            counter = params.getIntValue(PAM_COUNT);
            startTime();
        }

        return false;
    }

    @Override
    public void stopPlugin()
    {
        cancelTime();
    }

    private void cancelTime()
    {
        if (event != null) event.pluginResult(true, RES_TIMING_CANCEL, null);
        isStartTimer = false;
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    private void startTime()
    {
        cancelTime();
        if (timer == null)
        {
            timer = new Timer();
        }
        event.pluginResult(true, RES_TIMING_START, null);
        countering = counter;
        isStartTimer = true;
        pauseTime = 1;

        final AbsActivity activity = getActivity();
        if (activity == null)return;
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        event.pluginResult(true, RES_TIMING_COINTE, String.valueOf(--countering));
                        if (countering <= 0)
                        {
                            cancelTime();
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (isStartTimer && countering > 0 && pauseTime > 0)
        {
            pauseTime = (System.currentTimeMillis() - pauseTime) / 1000;
            pauseTime = countering - pauseTime;
            if (pauseTime > 0 && pauseTime < counter)
            {
                countering = (int)pauseTime;
                startTime();
            } else
            {
                cancelTime();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        pauseTime = System.currentTimeMillis();
    }
}
