package com.liu.alibrarytest.keeplive.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.AppUtils;

/**
 * Created by liu on 2018/10/23.
 */

public class ProtService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new ProcessServiceAidl.Stub(){};
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        assic(true);
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (LibContants.DEBUG)
        {
            LogUtils.LOGD(getClass(), "playService onDestroy");
        }
        startService(new Intent(getApplicationContext(), ProtService.class));
        assic(true);
    }

    private void assic(boolean isCheck)
    {
        if (!isCheck || (isCheck && !AppUtils.isServiceExist(this, GoKLService.class)))
        {
            startService(new Intent(this, GoKLService.class));
        }
        bindService(new Intent(this, GoKLService.class), commService, Context.BIND_IMPORTANT);
    }

    private ServiceConnection commService = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if (LibContants.DEBUG)
            {
                LogUtils.LOGD(GoKLService.class, "ProtService bind GoKLService");
            }
            Toast.makeText(ProtService.this, "prot-go 建立连接", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            if (LibContants.DEBUG)
            {
                LogUtils.LOGD(GoKLService.class, "GoKLService unbind ProtService");
            }
            assic(false);
        }
    };
}
