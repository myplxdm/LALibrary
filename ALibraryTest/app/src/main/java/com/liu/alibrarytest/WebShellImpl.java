package com.liu.alibrarytest;

import com.liu.app.web.IWebPlugin;
import com.liu.app.web.WebShellActivity;
import java.util.ArrayList;

/**
 * Created by liu on 2018/2/26.
 */

public class WebShellImpl extends WebShellActivity
{

    @Override
    protected void onInitView()
    {
        super.onInitView();
        titleView.setSpace(20);
        titleView.setBottomLine(0xffdcdcdc,2);
    }

    @Override
    protected void loadWebPlugins(ArrayList<IWebPlugin> list)
    {
        super.loadWebPlugins(list);
        list.add(new WebDataPlugin());
    }

    @Override
    protected int getReturnBtnResId()
    {
        return R.mipmap.btn_return_b;
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
    public void pluginCallback(String funName, Object param)
    {

    }
}
