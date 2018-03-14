package com.liu.app.web;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.pluginImpl.PluginFileUpload;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.utils.JsonHelper;

/**
 * Created by liu on 2018/3/9.
 */

public class WebPhotoPlugin extends WebPluginBase
{
    private final String UPLOAD_PHOTO = "photoup";//上传图片
    private final String P_UP_URL = "url";//上传图片的地址
    private final String P_PHOTO_CHOOSE_TYPE = "ct";//弹出选择框类型
    private final String P_UP_KEY = "upkey";//上传图片的key

    private final String P_IS_CUT = "isCut";//是否裁剪，默认为否
    private final String P_CUT_ASPECTX = "aspectX";//宽比例
    private final String P_CUT_ASPECTY = "aspectY";//高比例
    private final String P_OUT_EDGE_PX = "edge";//输出最大宽高像素,大于这个值就等比例缩小

    //
    private String callback;
    private String alias;


    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        final IWebShell shell = webShell.get();
        if (shell == null) return false;
        if (funName.equals(UPLOAD_PHOTO))
        {
            String url = JsonHelper.getString(param, P_UP_URL, "");
            String key = JsonHelper.getString(param, P_UP_KEY, "");
            alias = JsonHelper.getString(param, P_ALIAS, "");
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(key))
            {
                return procCallback(true, false, callback, param.getString(P_ALIAS));
            }
            shell.getActivity().getPluginByName(PluginFileUpload.NAME).exec(null,
                    PluginFileUpload.packetParam(url, param.getIntValue(P_CUT_ASPECTX),
                            param.getIntValue(P_CUT_ASPECTY), param.getIntValue(P_OUT_EDGE_PX),
                            key, null, null, param.getIntValue(P_PHOTO_CHOOSE_TYPE)), new IPluginEvent()
                    {
                        @Override
                        public void pluginResult(final boolean isSuccess, final String result, Object param)
                        {
                            shell.getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    uploadResult(isSuccess, result);
                                }
                            });
                        }

                        @Override
                        public void pluginClose(boolean isSuccess, String result)
                        {

                        }
                    });
            this.callback = callback;
        }
        return false;
    }

    private void uploadResult(boolean isSuccess, String result)
    {
        if (isSuccess)
        {
            procCallback(true, callback, JsonHelper.convert(SUCCESS, isSuccess,
                    METHOD, alias,
                    "result", result));
        }else
        {
            Toast.makeText(webShell.get().getActivity(), result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }
}
