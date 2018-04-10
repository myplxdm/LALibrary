package com.liu.alibrarytest;

import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.liu.app.JniApi;
import com.liu.app.data.XDataStore;
import com.liu.app.network.LjhHttpUtils;
import com.liu.app.network.NetResult;


/**
 * Created by liu on 16/11/2.
 */

public class Api
{
    public static final int STATE_CODE_OK = 0;
    public static final int STATE_CODE_ERROR = -1;
    public static final String VC_TYPE_SMS_LOGIN = "login";
    public static final String VC_TYPE_BIND = "bind";


    public interface JsonListener
    {
        public void onJson(boolean isSuccess, String json);
    }

    public interface NetResultListener
    {
        public void onNetResult(boolean isSuccess, NetResult nr);
    }

    public interface VercodeListener
    {
        public void onGetVercode(boolean isSuccess, String msg);
    }

    private Api() {}

    private static class SingletonHolder
    {
        private static final Api INSTANCE = new Api();
    }

    public static final Api inst()
    {
        return SingletonHolder.INSTANCE;
    }

    public void getVercode(final String phone, final String type, final VercodeListener rl)
    {
        String url = JniApi.inst().reqEncode(Config.API_GET_VER_CODE_URL, null,
                                             new String[]{"mobile", "type", "t"},
                                             new String[]{phone, type, String.valueOf(System.currentTimeMillis())});
        LjhHttpUtils.inst().get(url, new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, String result)
            {
                switch (state)
                {
                    case LjhHttpUtils.HU_STATE_OK:
                        result = Uri.decode(JniApi.inst().decodeResult(result));
                        NetResult nr = JSON.parseObject(result, NetResult.class);
                        rl.onGetVercode(nr.state == Api.STATE_CODE_OK, nr.ret);
                        break;
                    case LjhHttpUtils.HU_STATE_NO_NET:
                    case LjhHttpUtils.HU_STATE_ERR:
                        rl.onGetVercode(false, result);
                        break;
                }
            }

            @Override
            public void onHttpReqProgress(float v)
            {
            }
        });
    }

    public void getJson(String url, final JsonListener listener)
    {
        LjhHttpUtils.inst().get(url, new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, String result)
            {
                listener.onJson(state == LjhHttpUtils.HU_STATE_OK, result);
            }

            @Override
            public void onHttpReqProgress(float v)
            {

            }
        });
    }

    public void reqInfo(String url, String uid, String shopId, final NetResultListener listener)
    {
        if (shopId != null)
        {
            url = JniApi.inst().reqEncode(url, null,
                    new String[]{"uid","shopId","t"}, new String[]{uid, shopId,String.valueOf(System.currentTimeMillis())});
        }else
        {
            url = JniApi.inst().reqEncode(url, null,
                    new String[]{"uid","t"}, new String[]{uid,String.valueOf(System.currentTimeMillis())});
        }
        LjhHttpUtils.inst().get(url, new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, String result)
            {
                switch (state)
                {
                    case LjhHttpUtils.HU_STATE_OK:
                        result = Uri.decode(JniApi.inst().decodeResult(result));
                        NetResult nr = JSON.parseObject(result, NetResult.class);
                        listener.onNetResult(nr.state == Api.STATE_CODE_OK, nr);
                        break;
                    case LjhHttpUtils.HU_STATE_NO_NET:
                    case LjhHttpUtils.HU_STATE_ERR:
                        listener.onNetResult(false, new NetResult(state, result, ""));
                        break;
                }
            }

            @Override
            public void onHttpReqProgress(float v)
            {
            }
        });
    }
}
