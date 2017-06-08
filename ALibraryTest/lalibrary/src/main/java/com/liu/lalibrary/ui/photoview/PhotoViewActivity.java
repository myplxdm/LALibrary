package com.liu.lalibrary.ui.photoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.androidpagecontrol.PageControl;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/4/18.
 */

public class PhotoViewActivity extends AbsActivity implements OnPhotoTapListener
{
    private static final String PARAM_URLS = "urls";
    //下面二是组合，一个保存多个图片文件名，用,分割，一个保存url头地址 , url+imgs[x]
    private static final String PARAM_IMGS = "imgs";
    private static final String PARAM_HOST = "host";
    private static final String PARAMS_INDEX = "index";//第一个显示的索引

    private PHVViewPager vp;
    private PageControl pageControl;
    private List<View> viewList = new ArrayList<>();

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void onInitView()
    {
        vp = (PHVViewPager) findViewById(R.id.vp);
        pageControl = (PageControl) findViewById(R.id.pageControl);
    }

    @Override
    protected void onInitData()
    {
        String[] imgAry = null;
        String host = null;
        Intent intent = getIntent();
        String urls = intent.getStringExtra(PARAM_URLS);
        if (!TextUtils.isEmpty(urls))
        {
            imgAry = urls.split(",");
        }else
        {
            String imgs = intent.getStringExtra(PARAM_IMGS);
            host = intent.getStringExtra(PARAM_HOST);
            if (!TextUtils.isEmpty(imgs) && !TextUtils.isEmpty(host))
            {
                imgAry = imgs.split(",");
            }else
            {
                LogUtils.LOGE(PhotoViewActivity.class, "no photo data");
                return;
            }
        }

        View v;
        PhotoView pv;
        for (int i = 0;i< imgAry.length;i++)
        {
            v = View.inflate(this, R.layout.vp_photo_item, null);
            pv = (PhotoView) v.findViewById(R.id.pv);
            new PhotoViewAttacher(pv).setOnPhotoTapListener(this);
            Glide.with(this).load(host != null ? host + imgAry[i] : imgAry[i]).into(pv);
            viewList.add(v);
        }
        vp.setAdapter(adapter);
        pageControl.setViewPager(vp);
        pageControl.invalidate();

        int index = intent.getIntExtra(PARAMS_INDEX, 0);
        if (index > 0 && index < imgAry.length) vp.setCurrentItem(index);
    }

    private PagerAdapter adapter = new PagerAdapter()
    {
        @Override
        public int getCount()
        {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };

    @Override
    public void onPhotoTap(ImageView view, float x, float y)
    {
        finish();
    }

    public static void show(Activity activity, String urls, int index)
    {
        Intent i = new Intent(activity, PhotoViewActivity.class);
        i.putExtra(PARAM_URLS, urls);
        i.putExtra(PARAMS_INDEX, index);
        activity.startActivity(i);
    }

    public static void show(Activity activity, String host, String imgs, int index)
    {
        Intent i = new Intent(activity, PhotoViewActivity.class);
        i.putExtra(PARAM_HOST, host);
        i.putExtra(PARAM_IMGS, imgs);
        i.putExtra(PARAMS_INDEX, index);
        activity.startActivity(i);
    }
}
