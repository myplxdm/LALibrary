package com.liu.lalibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by liu on 2017/8/27.
 */

public class NetConnManager extends BroadcastReceiver
{
    public interface INetworkLisener
    {
        public void onNetworkChange(boolean isConn);
    }

    private ArrayList<WeakReference<INetworkLisener>> wrListeners;
    private boolean isConnect = false;
    private boolean isReg = false;
    private boolean isInit = false;
    private ConnectivityManager connManager;
    private ExecutorService executorService;

    private static class SingletonHolder
    {
        private static final NetConnManager INSTANCE = new NetConnManager();
    }

    private NetConnManager()
    {
        wrListeners = new ArrayList<>();
    }

    private void checkConn()
    {
        NetworkInfo info = connManager.getActiveNetworkInfo();
        isConnect = null != info && info.isConnected();
    }

    public static final NetConnManager inst()
    {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void init(Context c)
    {
        if (isInit)return;
        connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkConn();
        isInit = true;
    }

    public synchronized void reg(Context c)
    {
        init(c);
        if (!isReg)
        {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            c.registerReceiver(this, filter);
        }
    }

    public synchronized void unReg(Context c)
    {
        if (isReg)
        {
            try
            {
                c.unregisterReceiver(this);
            } catch (Exception e)
            {
            }
            connManager = null;
            isReg = false;
        }
    }

    public boolean isConnect()
    {
        return isConnect;
    }

    public synchronized void addListener(INetworkLisener listener)
    {
        wrListeners.add(new WeakReference<INetworkLisener>(listener));
    }

    public synchronized void removeListener(INetworkLisener listener)
    {
        INetworkLisener l;
        for (int i = wrListeners.size() - 1;i > -1;i--)
        {
            l = wrListeners.get(i).get();
            if (l == null || (l != null && l == listener))
            {
                wrListeners.remove(i);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        checkConn();
        INetworkLisener l;
        for (int i = wrListeners.size() - 1;i > -1;i--)
        {
            l = wrListeners.get(i).get();
            if (l != null)
            {
                l.onNetworkChange(isConnect);
            }else
            {
                wrListeners.remove(i);
            }
        }
    }
}
