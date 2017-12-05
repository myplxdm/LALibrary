package com.liu.app.web;

import android.content.Intent;

/**
 * Created by liu on 2017/11/30.
 */

public interface IWebPlugin
{
    public void init(IWebShell ws);
    public void deInit();
    public boolean exec(int funName, String json, String callback);//返回是否执行
    public boolean exec(String funName, String json, String callback);//返回是否执行
    public boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
