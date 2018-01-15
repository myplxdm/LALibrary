package com.liu.alibrarytest;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.app.web.WebShellActivity;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;

/**
 * Created by liu on 2017/11/24.
 */

public class TestActivity extends WebShellActivity
{
    @Override
    public Class getWebActivityClass()
    {
        return TestActivity.class;
    }

    @Override
    public void uiUpdate(int funName, Object param)
    {

    }

    @Override
    public void uiClick(int funName)
    {

    }

    @Override
    protected int getReturnBtnResId()
    {
        return R.mipmap.btn_return_w;
    }
}
