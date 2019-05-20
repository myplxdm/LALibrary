package com.liu.app.wx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.loader.LoaderBase;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.HttpUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu on 2017/9/5.
 */

public class WXSDK extends LoaderBase implements IWXAPIEventHandler
{
    public static final String WX_APPID_TYPE_LOGIN = "login";
    public static final String WX_APPID_TYPE_PAY = "pay";
    public static final String WX_APPID_TYPE_OTHER = "other";
    public static final int WX_SHARE_TYPE_FIREND = 0x1;
    public static final int WX_SHARE_TYPE_TIMELINE = 0x10;
    //
    public static final String WX_FIELD_OPENID = "openid";
    public static final String WX_FIELD_UNIONID = "unionid";
    public static final String WX_FIELD_NICKNAME = "nickname";
    public static final String WX_FIELD_HEADIMGURL = "headimgurl";
    public static final String WX_FIELD_ERRCODE = "errcode";
    public static final String WX_FIELD_ERRMSG = "errmsg";
    //
    public static final int TYPE_LOGIN_RESULT = 1;
    public static final int TYPE_USER_REJECT_AUTH = 2;
    public static final int TYPE_USER_CANCEL_AUTH = 3;
    public static final int TYPE_PAY_RESULT = 4;
    //
    public static final String WX_RESULT_IS_SUCCESS = "isSuccess";

    //
    private static class SingletonHolder
    {
        private static final WXSDK INSTANCE = new WXSDK();
    }

    public static final WXSDK inst()
    {
        return SingletonHolder.INSTANCE;
    }

    //
    public interface WXSDXListener
    {
        public void onWXResp(BaseResp baseResp);

        public void onWXResult(int type, JSONObject result);
    }

    private WXSDXListener listener;
    private IWXAPI wxApi;
    private HashMap<String, WxAppInfo> mapAppInfo = new HashMap<>();
    private String curType;
    private WxAppInfo curWxInfo;
    private Context context;

    public synchronized void addAppInfo(String type, String appId, String secret)
    {
        WxAppInfo info = new WxAppInfo(appId, secret);
        mapAppInfo.put(type, info);
        if (wxApi == null)
        {
            curType = type;
            curWxInfo = info;
            wxApi = WXAPIFactory.createWXAPI(context, appId);
            wxApi.registerApp(appId);
        }
    }

    public void handle(Intent i, IWXAPIEventHandler handler)
    {
        wxApi.handleIntent(i, handler);
    }

    public void setListener(WXSDXListener listener)
    {
        this.listener = listener;
    }

    public boolean switchType(String type)
    {
        if (checkApiInst())
        {
            if (!curType.equals(type))
            {
                WxAppInfo info = getWxInfoByType(type);
                if (info == null)
                {
                    LogUtils.LOGE(WXSDK.class, String.format("can't find %s type appid", type));
                    return false;
                }
                curWxInfo = info;
                wxApi.registerApp(info.appId);
            }
            return true;
        }
        return false;
    }

    public void loginWx()
    {
        if (switchType(WX_APPID_TYPE_LOGIN))
        {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo";
            boolean s = wxApi.sendReq(req);
            LogUtils.LOGD(WXSDK.class, "sendReq state = %s", String.valueOf(s));
        }
    }

    public boolean pay(String partenerId, String prepayId, String packageName, String nonceStr, String timeStamp, String sign)
    {
        if (switchType(WX_APPID_TYPE_PAY))
        {
            PayReq request = new PayReq();
            request.appId = getWxInfoByType(WX_APPID_TYPE_PAY).appId;
            request.partnerId = partenerId;
            request.prepayId = prepayId;
            request.packageValue = packageName;
            request.nonceStr = nonceStr;
            request.timeStamp = timeStamp;
            request.sign = sign;
            return wxApi.sendReq(request);
        }
        return false;
    }

    public void showShareMenu(AbsActivity activity, final String url, final String title, final String desc, final String imgUrl,
                              int shareType)
    {
        if (shareType == (WX_SHARE_TYPE_FIREND | WX_SHARE_TYPE_TIMELINE))
        {
            new AlertView("分享", null, "取消", null, new String[]{"发送给朋友", "分享朋友圈"},
                    activity, AlertView.Style.ActionSheet, new OnItemClickListener()
            {
                @Override
                public void onItemClick(Object o, int position)
                {
                    if (position == -1)return;
                    sendUrl(title, desc, imgUrl, url, position == 1);
                }
            }).show();
//            new BottomSheet.Builder(activity, R.style.BottomSheetStyleDialog).title("分享").sheet(R.menu.menu_wx_share).listener(new DialogInterface.OnClickListener()
//            {
//                @Override
//                public void onClick(DialogInterface dialog, int which)
//                {
//                    sendUrl(title, desc, imgUrl, url, which == 1);
//                }
//            }).grid().build().show();
        } else
        {
            sendUrl(title, desc, imgUrl, url, (shareType & WX_SHARE_TYPE_TIMELINE) == WX_SHARE_TYPE_TIMELINE);
        }
    }

    public boolean sendUrl(String title, String content, String iconUrl,
                           String httpUrl, boolean bTimeline)
    {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
        msg.thumbData = ImageURLUtil.getHtmlByteArray(iconUrl);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = bTimeline ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        return wxApi.sendReq(req);
    }

    public boolean isInstall()
    {
        return wxApi != null ? wxApi.isWXAppInstalled() : false;
    }

    /***************  private fun ***************/
    private WxAppInfo getWxInfoByType(String type)
    {
        if (wxApi != null)
        {
            WxAppInfo info = mapAppInfo.get(type);
            if (info == null)
            {
                for (Map.Entry<String, WxAppInfo> e : mapAppInfo.entrySet())
                {
                    info = e.getValue();
                    break;
                }
            }
            return info;
        }
        return null;
    }

    private boolean checkApiInst()
    {
        if (wxApi == null)
        {
            LogUtils.LOGE(WXSDK.class, "wxApi is null");
            return false;
        }
        return true;
    }

    private String buildTransaction(final String type)
    {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    private void getAccessTokenWithRefreshToken(String token)
    {
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s",
                                   curWxInfo.appId, token);
        HttpUtils.doGetAsyn(url, new HttpUtils.CallBack()
        {
            @Override
            public void onRequestComplete(String result)
            {
                if (result != null)
                {
                    JSONObject obj = JSON.parseObject(result);
                    if (obj.containsKey(WX_FIELD_ERRCODE))
                    {
                        loginWx();
                    } else
                    {
                        getUserInfo(obj.getString("openid"), obj.getString("access_token"), obj.getString("refresh_token"));
                    }
                }
            }
        });
    }

    private void getUserInfo(String openId, String token, final String refreshToken)
    {
        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s", token, openId);
        HttpUtils.doGetAsyn(url, new HttpUtils.CallBack()
        {
            @Override
            public void onRequestComplete(String result)
            {
                if (result != null)
                {
                    JSONObject json = JSON.parseObject(result);
                    if (json.containsKey("errcode"))
                    {
                        getAccessTokenWithRefreshToken(refreshToken);
                        return;
                    }
                    json.put(WX_RESULT_IS_SUCCESS, true);
                    listener.onWXResult(TYPE_LOGIN_RESULT, json);
                } else
                {
                    JSONObject json = new JSONObject();
                    json.put(WX_RESULT_IS_SUCCESS, false);
                    listener.onWXResult(TYPE_LOGIN_RESULT, json);
                }
            }
        });
    }

    private void getAccessToken(String code)
    {
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                                   curWxInfo.appId, curWxInfo.secret, code);
        HttpUtils.doGetAsyn(url, new HttpUtils.CallBack()
        {
            @Override
            public void onRequestComplete(String result)
            {
                if (result != null)
                {
                    JSONObject json = JSON.parseObject(result);
                    if (json.containsKey("errcode"))
                    {
                        json.put(WX_RESULT_IS_SUCCESS, true);
                        listener.onWXResult(TYPE_LOGIN_RESULT, json);
                        return;
                    }
                    getUserInfo(json.getString("openid"), json.getString("access_token"), json.getString("refresh_token"));
                } else
                {
                    JSONObject json = new JSONObject();
                    json.put(WX_RESULT_IS_SUCCESS, false);
                    listener.onWXResult(TYPE_LOGIN_RESULT, json);
                }
            }
        });
    }

    /***************  IWXAPIEventHandler start ***************/
    @Override
    public void onReq(BaseReq baseReq)
    {

    }

    @Override
    public void onResp(BaseResp baseResp)
    {
        if (listener == null) return;
        switch (baseResp.getType())
        {
            case ConstantsAPI.COMMAND_SENDAUTH:
                SendAuth.Resp re = ((SendAuth.Resp) baseResp);
                if (re.errCode == 0)
                {
                    getAccessToken(re.code);
                } else if (re.errCode == -4)
                {
                    listener.onWXResult(TYPE_USER_REJECT_AUTH, null);
                } else if (re.errCode == -2)
                {
                    listener.onWXResult(TYPE_USER_CANCEL_AUTH, null);
                }
                break;
            case ConstantsAPI.COMMAND_PAY_BY_WX:
                JSONObject json = new JSONObject();
                json.put(WX_FIELD_ERRCODE, baseResp.errCode);
                json.put(WX_FIELD_ERRMSG, baseResp.errStr);
                listener.onWXResult(TYPE_PAY_RESULT, json);
                break;
        }
        listener.onWXResp(baseResp);
    }

    /***************  IWXAPIEventHandler end ***************/

    private class WxAppInfo
    {
        public String appId;
        public String secret;

        public WxAppInfo(String appId, String secret)
        {
            this.appId = appId;
            this.secret = secret;
        }
    }

    /***************  ILoader  ***************/
    @Override
    public void loaderInit(Context context)
    {
        this.context = context;
    }

    @Override
    public void loaderUnInit(Context context)
    {
        this.context = null;
    }
}
