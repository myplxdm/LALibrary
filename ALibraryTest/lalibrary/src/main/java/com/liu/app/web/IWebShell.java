package com.liu.app.web;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.webview.WebViewEx;

/**
 * Created by liu on 2017/12/1.
 */

public interface IWebShell
{
    public static final int REQ_BASE = 0x2f00;
    public static final int REQ_PK = REQ_BASE + 1;
    public static final int REQ_OPEN_WEB_WINDOW = REQ_BASE + 10;
    //
    public static final String WS_CLOSE_RELOAD = "bCloseRelad";//当前关闭后，父窗体的web刷新
    public static final String WS_CLOSE_PARENT_CLOSE_LEVEL = "num";//当前窗体关闭后父窗体多级一并关闭
    public static final String WS_CLOSE_EXEC_JS = "execJs";//当前关闭后，父窗体执行js
    public static final String WS_TITLE = "wct";
    public static final String WS_URL = "wcurl";
    public static final String WS_TITLE_LOCATION = "titleLoc";
    public static final String WS_RETURN_RES_ID = "btReturnResId";
    public static final String WS_SHOW_RETURN = "bShowReturn";

    public void openWindow(boolean isShowReturn, String url, String title, int titleLoc, boolean bCloseReload, int closeLevel);
    public void closeWindow(int closeLevel, boolean bCloseReload, String execJs);
    public AbsActivity getActivity();
    public Class getWebActivityClass();
    public IWebPlugin getWebPluginByName(String name);
    public View getView(int resId);
    public void execJScript(String js);
    public void execPlugin(String funName, JSONObject json, String callback);
    public WebViewEx getWeb();
    public ITitleView getTitleView();
    public void jsCall(String funName, Object param);
    public void pluginCallback(String funName, Object param);
}
