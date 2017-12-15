package com.liu.app.web;

import android.content.Intent;

/**
 * Created by liu on 2017/11/30.
 */

public interface IWebPlugin
{
    public static final int EVENT_INIT = 1;
    public static final int EVENT_DEINIT = 2;
    public static final int EVENT_RESULT_DATA = 3;

    public void init(IWebShell ws, Intent data);
    public String getName();
    public void deInit();
    public boolean exec(int funName, String json, String callback);//返回是否执行
    public boolean exec(String funName, String json, String callback);//返回是否执行
    public boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
