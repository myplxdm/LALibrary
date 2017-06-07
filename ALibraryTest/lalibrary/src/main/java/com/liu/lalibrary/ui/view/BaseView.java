package com.liu.lalibrary.ui.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.log.LogUtils;
import com.zhy.autolayout.utils.AutoUtils;
import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/4/10.
 */

public abstract class BaseView implements IView
{
    protected ViewGroup rootViewGroup;
    protected WeakReference<AbsActivity> refActivity;

    public BaseView(AbsActivity activity, ViewGroup vg, int rid)
    {
        refActivity = new WeakReference<>(activity);
        if (rid != 0)
        {
            rootViewGroup = (ViewGroup) vg.findViewById(rid);
        }else
        {
            rootViewGroup = vg;
        }
        AutoUtils.auto(vg);
    }

    public void onInitView()
    {
    }

    public void onInitData()
    {
    }

    public void onStart()
    {
    }

    public void onEnter()
    {
    }

    public void onPause()
    {
    }

    public void onStop()
    {
    }

    public void onDestroy()
    {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, Boolean[] grantResults)
    {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }

    public void setShow(boolean isShow)
    {
        if (rootViewGroup != null)
        {
            rootViewGroup.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }else
        {
            LogUtils.LOGE(this.getClass(), "rootViewGroup is null");
        }
    }

    public ViewGroup getView()
    {
        return rootViewGroup;
    }

    public AbsActivity getActivity()
    {
        if (refActivity != null)
        {
            return refActivity.get();
        }
        return null;
    }

    public ViewGroup loadLayout(int rid, ViewGroup parent, boolean bAttRoot)
    {
        AbsActivity activity = getActivity();
        if (activity != null)
        {
            ViewGroup vg = (ViewGroup) LayoutInflater.from(activity).inflate(rid, parent, bAttRoot);
            AutoUtils.auto(vg);
            return vg;
        }
        return null;
    }
}
