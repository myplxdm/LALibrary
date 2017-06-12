package com.liu.lalibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liu on 16/11/3.
 */

public class Utils
{
    public static String toHexString(byte[] keyData)
    {
        if (keyData == null)
        {
            return null;
        }
        int expectedStringLen = keyData.length * 2;
        StringBuilder sb = new StringBuilder(expectedStringLen);
        for (int i = 0; i < keyData.length; i++)
        {
            String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
            if (hexStr.length() == 1)
            {
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }
        return sb.toString();
    }

    public static String md5(String s)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(String s, int count)
    {
        for (int i = 0; i < count; i++)
        {
            s = md5(s);
        }
        return s;
    }

    public static String getVersion(Context c)
    {
        try
        {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static void systemCall(Activity act, String phone)
    {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + phone));
        act.startActivity(i);
    }

    public static void deleteFile(File file, boolean onlyFile)
    {
        if (file.exists())
        {
            if (file.isFile())
            {
                file.delete();
            } else if (!onlyFile && file.isDirectory())
            {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    deleteFile(files[i], onlyFile);
                }
            }
        } else
        {
            Log.e("smarthome", "delete file no exists " + file.getAbsolutePath());
        }
    }

    public static void clearWebViewCache(Context c)
    {
        try
        {
            c.deleteDatabase("webview.db");
            c.deleteDatabase("webviewCache.db");
            File appCacheDir = new File(c.getFilesDir().getAbsolutePath() + "/webcache");
            Log.d("smarthome", "appCacheDir path=" + appCacheDir.getAbsolutePath());

            File webviewCacheDir = new File(c.getCacheDir().getAbsolutePath() + "/webviewCache");
            Log.d("smarthome", "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

            if (webviewCacheDir.exists())
            {
                deleteFile(webviewCacheDir, false);
            }
            if (appCacheDir.exists())
            {
                deleteFile(appCacheDir, false);
            }
            CookieSyncManager.createInstance(c);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        } catch (Exception e)
        {
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp)
    {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists())
        {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try
        {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                                file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:///" + appDir.getAbsolutePath())));
    }

    public static String replacePhone(String phone)
    {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static int bitAdd(int org, int value)
    {
        return org | value;
    }

    public static int bitReduce(int org, int value)
    {
        return org & (~value);
    }

    public static String nonNull(String value)
    {
        return value == null ? "" : value;
    }

    public static String getExtName(String fn)
    {
        if (!TextUtils.isEmpty(fn))
        {
            int i = fn.lastIndexOf(".");
            return fn.substring(i + 1);
        }
        return "";
    }

    public static String getPath(String fn)
    {
        if (!TextUtils.isEmpty(fn))
        {
            int i = fn.lastIndexOf("/");
            return fn.substring(0, i);
        }
        return "";
    }
}
