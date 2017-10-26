package com.liu.lalibrary.cache;

import java.io.File;

/**
 * Created by liu on 2017/10/23.
 */

public interface ICache
{
    public interface OnCacheListener
    {
        public void onCacheComplete(Object obj);
    }

    public void load(String url, OnCacheListener listener);
    public void setCacheDirPath(String path);
    public void clearCache();
}
