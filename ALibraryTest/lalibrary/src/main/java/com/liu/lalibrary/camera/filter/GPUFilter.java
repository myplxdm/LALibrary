//package com.liu.lalibrary.camera.filter;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//
//import java.util.ArrayList;
//
//import jp.co.cyberagent.android.gpuimage.GPUImage;
//import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
//import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
//import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
//import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
//import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
//import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
//
///**
// * Created by liu on 2016/12/29.
// */
//
//public class GPUFilter implements IFilter
//{
//    private GPUImage                    gpuImage;
//    private GPUImageContrastFilter      contrastFilter;//对比度
//    private float                       contrastValue;
//    private float                       contrastOrgValue;
//
//    private GPUImageSharpenFilter       sharpenFilter;//锐化
//    private float                       sharpenValue;
//    private float                       sharpenOrgValue;
//
//    private GPUImageExposureFilter      exposureFilter;//曝光度
//    private float                       exposureValue;
//    private float                       exposureOrgValue;
//
//    private GPUImageSaturationFilter    saturationFilter;//饱和度
//    private float                       saturationValue;
//    private float                       saturationOrgValue;
//
//    private GPUImageFilterGroup         filterGroup;
//
//    //
//
//    public GPUFilter(Context context)
//    {
//        gpuImage = new GPUImage(context);
//        //
//        contrastFilter = new GPUImageContrastFilter(1.0f);
//        contrastValue = 1.0f;
//        contrastOrgValue = contrastValue;
//
//        sharpenFilter = new GPUImageSharpenFilter();
//        sharpenValue = 0f;
//        sharpenOrgValue = sharpenValue;
//
//        exposureFilter = new GPUImageExposureFilter(0f);
//        exposureValue = 0f;
//        exposureOrgValue = exposureValue;
//
//        saturationFilter = new GPUImageSaturationFilter();
//        saturationValue = 1.0f;
//        saturationOrgValue = saturationValue;
//
//        ArrayList<GPUImageFilter> list = new ArrayList<GPUImageFilter>(4);
//        list.add(contrastFilter);
//        list.add(sharpenFilter);
//        list.add(exposureFilter);
//        list.add(saturationFilter);
//        filterGroup = new GPUImageFilterGroup(list);
//        gpuImage.setFilter(filterGroup);
//    }
//
//    public void resetValue()
//    {
//        exposureFilter.setExposure(exposureOrgValue);
//        exposureValue = exposureOrgValue;
//
//        contrastFilter.setContrast(contrastOrgValue);
//        contrastValue = contrastOrgValue;
//
//        sharpenFilter.setSharpness(sharpenOrgValue);
//        sharpenValue = sharpenOrgValue;
//
//        saturationFilter.setSaturation(saturationOrgValue);
//        saturationValue = saturationOrgValue;
//    }
//
//    public void saveValue()
//    {
//        exposureOrgValue = exposureValue;
//        contrastOrgValue = contrastValue;
//        sharpenOrgValue = sharpenValue;
//        saturationOrgValue = saturationValue;
//    }
//
//    @Override
//    public void setExposure(float exposure)
//    {
//        exposureValue = exposure;
//        exposureFilter.setExposure(exposure);
//    }
//
//    @Override
//    public float getExposure()
//    {
//        return exposureValue;
//    }
//
//    @Override
//    public void setContrast(float contrast)
//    {
//        contrastValue = contrast;
//        contrastFilter.setContrast(contrast);
//    }
//
//    @Override
//    public float getContrast()
//    {
//        return contrastValue;
//    }
//
//    @Override
//    public void setSharpen(float sharpen)
//    {
//        sharpenValue = sharpen;
//        sharpenFilter.setSharpness(sharpen);
//    }
//
//    @Override
//    public float getSharpen()
//    {
//        return sharpenValue;
//    }
//
//    @Override
//    public void setSaturation(float saturation)
//    {
//        saturationValue = saturation;
//        saturationFilter.setSaturation(saturation);
//    }
//
//    @Override
//    public float getSaturation()
//    {
//        return saturationValue;
//    }
//
//    @Override
//    public void setImageBitmap(Bitmap bmp)
//    {
//        gpuImage.setImage(bmp);
//    }
//
//    @Override
//    public Bitmap getImageBitmap()
//    {
//        return gpuImage.getBitmapWithFilterApplied();
//    }
//}
