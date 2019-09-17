package com.liu.app.web;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.wx.WXSDK;
import com.liu.lalibrary.utils.JsonHelper;
import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * Created by liu on 2017/12/29.
 */

public class WebWXPlugin extends WebPluginBase implements WXSDK.WXSDXListener
{
    public static final String NAME = WebWXPlugin.class.getSimpleName();
    //---------------------------------------------
    private final String SHARE = "sharewx";
    private final String P_URL = "url";
    private final String P_TITLE = "title";
    private final String P_DESC = "desc";
    private final String P_IMG_URL = "imgurl";
    private final String P_SHARE_TYPE = "sharetype";
    //
    private final String WEIOAUTH = "weioauth";
    //---------------------------------------------
    private final String PAY = "wxpay";
    private final String P_PARTENER_ID = "partenerId";
    private final String P_PREPAY_ID = "prepayId";
    private final String P_PACKAGE_NAME = "packageName";
    private final String P_NONCE_STR = "nonceStr";
    private final String P_TIME_STAMP = "timeStamp";
    private final String P_SIGN = "sign";
    private String payCallback;
    private String authCallback;

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        boolean isProc = false;
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        if (funName.equals(SHARE))
        {
            WXSDK.inst().setListener(this);
            WXSDK.inst().showShareMenu(shell.getActivity(), JsonHelper.getString(param, P_URL, ""),
                    JsonHelper.getString(param, P_TITLE, ""), JsonHelper.getString(param, P_DESC, ""),
                    JsonHelper.getString(param, P_IMG_URL, ""), JsonHelper.getInt(param, P_SHARE_TYPE, WXSDK.WX_SHARE_TYPE_FIREND));
            isProc = true;
        }else if (funName.equals(WEIOAUTH))
        {
            WXSDK.inst().setListener(this);
            WXSDK.inst().loginWx();
            authCallback = callback;
            return true;
        }else if (funName.equals(PAY))
        {
            WXSDK.inst().setListener(this);
            WXSDK.inst().pay(JsonHelper.getString(param, P_PARTENER_ID, ""),
                    JsonHelper.getString(param, P_PREPAY_ID, ""),
                    JsonHelper.getString(param, P_PACKAGE_NAME, ""),
                    JsonHelper.getString(param, P_NONCE_STR, ""),
                    JsonHelper.getString(param, P_TIME_STAMP, ""),
                    JsonHelper.getString(param, P_SIGN, ""));
            payCallback = callback;
            return true;
        }
        isProc = isProc || (execOther(funName, param, callback) == IWebPlugin.EXEC_OTHER_NO_PROC ? false : true);
        if (!isProc)return false;
        procCallback(true, callback, param, null);
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }

    //-------------------------------------------------------------
    @Override
    public void onWXResp(BaseResp baseResp)
    {
    }

    @Override
    public void onWXResult(int type, JSONObject result)
    {
        String js = null;
        if (type == WXSDK.TYPE_PAY_RESULT && payCallback != null)
        {
            js = String.format("javascript:" + payCallback + "(%d)", result.getIntValue(WXSDK.WX_FIELD_ERRCODE));

        } else if (type == WXSDK.TYPE_LOGIN_RESULT && authCallback != null)
        {
            js = String.format("javascript:" + authCallback + "('%s','%s')",
                    result.getString(WXSDK.WX_FIELD_OPENID),
                    result.getString(WXSDK.WX_FIELD_UNIONID));
        }
        if (js == null) return;
        final IWebShell shell = webShell.get();
        if (shell == null) return;
        final String finalJs = js;
        shell.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                shell.execJScript(finalJs);
            }
        });
    }
}
