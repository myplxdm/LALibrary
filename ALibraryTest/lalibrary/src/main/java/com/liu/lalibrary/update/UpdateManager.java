package com.liu.lalibrary.update;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;

import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.Utils;
import com.liu.lalibrary.utils.imagecache.CommonUtil;
import com.liu.lalibrary.utils.imagecache.FileHelper;

import java.io.File;

public class UpdateManager
{
	//private DownloadBinder	binder;
//	private String			url;
//	private String			path;
//	private String			filename;

	private long			mDownloadID;
	private Context			mContext;
	// private ServiceConnection serviceConnection = new ServiceConnection()
	// {
	// @Override
	// public void onServiceDisconnected(ComponentName name)
	// {
	// binder = null;
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service)
	// {
	// binder = (DownloadBinder)service;
	// binder.start(url, path, filename);
	// }
	// };

	private BroadcastReceiver downReceiver = new BroadcastReceiver() 
	{   
        @Override   
        public void onReceive(Context context, Intent intent) 
        {               
            queryDownloadStatus();   
        }   
    };   
    
    private void queryDownloadStatus() 
    {   
    	DownloadManager manger = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();   
        query.setFilterById(mDownloadID);
        Cursor c = manger.query(query);   
        if(c.moveToFirst()) 
        {   
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));   
            switch(status) 
            {   
//            case DownloadManager.STATUS_PAUSED:   
//                Log.v("down", "STATUS_PAUSED");  
//            case DownloadManager.STATUS_PENDING:   
//                Log.v("down", "STATUS_PENDING");  
//            case DownloadManager.STATUS_RUNNING:   
//                //濮濓絽婀稉瀣祰閿涘奔绗夐崑姘崲娴ｆ洑绨ㄩ幆锟�
//                Log.v("down", "STATUS_RUNNING");  
//                break;   
            case DownloadManager.STATUS_SUCCESSFUL:   
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String name;
				if (AppUtils.getOSVersion() >= 24)
				{
					name = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				}else
				{
					name = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
				}
            	File f = new File(name);
            	if (f.exists())
				{
            		intent.setDataAndType(
    						Uri.fromFile(f),
    						"application/vnd.android.package-archive");
    				mContext.startActivity(intent);
				}				
                break;   
            case DownloadManager.STATUS_FAILED:   
                manger.remove(mDownloadID);   
                break;   
            }   
        }  
    }  
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public UpdateManager(Context c, String url)
	{
		mContext = c;
		String appName = AppUtils.getAppName(c);
		String path = CommonUtil.getRootFilePath() + appName + File.separator;
		String filename = Utils.getFileName(url);
		FileHelper.createDirectory(path);
		//
		DownloadManager manger = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
		Request down = new Request(Uri.parse(url));
		down.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
		if (VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			down.setShowRunningNotification(true);
		} else
		{
			down.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		down.setVisibleInDownloadsUi(true);
		down.setTitle(filename);
		down.setDestinationInExternalPublicDir(path, filename);
		String sPath = path + filename;
		File f = new File(sPath);
		if (f.exists())
		{
			f.delete();
		}
		mDownloadID = manger.enqueue(down);
		mContext.registerReceiver(downReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

}
