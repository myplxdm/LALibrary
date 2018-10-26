package com.liu.alibrarytest.keeplive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 静态监听锁屏、解锁、开屏广播
 * a) 当用户锁屏时，将Activity置于前台，同时开启1像素悬浮窗；
 * b) 当用户解锁时，关闭1像素悬浮窗；
 */

public class ScreenReceiver
{
    private Context mContext;
    private Class mCls;
    private WeakReference<Activity> actRef;
    // 锁屏广播接收器
    private SreenBroadcastReceiver mScreenReceiver;
    // 屏幕状态改变回调接口
    private SreenStateListener mStateReceiverListener;

    public ScreenReceiver(Context mContext)
    {
        this.mContext = mContext;
    }

    public void startReceiver(Class cls, SreenStateListener listener)
    {
        mCls = cls;
        mStateReceiverListener = listener;
        mScreenReceiver = new SreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public void setActivity(Activity activity)
    {
        actRef = new WeakReference<Activity>(activity);
    }

    public void openSingle()
    {
        Intent i = new Intent(mContext,mCls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public void stopScreenReceiverListener()
    {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    public class SreenBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (LibContants.DEBUG)
            {
                LogUtils.LOGD(getClass(), "listner system broadcast");
            }

            if (Intent.ACTION_SCREEN_ON.equals(action) && actRef != null)
            {
                Activity activity = actRef.get();
                if (activity != null)
                {
                    activity.finish();
                }
                if (mStateReceiverListener != null) mStateReceiverListener.onSreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action))
            {   // 锁屏
                if (mCls != null)
                {
                    if (LibContants.DEBUG)
                    {
                        LogUtils.LOGD(getClass(), "open 1px activity");
                    }
                    openSingle();
                }
                if (mStateReceiverListener != null) mStateReceiverListener.onSreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action))
            {
                if (mStateReceiverListener != null) mStateReceiverListener.onUserPresent();
            }
        }
    }

    // 监听sreen状态对外回调接口
    public interface SreenStateListener
    {
        void onSreenOn();
        void onSreenOff();
        void onUserPresent();
    }
}
