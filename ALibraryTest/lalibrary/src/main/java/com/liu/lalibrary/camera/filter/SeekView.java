package com.liu.lalibrary.camera.filter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.view.BaseView;

/**
 * Created by liu on 16/9/12.
 */
public class SeekView extends BaseView implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
    public interface OnSeekListener
    {
        public void OnSeekOk(float progress, Object param);
        public void OnSeekChangValue(float progress, Object param);
        public void OnSeekCancel();
    }

    //
    private TextView        tv_ok;
    private TextView        tv_title;
    private TextView        tv_cancel;
    //
    private SeekBar         sb_progress;
    private TextView        tv_value;

    private int             min_value;
    private int             offset_value;
    //
    private OnSeekListener  listener;

    public SeekView(AbsActivity activity, ViewGroup vg, int rid)
    {
        super(activity, vg, rid);
        initView(rootViewGroup);
    }

    public void initView(View view)
    {
        tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        //
        sb_progress = (SeekBar) view.findViewById(R.id.sb_progress);
        tv_value = (TextView) view.findViewById(R.id.tv_value);
        sb_progress.setOnSeekBarChangeListener(this);
    }

    public void setOnSeekListener(OnSeekListener listener)
    {
        this.listener = listener;
    }

    public void set(String title, Object param, int min, int max, int cur)
    {
        offset_value = (max - min) / 2;

        tv_title.setText(title);
        tv_title.setTag(param);

        sb_progress.setProgress(offset_value + cur);
        sb_progress.setMax(max - min);
        //
        rootViewGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        if (listener == null) return;
        if (v.getId() == R.id.tv_ok)
        {
            listener.OnSeekOk(sb_progress.getProgress(), tv_title.getTag());
        }else
        {
            listener.OnSeekCancel();
        }
        rootViewGroup.setVisibility(View.GONE);
    }

    /*********************  OnSeekBarChangeListener  *************************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        tv_value.setText(String.format("%.1f",((progress - offset_value) * 0.1f)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        listener.OnSeekChangValue((seekBar.getProgress() - offset_value) * 0.1f, tv_title.getTag());
    }
}
