package com.liu.app;

import android.Manifest;
import android.content.Context;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.PermissionsUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by liu on 2017/9/14.
 */

public class UMengHelper
{
    public static void init(final AbsActivity absActivity, final String appkey, final String channelId)
    {
        if (AppUtils.getOSVersion() >= 23 && absActivity != null)
        {
            absActivity.checkPermissions(new PermissionsUtil.PermissionCallback()
            {
                @Override
                public void onPermission(boolean isOK)
                {
                    if (isOK)
                    {
                        MobclickAgent.UMAnalyticsConfig cfg = new MobclickAgent.UMAnalyticsConfig(absActivity, appkey, channelId);
                        MobclickAgent.startWithConfigure(cfg);
                    }
                }
            }, Manifest.permission.READ_PHONE_STATE);
        }else
        {
            MobclickAgent.UMAnalyticsConfig cfg = new MobclickAgent.UMAnalyticsConfig(absActivity, appkey, channelId);
            MobclickAgent.startWithConfigure(cfg);
        }
    }

    public static void onResume(Context context)
    {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context)
    {
        MobclickAgent.onPause(context);
    }
}
