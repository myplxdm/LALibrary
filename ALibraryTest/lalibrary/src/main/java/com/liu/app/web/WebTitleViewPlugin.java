package com.liu.app.web;

import android.content.Intent;

import com.liu.lalibrary.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/4.
 */

public class WebTitleViewPlugin implements IWebPlugin
{
    private final int TV_ADD_IMG_BTN = Utils.strToInt("addImgBtn");
    private final int TV_ADD_TXT_BTN = Utils.strToInt("addTxtBtn");
    private final int TV_MD_IMG_BTN_ICO = Utils.strToInt("mdImgBtnIco");
    private final int TV_MD_TXT_BTN_TITLE = Utils.strToInt("mdTxtBtnTitle");
    private final int TV_CLEAR_ALL_BTN = Utils.strToInt("clsAllBtn");
    private final int TV_LEFT_IMG_BTN = Utils.strToInt("addLeftImgBtn");
    private final int TV_SET_TITLE = Utils.strToInt("setTitle");
    //private

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
