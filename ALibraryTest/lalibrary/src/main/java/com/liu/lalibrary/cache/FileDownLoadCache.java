package com.liu.lalibrary.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.utils.Utils;
import com.liu.lalibrary.utils.imagecache.FileHelper;

import java.io.File;

/**
 * Created by liu on 2017/10/20.
 */

public class FileDownLoadCache implements ICache
{
    private static class SingletonHolder
    {
        private static final FileDownLoadCache INSTANCE = new FileDownLoadCache();
    }

    public static final FileDownLoadCache inst()
    {
        return FileDownLoadCache.SingletonHolder.INSTANCE;
    }

    private String savePath;

    public void setCacheDirPath(String path)
    {
        this.savePath = path + File.separator;
    }

    @Override
    public void clearCache()
    {
        FileHelper.deleteDirectory(savePath);
    }

    @Override
    public void load(String url, final OnCacheListener listener)
    {
        String sn = Utils.md5(url);
        File f = new File(savePath + sn);
        if (f.exists())
        {
            listener.onCacheComplete(f);
            return;
        }
        LjhHttpUtils.inst().downFile(url, f.getAbsolutePath(), new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, String result)
            {
                if (state == LjhHttpUtils.HU_STATE_OK)
                {
                    listener.onCacheComplete(new File(result));
                    return;
                }
                listener.onCacheComplete(null);
            }

            @Override
            public void onHttpReqProgress(float progress)
            {
            }
        });
    }
}
