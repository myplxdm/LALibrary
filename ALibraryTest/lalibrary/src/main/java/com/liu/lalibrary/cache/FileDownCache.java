package com.liu.lalibrary.cache;

import com.liu.app.wx.WXSDK;

import java.util.HashMap;

/**
 * Created by liu on 2017/10/20.
 */

public class FileDownCache
{
    private static class SingletonHolder
    {
        private static final FileDownCache INSTANCE = new FileDownCache();
    }

    public static final FileDownCache inst()
    {
        return FileDownCache.SingletonHolder.INSTANCE;
    }

    //public void setDirPath()
}
