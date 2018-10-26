package com.liu.lalibrary.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.liu.lalibrary.utils.imagecache.CommonUtil;

import java.util.List;

/**
 * 跟App相关的辅助类
 * 
 * @author zhy
 * 
 */
public class AppUtils
{
	private AppUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static int getOSVersion()
	{
		return Build.VERSION.SDK_INT;
	}

	public static boolean isServiceExist(Context context, Class cls)
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> ri = am.getRunningServices(Integer.MAX_VALUE);
		for (int i = ri.size() - 1;i > -1;i--)
		{
			if (ri.get(i).service.getClassName().equals(cls.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isIgnoringBatteryOpt(Activity context, boolean isOpenOpt)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			boolean isIbo = pm.isIgnoringBatteryOptimizations(context.getPackageName());
			if (!isIbo && isOpenOpt)
			{
				Intent i = new Intent();
				i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				i.setData(Uri.parse("package:" + context.getPackageName()));
				context.startActivityForResult(i, 0x200);
			}
			return isIbo;
		}
		return false;
	}

}
