package com.liu.lalibrary.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.net.URL;
import java.net.URLConnection;

/**
 * 跟网络相关的工具类
 * 
 * @author zhy
 * 
 */
public class NetUtils
{
	private NetUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context)
	{

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity)
		{

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected())
			{
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}

	public static String getLocalIP(Context context)
	{
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (m.isWifiEnabled())
		{
			return addressToIP(m.getConnectionInfo().getIpAddress());
		}
		return "";
	}

	public static String addressToIP(int addr)
	{
		return (addr & 0xFF ) + "." +
				((addr >> 8 ) & 0xFF) + "." +
				((addr >> 16 ) & 0xFF) + "." +
				(addr >> 24 & 0xFF);
	}

	/**
	 * 判断是否是wifi连接
	 */
	public static boolean isWifi(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null)
			return false;
		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity)
	{
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	public static boolean ping()
	{
		try
		{
			URLConnection urlConnection = new URL("http://www.baidu.com").openConnection();
			urlConnection.setConnectTimeout(1500);
			urlConnection.connect();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
