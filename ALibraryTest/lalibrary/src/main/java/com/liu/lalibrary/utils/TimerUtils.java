package com.liu.lalibrary.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liu on 2017/3/8.
 */

public class TimerUtils
{
    private static TimerUtils inst = new TimerUtils();
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private TimerUtils(){}

    public static TimerUtils getInst()
    {
        return inst;
    }

    public void exec(Runnable runnable, int millsec)
    {
        service.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }
}
