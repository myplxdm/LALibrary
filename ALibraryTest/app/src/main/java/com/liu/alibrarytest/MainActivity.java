package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.RLTitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ryt.mtzf.R;

public class MainActivity extends AbsActivity
{
    @BindView(R.id.btnOpen)
    Button btnOpen;
    @BindView(R.id.btnStop)
    Button btnStop;

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onInitView()
    {
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginContacts(this));
    }

    @OnClick({R.id.btnOpen, R.id.btnStop})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnOpen:
                WebShellImpl.openWindow(this, WebShellImpl.class,
                        R.mipmap.i_exit, "http://192.168.3.29:1024/test",
                        "", RLTitleView.TITLE_ALIG_MIDDLE);
                break;
            case R.id.btnStop:
                break;
        }
    }

}
