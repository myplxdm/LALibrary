package com.liu.alibrarytest;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.data.XDataStore;
import com.liu.app.web.IWebShell;
import com.liu.app.web.WebPluginBase;
import com.liu.lalibrary.utils.JsonHelper;

/**
 * Created by liu on 2018/3/1.
 */

public class WebDataPlugin extends WebPluginBase
{
    public static final String NAME = WebDataPlugin.class.getSimpleName();
    //
    private final String GET_ACCOUNT = "getAccount";
    private final String P_ACCOUNT = "account";
    private final String GET_PWD = "getPwd";
    private final String P_PWD = "pwd";

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        if (funName.equals(GET_ACCOUNT))
        {
            if (!TextUtils.isEmpty(callback))
            {
                shell.execJScript("javascript:" + callback.replaceAll("#",
                        JsonHelper.convertToStr("method",param.getString("method"),
                                "success","true",
                                P_ACCOUNT, XDataStore.inst().getAccount())));
            }
            return true;
        }else if (funName.equals(GET_PWD))
        {
            if (!TextUtils.isEmpty(callback))
            {
                shell.execJScript("javascript:" + callback.replaceAll("#",
                        JsonHelper.convertToStr("method",param.getString("method"),
                                "success","true",
                                P_PWD, XDataStore.inst().getSecret())));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivityResult(int i, int i1, Intent intent)
    {
        return false;
    }
}
