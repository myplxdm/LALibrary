package com.liu.app.wx;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.liu.lalibrary.log.LogUtils;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImageURLUtil
{
    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle)
        {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try
        {
            output.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] getHtmlByteArray(final String url)
    {
        URL htmlUrl = null;
        InputStream inStream = null;
        try
        {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inStream = httpConnection.getInputStream();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        byte[] data = inputStreamToByte(inStream);

        return data;
    }

    public static byte[] inputStreamToByte(InputStream is)
    {
        try
        {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            byte[] buffer = new byte[10240];
            while ((ch = is.read(buffer)) != -1)
            {
                bytestream.write(buffer, 0, ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] readFromFile(String fileName, int offset, int len)
    {
        if (fileName == null)
        {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists())
        {
            LogUtils.LOGI(ImageURLUtil.class, "readFromFile: file not found");
            return null;
        }

        if (len == -1)
        {
            len = (int) file.length();
        }
        LogUtils.LOGI(ImageURLUtil.class, "readFromFile : offset = " + offset + " len = " + len
                + " offset + len = " + (offset + len));

        if (offset < 0)
        {
            LogUtils.LOGI(ImageURLUtil.class, "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0)
        {
            LogUtils.LOGI(ImageURLUtil.class, "readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length())
        {
            LogUtils.LOGI(ImageURLUtil.class, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try
        {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len];
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e)
        {
            LogUtils.LOGI(ImageURLUtil.class, "readFromFile : errMsg = " + e.getMessage());
        }
        return b;
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height,
                                          final int width, final boolean crop)
    {
        Assert.assertTrue(path != null && !path.equals("") && height > 0
                                  && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try
        {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null)
            {
                tmp.recycle();
                tmp = null;
            }
            LogUtils.LOGD(ImageURLUtil.class, "extractThumbNail: round=" + width + "x" + height
                    + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            LogUtils.LOGD(ImageURLUtil.class, "extractThumbNail: extract beX = " + beX + ", beY = "
                    + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
                    : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1)
            {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE)
            {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop)
            {
                if (beY > beX)
                {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else
                {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else
            {
                if (beY < beX)
                {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else
                {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;
            LogUtils.LOGI(ImageURLUtil.class, "bitmap required size=" + newWidth + "x" + newHeight
                    + ", orig=" + options.outWidth + "x" + options.outHeight
                    + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null)
            {
                LogUtils.LOGE(ImageURLUtil.class, "bitmap decode failed");
                return null;
            }

            LogUtils.LOGI(ImageURLUtil.class, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null)
            {
                bm.recycle();
                bm = scale;
            }

            if (crop)
            {
                final Bitmap cropped = Bitmap.createBitmap(bm,
                                                           (bm.getWidth() - width) >> 1,
                                                           (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null)
                {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                LogUtils.LOGI(ImageURLUtil.class, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e)
        {
            LogUtils.LOGE(ImageURLUtil.class, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }
}
