package com.liu.lalibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionChangeReceiver extends BroadcastReceiver
{
    public interface INetworkLisener
    {
        public void onNetworkChange(boolean isUICall, boolean isConn);
    }

    private static ConnectionChangeReceiver inst;
    private ArrayList<INetworkLisener> list = new ArrayList<INetworkLisener>();
    private boolean isConnect = false;
    private boolean isReg = false;
    private ConnectivityManager connManager;
    private ExecutorService executorService;

    private ConnectionChangeReceiver()
    {
        executorService = Executors.newFixedThreadPool(1);
    }

    public static ConnectionChangeReceiver getInst()
    {
        if (inst == null)
        {
            inst = new ConnectionChangeReceiver();
        }
        return inst;
    }

    private void procConn()
    {
        NetworkInfo info = connManager.getActiveNetworkInfo();

        NetworkInfo mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isConnect = mobNetInfo.isConnected() || wifiNetInfo.isConnected();
    }

    public boolean isConnect()
    {
        return isConnect;
    }

    public synchronized void reg(Context c)
    {
        if (!isReg)
        {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            c.registerReceiver(this, filter);

            connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            procConn();
            isReg = true;
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
        }
    }

    public synchronized void addListener(INetworkLisener listener)
    {
        removeListener(listener);
        list.add(listener);
    }

    public synchronized void removeListener(INetworkLisener lisener)
    {
        list.remove(lisener);
    }

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConn = mobNetInfo.isConnected();
        if (!isConn && wifiNetInfo.isConnected())
        {
            executorService.execute(new Runnable()
            {
                public void run()
                {
                    isConnect = NetUtils.ping();
//                    if (BaseActivity.cur_act != null)
//                    {
//                        BaseActivity.cur_act.runOnUiThread(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
                                  for (INetworkLisener l : list)
                                  {
                                      l.onNetworkChange(false, isConnect);
                                  }
//                            }
//                        });
//                    }
                }
            });
            return;
        } else
        {
            isConnect = isConn;
        }
        synchronized (this)
        {
            for (INetworkLisener l : list)
            {
                l.onNetworkChange(true, isConnect);
            }
        }
    }

}
