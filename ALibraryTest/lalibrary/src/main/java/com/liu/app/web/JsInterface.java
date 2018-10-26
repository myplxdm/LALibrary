package com.liu.app.web;

import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/28.
 */

public class JsInterface
{
    public static final String JS_NAME = "android";
    private IWebShell shell;

    public JsInterface(IWebShell shell)
    {
        this.shell = shell;
    }

    @JavascriptInterface
    public void exec(String json)
    {
        final JSONArray ary = JSON.parseArray(json);
        shell.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                JSONObject js;
                for (int i = 0;i < ary.size();i++)
                {
                    js = ary.getJSONObject(i);
                    shell.execPlugin(js);
                }
            }
        });

    }
}
