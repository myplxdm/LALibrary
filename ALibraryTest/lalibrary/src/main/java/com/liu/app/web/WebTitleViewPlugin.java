package com.liu.app.web;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/4.
 */

public class WebTitleViewPlugin implements IWebPlugin
{
    private final String P_CLOSE_RELOAD = "closeReload";
    private final String P_BTN_TYPE = "btnType";
    //--------------------------------------------
    private final int TV_ADD_IMG_BTN = Utils.strToInt("addImgBtn");
    private final String P_IB_IMG_URL = "btImgUrl";
    private final String P_IB_TITLE = "title";
    private final String P_IB_URL = "url";
    //--------------------------------------------
    private final int TV_ADD_TXT_BTN = Utils.strToInt("addTxtBtn");
    private final int TV_MD_IMG_BTN_ICO = Utils.strToInt("mdImgBtnIco");
    private final int TV_MD_TXT_BTN_TITLE = Utils.strToInt("mdTxtBtnTitle");
    private final int TV_CLEAR_ALL_BTN = Utils.strToInt("clsAllBtn");
    private final int TV_LEFT_IMG_BTN = Utils.strToInt("addLeftImgBtn");
    private final int TV_SET_TITLE = Utils.strToInt("setTitle");


    private WeakReference<IWebShell> webShell;

    @Override
    public void init(IWebShell ws)
    {
        webShell = new WeakReference<IWebShell>(ws);
    }

    @Override
    public void deInit()
    {
        webShell = null;
    }

    @Override
    public boolean exec(int funName, String json, String callback)
    {
        JSONObject param;
        IWebShell shell = webShell.get();
        if (shell == null)return false;
        ITitleView tv = shell.getTitleView();
        try
        {
            if (funName == TV_ADD_IMG_BTN)
            {
                param = JSON.parseObject(json);
                //tv.addView(LTitleView.TVL_RIGHT, param.getString(P_IB_IMG_URL));
            }else if (funName == TV_ADD_TXT_BTN)
            {

            }else if (funName == TV_MD_IMG_BTN_ICO)
            {

            }else if (funName == TV_MD_TXT_BTN_TITLE)
            {

            }else if (funName == TV_CLEAR_ALL_BTN)
            {

            }else if (funName == TV_LEFT_IMG_BTN)
            {

            }else if (funName == TV_SET_TITLE)
            {

            }
        }catch (Exception e)
        {
            return true;
        }
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
