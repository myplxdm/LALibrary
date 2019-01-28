package com.liu.app.web;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.util.Util;
import com.liu.lalibrary.utils.JsonHelper;
import com.liu.lalibrary.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/11.
 */

public abstract class WebPluginBase implements IWebPlugin
{
    protected WeakReference<IWebShell> webShell;
    protected final String P_ALIAS = "alias";
    protected final String SUCCESS = "success";
    protected final String METHOD = "method";

    @Override
    public void init(IWebShell ws, Intent data)
    {
        webShell = new WeakReference<IWebShell>(ws);
    }

    @Override
    public void deInit()
    {
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

    protected boolean procCallback(boolean isProc, String callback, JSONObject values)
    {
        IWebShell shell = webShell.get();
        if (shell == null)return false;
        if (isProc && !TextUtils.isEmpty(callback))
        {
            shell.execJScript("javascript:" + callback.replaceAll("#", values.toJSONString()));
        }
        return isProc;
    }

    protected boolean procCallback(boolean isProc, boolean isSuccess, String callback, String alias)
    {
        return this.procCallback(isProc, callback, JsonHelper.convert(SUCCESS,isSuccess,
                                                               METHOD,Utils.safeStr(alias)));
    }

    protected boolean procCallback(boolean isProc, String callback, String alias)
    {
        return this.procCallback(isProc, true, callback, Utils.safeStr(alias));
    }

    public void onResume(){}
    public void onPause(){}
    public void onStop(){}
}
