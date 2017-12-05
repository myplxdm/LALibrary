package com.liu.app.web;

import android.content.Intent;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.ui.webview.RLWebViewWrapper;

/**
 * Created by liu on 2017/12/1.
 */

public class WebShellActivity extends AbsActivity
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
}
