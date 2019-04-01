package com.liu.alibrarytest.keeplive.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.liu.alibrarytest.JMsgRecver;
import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.AppUtils;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by liu on 2018/10/23.
 */

public class GoKLService extends Service
{
    public static final int NID = 2301;
    private JMsgRecver recver;
    @Override
    public void onCreate()
    {
        super.onCreate();
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle("ka");
            startForeground(NID,builder.build());
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);
        } else
        {
            startForeground(NID, new Notification());
        }
        JMessageClient.init(this);
        initJMessage();
    }

    private void initJMessage()
    {
        if (recver == null)recver = new JMsgRecver(this);
        JMessageClient.login("18106073669", "073669", new BasicCallback()
        {
            @Override
            public void gotResult(int responseCode, String responseMessage)
            {
                if (responseCode == 0)
                {
                    JMessageClient.registerEventReceiver(recver);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        assic(true);
        initJMessage();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NID);
        }
        recver.close();
        // 重启自己
        startService(new Intent(getApplicationContext(), GoKLService.class));
        assic(true);
    }

    private void assic(boolean isCheck)
    {
        if (!isCheck || (isCheck && !AppUtils.isServiceExist(this, ProtService.class)))
        {
            startService(new Intent(this, ProtService.class));
        }
        bindService(new Intent(this, ProtService.class), commService, Context.BIND_IMPORTANT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new ProcessServiceAidl.Stub(){};
    }

    private ServiceConnection commService = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if (LibContants.DEBUG)
            {
                LogUtils.LOGD(GoKLService.class, "GoKLService bind ProtService");
            }
            Toast.makeText(GoKLService.this, "go-prot 建立连接", Toast.LENGTH_LONG).show();
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
