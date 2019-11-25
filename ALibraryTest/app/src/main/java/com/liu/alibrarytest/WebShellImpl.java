package com.liu.alibrarytest;

import android.graphics.Color;
import android.view.View;

import com.liu.app.pluginImpl.PluginFileUpload;
import com.liu.app.pluginImpl.PluginPhotoChoose;
import com.liu.app.web.IWebPlugin;
import com.liu.app.web.WebPhotoPlugin;
import com.liu.app.web.WebShellActivity;

import java.util.ArrayList;

import cn.ryt.mtzf.R;

/**
 * Created by liu on 2018/2/26.
 */

public class WebShellImpl extends WebShellActivity
{

    @Override
    protected void onInitView()
    {
        super.onInitView();
        addPlugin(new PluginPhotoChoose(this));
        titleView.setBackgroundColor(Color.rgb(0,0,0));
    }

    @Override
    protected void loadWebPlugins(ArrayList<IWebPlugin> list)
    {
        list.add(new WebDataPlugin());
        super.loadWebPlugins(list);

    }

    @Override
    protected int getReturnBtnResId()
    {
        return R.mipmap.i_exit;
    }

    @Override
    public Class getWebActivityClass()
    {
        return WebShellImpl.class;
    }

    @Override
    public void jsCall(String funName, Object param)
    {

    }

    @Override
    public Object pluginCallback(String funName, Object param)
    {
        return null;
    }
}
