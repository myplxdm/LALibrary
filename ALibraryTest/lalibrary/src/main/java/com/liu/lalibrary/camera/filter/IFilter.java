package com.liu.lalibrary.camera.filter;

import android.graphics.Bitmap;

/**
 * Created by liu on 2016/12/29.
 */

public interface IFilter
{
    //曝光度
    public void setExposure(float exposure);
    public float getExposure();
    //对比度
    public void setContrast(float contrast);
    public float getContrast();
    //锐化
    public void setSharpen(float sharpen);
    public float getSharpen();
    //饱和度
    public void setSaturation(float saturation);
    public float getSaturation();
    //
    public void setImageBitmap(Bitmap bmp);
    public Bitmap getImageBitmap();
}
