package com.liu.lalibrary.camera.adjust;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.view.BaseView;

/**
 * Created by liu on 2017/1/11.
 */

public class AdjustOperView extends BaseView implements View.OnClickListener
{
    public interface AdjustOperListener
    {
        public void onOperFill(boolean isFill);
        public void onShowFilter();
        public void onSetBgColor(int color);
        public void onRotate();
    }

    private LinearLayout ll_fill;
    private ImageView iv_fill;
    private TextView tv_fill;

    private LinearLayout ll_filter;

    private LinearLayout ll_bg_color;
    private ImageView iv_bg_color;

    private LinearLayout ll_rot_photo;

    private AdjustOperListener listener;

    public AdjustOperView(AbsActivity activity, ViewGroup vg, int rid)
    {
        super(activity, vg, rid);

        ll_fill = (LinearLayout) rootViewGroup.findViewById(R.id.ll_fill);
        ll_fill.setTag(1);
        ll_fill.setOnClickListener(this);

        iv_fill = (ImageView) rootViewGroup.findViewById(R.id.iv_fill);
        tv_fill = (TextView) rootViewGroup.findViewById(R.id.tv_fill);

        ll_filter = (LinearLayout) rootViewGroup.findViewById(R.id.ll_filter);
        ll_filter.setOnClickListener(this);

        ll_bg_color = (LinearLayout) rootViewGroup.findViewById(R.id.ll_bg_color);
        ll_bg_color.setOnClickListener(this);
        ll_bg_color.setTag(1);

        iv_bg_color = (ImageView) rootViewGroup.findViewById(R.id.iv_bg_color);
        ll_rot_photo = (LinearLayout) rootViewGroup.findViewById(R.id.ll_rot_photo);
        ll_rot_photo.setOnClickListener(this);
    }

    public void setAdjustOperListener(AdjustOperListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        if (listener == null) return;
        int i = v.getId();
        if (i == R.id.ll_fill)
        {
            int tag = (Integer) ll_fill.getTag();
            iv_fill.setBackgroundResource(tag == 1 ? R.mipmap.camera_white_egde : R.mipmap.camera_fill);
            tv_fill.setText(tag == 1 ? "留白" : "填充");
            ll_fill.setTag(tag == 1 ? 0 : 1);
            listener.onOperFill(tag != 1);

        } else if (i == R.id.ll_filter)
        {
            listener.onShowFilter();

        } else if (i == R.id.ll_bg_color)
        {
            int tag;
            tag = (Integer) ll_bg_color.getTag();
            ll_bg_color.setTag(tag == 1 ? 0 : 1);
            iv_bg_color.setBackgroundResource(tag == 1 ? R.mipmap.camera_bg_white : R.mipmap.camera_bg_black);
            listener.onSetBgColor(tag == 1 ? 0xffffffff : 0xff000000);

        } else if (i == R.id.ll_rot_photo)
        {
            listener.onRotate();

        }
    }
}
