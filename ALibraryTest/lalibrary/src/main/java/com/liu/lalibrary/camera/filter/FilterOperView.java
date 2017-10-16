package com.liu.lalibrary.camera.filter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.view.BaseView;


/**
 * Created by liu on 2016/12/28.
 */

public class FilterOperView extends BaseView implements View.OnClickListener, SeekView.OnSeekListener
{
    public interface FilterOperViewListener
    {
        public void onFilterUpdate(Bitmap bmp);
        public void onFilterViewFinish(boolean bCancle, Bitmap bmp);
        public void onFilterClose();
    }

    //seek
    private SeekView                seek_view;
    //filter
    private Button                  btn_ok;
    private LinearLayout            ll_exposure;
    private LinearLayout            ll_contrast;
    private LinearLayout            ll_sharpen;
    private LinearLayout            ll_saturation;
    //
    private FilterOperViewListener  listener;
    private Bitmap                  filter_bitmap;
    //
    private GPUFilter               gpuFilter;

    public FilterOperView(AbsActivity activity, ViewGroup vg, int rid)
    {
        super(activity, vg, rid);
        initFilterBtn(rootViewGroup);
        initFilterProgress(activity, rootViewGroup, 0);
        gpuFilter = new GPUFilter(rootViewGroup.getContext());
    }

    private void initFilterBtn(View view)
    {
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        //曝光度
        ll_exposure = (LinearLayout) view.findViewById(R.id.ll_exposure);
        ll_exposure.setOnClickListener(this);
        //对比度
        ll_contrast = (LinearLayout) view.findViewById(R.id.ll_contrast);
        ll_contrast.setOnClickListener(this);
        //锐化
        ll_sharpen = (LinearLayout) view.findViewById(R.id.ll_sharpen);
        ll_sharpen.setOnClickListener(this);
        //饱和度
        ll_saturation = (LinearLayout) view.findViewById(R.id.ll_saturation);
        ll_saturation.setOnClickListener(this);
    }

    private void initFilterProgress(AbsActivity activity, ViewGroup vg, int rid)
    {
        seek_view = new SeekView(activity, vg, R.id.camera_seek);
        seek_view.setOnSeekListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.ll_exposure)
        {
            seek_view.setShow(true);
            seek_view.set("曝光度", R.id.ll_exposure, -100, 100, (int) (gpuFilter.getExposure() * 10));

        } else if (i == R.id.ll_contrast)
        {
            seek_view.setShow(true);
            seek_view.set("对比度", R.id.ll_contrast, 0, 40, (int) (gpuFilter.getContrast() * 10));

        } else if (i == R.id.ll_sharpen)
        {
            seek_view.setShow(true);
            seek_view.set("锐化", R.id.ll_sharpen, -40, 40, (int) (gpuFilter.getSharpen() * 10));

        } else if (i == R.id.ll_saturation)
        {
            seek_view.setShow(true);
            seek_view.set("饱和度", R.id.ll_saturation, 0, 20, (int) (gpuFilter.getSaturation() * 10));

        } else if (i == R.id.btn_ok)
        {
            listener.onFilterClose();
            rootViewGroup.setVisibility(View.GONE);

        }
    }

    //
    public void setFilterViewListener(FilterOperViewListener listener)
    {
        this.listener = listener;
    }

    public void setImage(Bitmap bmp)
    {
        gpuFilter.setImageBitmap(bmp);
    }

    private Bitmap createFilterImage()
    {
        if (filter_bitmap != null && !filter_bitmap.isRecycled()) filter_bitmap.recycle();
        filter_bitmap = gpuFilter.getImageBitmap();
        return filter_bitmap;
    }

    /*******************   OnSeekListener   *******************/
    @Override
    public void OnSeekOk(float progress, Object param)
    {
        gpuFilter.saveValue();

        listener.onFilterViewFinish(false, filter_bitmap);
    }

    @Override
    public void OnSeekChangValue(float progress, Object param)
    {
        if (listener != null)
        {
            int id = (Integer)param;
            if (id == R.id.ll_exposure)
            {
                gpuFilter.setExposure(progress);

            } else if (id == R.id.ll_contrast)
            {
                gpuFilter.setContrast(progress);

            } else if (id == R.id.ll_sharpen)
            {
                gpuFilter.setSharpen(progress);

            } else if (id == R.id.ll_saturation)
            {
                gpuFilter.setSaturation(progress);

            }

            createFilterImage();

            listener.onFilterUpdate(filter_bitmap);
        }
    }

    @Override
    public void OnSeekCancel()
    {
        gpuFilter.resetValue();
        listener.onFilterViewFinish(true, createFilterImage());
    }

    @Override
    public void onDestroy()
    {
        if (filter_bitmap != null && !filter_bitmap.isRecycled()) filter_bitmap.recycle();
    }
}
