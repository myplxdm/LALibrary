package com.liu.app.web;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.update.UpdateManager;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.JsonHelper;
import com.liu.lalibrary.utils.Utils;

/**
 * Created by liu on 2017/12/27.
 */

public class WebSystemPlugin extends WebPluginBase
{
    public static final String NAME = "webSystemPlugin";
    //
    private final String SEND_SMS = "sendsms";
    private final String P_SS_TOS = "tos";
    private final String P_SS_TXT = "txt";
    //------------------------------------------------
    private final String VERSION = "version";
    //------------------------------------------------
    private final String CALL_PHONE = "callphone";
    private final String P_CP_PHONE = "phone";
    //------------------------------------------------
    private final String UPDAGE = "update";
    private final String P_U_URL = "url";

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        boolean isProc = false;
        if (funName.equals(SEND_SMS))
        {
            Utils.sendSms(shell.getActivity(), JsonHelper.getString(param, P_SS_TOS, ""),
                    JsonHelper.getString(param, P_SS_TXT, ""));
            isProc = true;
        }else if (funName.equals(VERSION))
        {
            if (!TextUtils.isEmpty(callback))
            {
                shell.execJScript("javascript:" + callback.replaceAll("#",
                        JsonHelper.convertToStr("method",param.getString("method"),
                                "success","true",
                                "ver",AppUtils.getVersionName(shell.getActivity()))));
            }
            return true;
        }else if (funName.equals(CALL_PHONE))
        {
            Utils.systemCall(shell.getActivity(), JsonHelper.getString(param, P_CP_PHONE, ""));
            isProc = true;
        }else  if (funName.equals(UPDAGE))
        {
            new UpdateManager(shell.getActivity(), JsonHelper.getString(param, P_U_URL, ""));
            isProc = true;
        }
        isProc = isProc || (execOther(funName, param, callback) == IWebPlugin.EXEC_OTHER_NO_PROC ? false : true);
        return procCallback(isProc, param, callback, shell);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }
}
