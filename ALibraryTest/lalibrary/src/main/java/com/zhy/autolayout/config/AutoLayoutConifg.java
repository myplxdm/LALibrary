package com.zhy.autolayout.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import com.liu.lalibrary.AbsActivity;
import com.zhy.autolayout.utils.L;
import com.zhy.autolayout.utils.ScreenUtils;

/**
 * Created by zhy on 15/11/18.
 */
public class AutoLayoutConifg
{

    private static AutoLayoutConifg sIntance = new AutoLayoutConifg();

    private static final String KEY_DESIGN_WIDTH = "design_width";
    private static final String KEY_DESIGN_HEIGHT = "design_height";

    private int mScreenWidth;
    private int mScreenHeight;

    private int mDesignWidth;
    private int mDesignHeight;

    private boolean useDeviceSize;

    private Context context;

    private AutoLayoutConifg()
    {
    }

    public void checkParams()
    {
        if (mDesignHeight <= 0 || mDesignWidth <= 0)
        {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.");
        }
    }

    public AutoLayoutConifg useDeviceSize()
    {
        useDeviceSize = true;
        return this;
    }


    public static AutoLayoutConifg getInstance()
    {
        return sIntance;
    }

    public int getScreenDir()
    {
        Configuration mConfiguration = context.getResources().getConfiguration();
        return mConfiguration.orientation;
    }

    public int getScreenWidth()
    {
        if (getScreenDir() == Configuration.ORIENTATION_LANDSCAPE)
            return mScreenHeight;
        return mScreenWidth;
    }

    public int getScreenHeight()
    {
        if (getScreenDir() == Configuration.ORIENTATION_LANDSCAPE)
            return mScreenWidth;
        return mScreenHeight;
    }

    public int getDesignWidth()
    {
        if (getScreenDir() == Configuration.ORIENTATION_LANDSCAPE)
            return mDesignHeight;
        return mDesignWidth;
    }

    public int getDesignHeight()
    {
        if (getScreenDir() == Configuration.ORIENTATION_LANDSCAPE)
            return mDesignWidth;
        return mDesignHeight;
    }

    public void init(Context context)
    {
        this.context = context;
        getMetaData(context);
        int[] screenSize = ScreenUtils.getScreenSize(context, useDeviceSize);
        mScreenWidth = screenSize[0];
        mScreenHeight = screenSize[1];
        L.e(" screenWidth =" + mScreenWidth + " ,screenHeight = " + mScreenHeight);
    }

    private void getMetaData(Context context)
    {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try
        {
            applicationInfo = packageManager.getApplicationInfo(context
                    .getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null)
            {
                mDesignWidth = (int) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
                mDesignHeight = (int) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);
            }
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.", e);
        }

        L.e(" designWidth =" + mDesignWidth + " , designHeight = " + mDesignHeight);
    }


}
