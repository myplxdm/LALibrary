package com.liu.lalibrary.ui.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by liu on 2017/3/18.
 */

public interface IView
{
    public static final int VIEW_EVENT_INIT_VIEW = 1;
    public static final int VIEW_EVENT_INIT_DATA = 2;
    public static final int VIEW_EVENT_START = 3;
    public static final int VIEW_EVENT_RESUME = 4;
    public static final int VIEW_EVENT_RESTART = 5;
    public static final int VIEW_EVENT_PAUSE = 6;
    public static final int VIEW_EVENT_STOP = 7;
    public static final int VIEW_EVENT_DESTRORY = 8;

    public void onInitView();
    public void onInitData();
    public void onStart();
    public void onRestart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
    public void setShow(boolean isShow);
    public ViewGroup getView();
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
