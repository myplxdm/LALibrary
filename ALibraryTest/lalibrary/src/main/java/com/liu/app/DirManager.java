package com.liu.app;

import android.content.Context;

import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.imagecache.CommonUtil;
import com.liu.lalibrary.utils.imagecache.FileHelper;

import java.io.File;

/**
 * Created by liu on 2017/8/30.
 */

public class DirManager
{
    public static final int DIR_CACHE = 0;
    public static final int DIR_WORK = 1;
    private String[] dirTypes = new String[]{"cache","work"};
    private String[] dirNames = new String[]{"",""};
    private String appPath;

    private static class SingletonHolder
    {
        private static final DirManager INSTANCE = new DirManager();
    }

    public static final DirManager inst()
    {
        return SingletonHolder.INSTANCE;
    }

    private DirManager() {}

    public void init(Context context)
    {
        appPath = CommonUtil.getRootFilePath() + AppUtils.getAppName(context);
        FileHelper.createDirectory(appPath);
        int i = 0;
        for (String path : dirTypes)
        {
            dirNames[i] = appPath + File.separator + path;
            FileHelper.createDirectory(dirNames[i++]);
        }
    }

    public String getDirByType(int type)
    {
        if (type < dirNames.length)
        {
            return dirNames[type];
        }
        return null;
    }

    public String getDirByType(int type, String fileName)
    {
        if (type < dirNames.length)
        {
            return dirNames[type] + File.separator + fileName;
        }
        return null;
    }
}
