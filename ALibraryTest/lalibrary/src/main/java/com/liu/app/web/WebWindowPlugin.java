package com.liu.app.web;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.utils.JsonHelper;

/**
 * Created by liu on 2017/12/11.
 */

public class WebWindowPlugin extends BaseWebPlugin
{
    public static final String NAME = "webWindowPlugin";
    //fun name
    private final int WND_TO_URL = "toUrl".hashCode();
    private final int WND_CLOSE_WINDWO = "closeWindow".hashCode();
    private final int WND_EXIT_TO = "exitTo".hashCode();
    private final int WND_MASK_BACK = "maskBack".hashCode();

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public boolean exec(int funName, String json, String callback)
    {
        try
        {
            JSONObject param = JSON.parseObject(json);
            IWebShell shell = webShell.get();
            if (shell == null) return false;
            if (funName == WND_TO_URL)
            {
                boolean isShowRB = JsonHelper.getBoolen(param, IWebShell.WC_RETURN_RES_ID, true);
                shell.openWindow();
            }

            //final String title, final String url, final boolean reload, final boolean trans, final int titleAlign, final boolean isShowReturn
        }catch (Exception e){}
        return false;
    }

    @Override
    public boolean exec(String funName, String json, String callback)
    {
        return false;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }
}
