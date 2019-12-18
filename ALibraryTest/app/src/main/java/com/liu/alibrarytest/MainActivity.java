package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ryt.mtzf.R;

public class MainActivity extends AbsActivity
{

    @BindView(R.id.add)
    Button add;
    @BindView(R.id.fl)
    FlowLayout fl;

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
        LayoutInflater li = LayoutInflater.from(this);
        RadioButton rb;
        for (int i = 0; i < 10; i++)
        {

        }
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

    @OnClick(R.id.add)
    public void onViewClicked()
    {
    }
}
