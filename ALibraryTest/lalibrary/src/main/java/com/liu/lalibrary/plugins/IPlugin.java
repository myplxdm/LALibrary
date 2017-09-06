package com.liu.lalibrary.plugins;

import android.content.Intent;

import com.liu.lalibrary.AbsActivity;

/**
 * Created by liu on 2017/8/31.
 */

public interface IPlugin
{
    public String getName();
    public String getDescribe();
    public boolean exec(String cmd, IPluginEvent event);
    public void onStart();
    public void onRestart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}