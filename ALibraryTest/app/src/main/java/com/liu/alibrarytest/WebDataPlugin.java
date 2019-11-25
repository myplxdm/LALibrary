package com.liu.alibrarytest;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.web.IWebShell;
import com.liu.app.web.WebPluginBase;

/**
 * Created by liu on 2018/3/1.
 */

public class WebDataPlugin extends WebPluginBase
{
    public static final String NAME = WebDataPlugin.class.getSimpleName();
    //
    private final String GET_TOKEN = "getToken";
    private final String P_TOKEN = "token";
    //
    private final String SET_FACE = "setFace";
    private final String P_FACE = "face";
    //
    private final String GET_LOCATION = "getLocation";
    private final String P_LAT = "lat";
    private final String P_LNG = "lng";
    private final String P_PROVINCE = "province";
    private final String P_CITY = "city";
    //
    private final String LOGOUT = "logout";

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        if (funName.equals(GET_TOKEN))
        {
            shell.execJScript("javascript:test()");
        }
        return false;
    }

    @Override
    public boolean onActivityResult(int i, int i1, Intent intent)
    {
        return false;
    }
}
