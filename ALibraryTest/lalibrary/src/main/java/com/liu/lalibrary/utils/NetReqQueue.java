package com.liu.lalibrary.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liu on 2017/3/8.
 */

public class NetReqQueue
{
//    private class NetReqObject
//    {
//        public Object param;
//        public WeakReference<Runnable> runable;
//    }

//    private static class NetReqSingleton
//    {
//        public static final NetReqQueue inst = new NetReqQueue();
//    }

    private ArrayList<Runnable> listNetReqRunable;
    private ExecutorService executorService;

    public NetReqQueue(int threadCount)
    {
        listNetReqRunable = new ArrayList<>();
        if (threadCount <= 0)
            executorService = Executors.newCachedThreadPool();
        else
            executorService = Executors.newFixedThreadPool(threadCount);
        //ConnectionChangeReceiver.getInst().addListener(this);
    }

    public boolean exec(Runnable runnable)
    {
        if (ConnectionChangeReceiver.getInst().isConnect())
        {
            executorService.submit(runnable);
            return true;
        }

        return false;
    }

}
