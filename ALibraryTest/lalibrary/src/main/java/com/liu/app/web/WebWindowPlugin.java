package com.liu.app.web;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.utils.JsonHelper;

/**
 * Created by liu on 2017/12/11.
 */

public class WebWindowPlugin extends WebPluginBase
{
    public static final String NAME = WebWindowPlugin.class.getSimpleName();
    //fun name
    private final String WND_TO_URL = "tourl";
    private final String WND_CLOSE_WINDOW = "closewindow";
    //---------------------------------------------------
    private final String WND_EXIT_TO = "exitto";
    //---------------------------------------------------
    private final String WND_MASK_BACK = "maskback";
    //
    private final String WND_RELOAD = "reload";
    //
    private boolean isNextSelfClose;

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        boolean isProc = false;
        if (funName.equals(WND_TO_URL))
        {
            boolean isShowRB = JsonHelper.getBoolen(param, IWebShell.WS_SHOW_RETURN, true);
            String url = JsonHelper.getString(param, IWebShell.WS_URL, "");
            String title = JsonHelper.getString(param, IWebShell.WS_TITLE, "");
            isNextSelfClose = JsonHelper.getBoolen(param, IWebShell.WS_IS_NEXT_SELF_CLOSE, false);
            int titleLoc = JsonHelper.getInt(param, IWebShell.WS_TITLE_LOCATION, ITitleView.TVL_MIDDLE);
            shell.openWindow(isShowRB, url, title, titleLoc);
            isProc = true;
        }else if (funName.equals(WND_CLOSE_WINDOW) || funName.equals(WND_EXIT_TO))
        {
            shell.closeWindow(JsonHelper.getInt(param, IWebShell.WS_CLOSE_PARENT_CLOSE_LEVEL, 1),
                    JsonHelper.getBoolen(param, IWebShell.WS_CLOSE_RELOAD, false),
                    JsonHelper.getString(param, IWebShell.WS_CLOSE_EXEC_JS, ""));
            isProc = true;
        }else if (funName.equals(WND_MASK_BACK))
        {
            shell.getActivity().maskBack(true);
            isProc = true;
        }else if (funName.equals(WND_RELOAD))
        {
            shell.getWeb().reload();
            isProc = true;
        }
        isProc = isProc || (execOther(funName, param, callback) == IWebPlugin.EXEC_OTHER_NO_PROC ? false : true);
        return procCallback(isProc, callback, param.getString(P_ALIAS));
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IWebShell shell = webShell.get();
        if (shell == null || data == null) return false;
        if (data.getBooleanExtra(IWebShell.WS_CLOSE_RELOAD, false))
        {
            shell.getWeb().reload();
        }
        String rj = data.getStringExtra(IWebShell.WS_CLOSE_EXEC_JS);
        if (!TextUtils.isEmpty(rj))
        {
            shell.execJScript("javascript:" + rj);
        }
        if (isNextSelfClose) shell.closeWindow(1, false, "");
        return false;
    }
}
