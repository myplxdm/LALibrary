package com.liu.lalibrary.loader;

import android.content.Context;

/**
 * Created by liu on 2017/9/7.
 */

public interface ILoader
{
    public void loaderInit(Context context);
    public void loaderUnInit(Context context);
    public void onStart();
    public void onRestart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
}
