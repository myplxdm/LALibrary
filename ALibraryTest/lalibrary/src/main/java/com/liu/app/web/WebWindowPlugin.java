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
    public static final String NAME = "webWindowPlugin";
    //fun name
    private final String WND_TO_URL = "tourl";
    private final String WND_CLOSE_WINDOW = "closewindow";
    //---------------------------------------------------
    private final String WND_EXIT_TO = "exitto";
    private final String P_EXIT_NUM = "num";
    //---------------------------------------------------
    private final String WND_MASK_BACK = "maskback";

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        boolean isProc = false;
        if (funName.equals(WND_TO_URL))
        {
            boolean isShowRB = JsonHelper.getBoolen(param, IWebShell.WS_RETURN_RES_ID, true);
            String url = JsonHelper.getString(param, IWebShell.WS_URL, "");
            String title = JsonHelper.getString(param, IWebShell.WS_TITLE, "");
            boolean bReload = JsonHelper.getBoolen(param, IWebShell.WS_CLOSE_RELOAD, false);
            int nCloseLevel = param.getIntValue(IWebShell.WS_CLOSE_PARENT_CLOSE_LEVEL);
            int titleLoc = JsonHelper.getInt(param, IWebShell.WS_TITLE_LOCATION, ITitleView.TVL_MIDDLE);
            shell.openWindow(isShowRB, url, title, titleLoc, bReload, nCloseLevel);
            isProc = true;
        }else if (funName.equals(WND_CLOSE_WINDOW))
        {
            shell.closeWindow(param.getIntValue(IWebShell.WS_CLOSE_PARENT_CLOSE_LEVEL),
                    JsonHelper.getBoolen(param, IWebShell.WS_CLOSE_RELOAD, false),
                    JsonHelper.getString(param, IWebShell.WS_CLOSE_EXEC_JS, ""));
            isProc = true;
        }else if (funName.equals(WND_EXIT_TO))
        {
            AbsActivity.exitToNum(JsonHelper.getInt(param, P_EXIT_NUM, 0), null);
            isProc = true;
        }else if (funName.equals(WND_MASK_BACK))
        {
            shell.getActivity().maskBack(true);
            isProc = true;
        }
        isProc = isProc || (execOther(funName, param, callback) == IWebPlugin.EXEC_OTHER_NO_PROC ? false : true);
        return procCallback(isProc, param, callback, shell);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        if (data.getBooleanExtra(IWebShell.WS_CLOSE_RELOAD, false))
        {
            shell.getWeb().reload();
        }
        int nCloseLevel = data.getIntExtra(IWebShell.WS_CLOSE_PARENT_CLOSE_LEVEL, 0);
        if (nCloseLevel == 1) shell.getActivity().finish();
        else if (nCloseLevel > 1)
        {
            shell.closeWindow(nCloseLevel - 1, false, "");
        }
        String rj = data.getStringExtra(IWebShell.WS_CLOSE_EXEC_JS);
        if (!TextUtils.isEmpty(rj))
        {
            shell.execJScript(rj);
        }
        return false;
    }
}
