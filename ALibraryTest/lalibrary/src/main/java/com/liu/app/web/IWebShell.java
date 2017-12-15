package com.liu.app.web;

import android.view.View;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.titleview.ITitleView;

/**
 * Created by liu on 2017/12/1.
 */

public interface IWebShell
{
    public static final int REQ_BASE = 0x2f00;
    public static final int REQ_PK = REQ_BASE + 1;
    public static final int REQ_OPEN_WEB_WINDOW = REQ_BASE + 10;
    //
    public static final String WC_CLOSE_RELOAD	= "bCloseRelad";//当前关闭后，父窗体的web刷新
    public static final String WC_CLOSE_PARENT_CLOSE = "closeParentClose";//当前窗体关闭后父窗体也一并关闭
    public static final String WC_CLOSE_EXEC_JS = "execJs";//当前关闭后，父窗体执行js
    public static final String WC_TITLE	= "wct";
    public static final String WC_URL = "wcurl";
    public static final String WC_RETURN_RES_ID = "btReturnResId";

    public void openWindow(boolean isShowReturn, String url, String title, boolean bCloseReload);
    public void closeWindow(boolean parentClose, String execJs);
    public AbsActivity getActvity();
    public IWebPlugin getWebPluginByName(String name);
    public View getView(int resId);
    public void execJScript(String js);
    public ITitleView getTitleView();
}
