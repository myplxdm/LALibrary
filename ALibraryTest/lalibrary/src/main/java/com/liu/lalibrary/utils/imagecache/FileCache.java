package com.liu.lalibrary.utils.imagecache;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.File;

public class FileCache
{
	private String	dirString;
	private final int FILE_CACHE_SIZE = 100 * 1024 * 1024; //100M  
	private final LruCache<String, Integer> fileCache = new LruCache<String, Integer>(FILE_CACHE_SIZE)
	{  
        @Override  
        public int sizeOf(String key, Integer value)
        {  
            return value.intValue();  
        }  
        @Override  
        protected void entryRemoved(boolean evicted, String key, Integer oldValue, Integer newValue)
        {  
            File file = new File(key);
            if (file.exists() && file.isFile())
			{
				file.delete();
			}
        }  
    };

	public FileCache(Context context, String cachePath)
	{
		dirString = cachePath;
		boolean ret = FileHelper.createDirectory(dirString);
		Log.e("", "FileHelper.createDirectory:" + dirString + ", ret = " + ret);
	}

	public File getFile(String url)
	{
		String ext;
		File f = null;
		int i = url.lastIndexOf(".");
		if (i > -1 && i < url.length())
		{
			ext = url.substring(i);
			f = new File(dirString + encodeFileName(url) + ext);
		}
		return f;
	}
	
	public void put(String file,int filesize)
	{
		fileCache.put(file, filesize);
	}

	public void clear()
	{
		FileHelper.deleteDirectory(dirString);
	}

	//
	public static String encodeFileName(String url)
	{
		if (url != null)
		{
			return String.valueOf(url.hashCode());
		}
		return "";
	}
}
