package com.liu.app.web;

import android.view.View;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.titleview.ITitleView;

/**
 * Created by liu on 2017/12/1.
 */

public interface IWebShell
{
    public AbsActivity getActvity();
    public View getView(int resId);
    public void execJScript(String js);
    public ITitleView getTitleView();
}
