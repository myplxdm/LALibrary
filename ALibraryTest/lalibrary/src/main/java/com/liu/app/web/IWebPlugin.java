package com.liu.app.web;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by liu on 2017/11/30.
 */

public interface IWebPlugin
{
    public static final int EVENT_INIT = 0;
    public static final int EVENT_DEINIT = 1;
    public static final int EVENT_RESULT_DATA = 2;
    public static final int EVENT_EXEC = 3;
    //
    public static final int EXEC_OTHER_NO_PROC = 0;//不需要处理
    public static final int EXEC_OTHER_BASE_PROC = 1;//由父类处理回调
    public static final int EXEC_OTHER_SELF_PROC = 2;//继承类自己处理回调
    public static final String METHOD = "method";
    public static final String CALLBACK = "callback";

    public void init(IWebShell ws, Intent data);
    public String getName();
    public void deInit();
    public int execOther(String method, JSONObject param, String callback);//用于继承扩展
    public boolean exec(String method, JSONObject param, String callback);//返回是否执行
    public boolean onActivityResult(int requestCode, int resultCode, Intent data);
    public void onResume();
    public void onPause();
    public void onStop();
}
