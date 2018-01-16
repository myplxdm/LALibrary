package com.liu.app.alipay;

import android.content.Context;

import com.alipay.sdk.app.PayTask;
import com.liu.app.wx.WXSDK;
import com.liu.lalibrary.AbsActivity;

/**
 * Created by liu on 2018/1/16.
 */

public class AliPaySDK
{
    private static class SingletonHolder
    {
        private static final AliPaySDK INSTANCE = new AliPaySDK();
    }

    public static final AliPaySDK inst()
    {
        return AliPaySDK.SingletonHolder.INSTANCE;
    }

    public void pay(final AbsActivity activity, final String orderStr)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                PayTask alipay = new PayTask(activity);
                alipay.payV2(orderStr,true);
            }
        }).start();
    }
}
