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

    protected boolean procCallback(boolean isSuccess, String callback, JSONObject param, JSONObject values)
    {
        IWebShell shell = webShell.get();
        if (shell == null)return false;
        if (!TextUtils.isEmpty(callback))
        {
            if (values == null)
            {
                values = new JSONObject();
            }
            values.put(METHOD, Utils.safeStr(param.getString(METHOD)));
            values.put(P_ALIAS, Utils.safeStr(param.getString(P_ALIAS)));
            values.put(SUCCESS, isSuccess);
            shell.execJScript("javascript:" + callback.replaceAll("#", values.toJSONString()));
            return true;
        }
        return true;
    }
    public void onResume(){}
    public void onPause(){}
    public void onStop(){}
}
