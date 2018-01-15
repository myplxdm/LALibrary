package com.liu.app.web;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.utils.JsonHelper;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/11.
 */

public abstract class WebPluginBase implements IWebPlugin
{
    protected WeakReference<IWebShell> webShell;

    @Override
    public void init(IWebShell ws, Intent data)
    {
        webShell = new WeakReference<IWebShell>(ws);
    }

    @Override
    public void deInit()
    {
        webShell = null;
    }

    @Override
    public String getName()
    {
        return getClass().getSimpleName();
    }

    @Override
    public int execOther(String funName, JSONObject param, String callback)
    {
        return IWebPlugin.EXEC_OTHER_NO_PROC;
    }

    protected boolean procCallback(boolean isProc, JSONObject param, String callback, IWebShell shell)
    {
        if (isProc && !TextUtils.isEmpty(callback))
        {
            shell.execJScript("javascript:" + callback.replaceAll("#", JsonHelper.convertToStr("success","true",
                    "method",param.getString("alias"))));
        }
        return isProc;
    }
}
