package com.liu.lalibrary.plugins;

import android.content.Intent;

import com.liu.lalibrary.AbsActivity;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/8/31.
 */

public abstract class PluginBase implements IPlugin
{
    protected WeakReference<AbsActivity> wrActivity;

    public PluginBase(AbsActivity activity)
    {
        if (activity != null)
        {
            wrActivity = new WeakReference<AbsActivity>(activity);
        }
    }

    protected AbsActivity getActivity()
    {
        if (wrActivity != null)
        {
            return wrActivity.get();
        }
        return null;
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public void onRestart()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onStop()
    {

    }

    @Override
    public void onDestroy()
    {
        wrActivity = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }
}
