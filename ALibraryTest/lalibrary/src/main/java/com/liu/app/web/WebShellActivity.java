package com.liu.app.web;

import android.content.Intent;
import android.view.View;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.ui.webview.RLWebViewWrapper;

/**
 * Created by liu on 2017/12/1.
 */

public class WebShellActivity extends AbsActivity implements IWebShell
{
    private LTitleView titleView;
    private RLWebViewWrapper webView;

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_web_shell;
    }

    @Override
    protected void onInitView()
    {

    }

    @Override
    protected void onInitData(Intent data)
    {

    }

    @Override
    public AbsActivity getActvity()
    {
        return null;
    }

    @Override
    public View getView(int resId)
    {
        return null;
    }

    @Override
    public void execJScript(String js)
    {

    }

    @Override
    public ITitleView getTitleView()
    {
        return null;
    }
}
