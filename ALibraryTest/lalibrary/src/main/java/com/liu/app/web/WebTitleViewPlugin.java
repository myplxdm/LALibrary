package com.liu.app.web;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.utils.JsonHelper;
import com.liu.lalibrary.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liu on 2017/12/4.
 */

public class WebTitleViewPlugin extends BaseWebPlugin implements ITitleView.TitleViewListener
{
    public static final String NAME = "pluginTitleView";

    private final int BTN_TYPE_NORMAL = 0;
    private final int BTN_TYPE_PK = 1;
    private final int BTN_TYPE_JS = 2;

    class BtnInfo
    {
        public String openUrl;
        public String title;
        public boolean closeReload;
        public int btnType;
    }

    private final String P_URL = "url";
    private final String P_TITLE = "title";
    private final String P_CLOSE_RELOAD = "closeReload";
    private final String P_BTN_TYPE = "btnType";
    private final String P_INDEX = "index";
    //--------------------------------------------
    private final int TV_ADD_IMG_BTN = "addImgBtn".hashCode();
    private final String P_IB_IMG_URL = "btImgUrl";
    private final String P_IB_RES_ID = "btResId";
    //--------------------------------------------
    private final int TV_ADD_TXT_BTN = "addTxtBtn".hashCode();
    private final String P_TB_TEXT = "btText";
    private final String P_TB_TEXT_COLOR = "btTextColor";
    private final String P_TB_TEXT_SIZE = "textSize";
    //--------------------------------------------
    private final int TV_MD_IMG_BTN_ICO = "mdImgBtnIco".hashCode();
    private final int TV_MD_TXT_BTN_TITLE = "mdTxtBtnTitle".hashCode();
    private final int TV_CLEAR_ALL_BTN = "clsAllBtn".hashCode();
    private final int TV_LEFT_IMG_BTN = "addLeftImgBtn".hashCode();
    //
    private final int TV_SET_TITLE = "setTitle".hashCode();
    private final int TV_ADD_TITLE = "addTitle".hashCode();
    private final int TV_ADD_RETURN = "addReturn".hashCode();


    private HashMap<View, BtnInfo> mapBtnInfo = new HashMap<>();

    @Override
    public void init(IWebShell ws, Intent data)
    {
        ITitleView tv = ws.getTitleView();
        tv.setTitleViewListener(this);
        //
        String title = data.getStringExtra(IWebShell.WC_TITLE);
        int resId = data.getIntExtra(IWebShell.WC_RETURN_RES_ID, -1);
        if (resId != -1)
        {
            exec(TV_ADD_RETURN, JsonHelper.convert(P_IB_RES_ID, resId).toJSONString(), null);
        }
        exec(TV_ADD_TITLE, JsonHelper.convert(P_TITLE, title).toJSONString(), null);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    private boolean exec(int funName, JSONObject param, String callback)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        ITitleView tv = shell.getTitleView();
        View view;
        boolean isProc = false;
        if (funName == TV_ADD_IMG_BTN)
        {
            view = tv.addView(LTitleView.TVL_RIGHT, param.getString(P_IB_IMG_URL), true);
            genBtnInfo(param, view);
            isProc = true;
        } else if (funName == TV_ADD_TXT_BTN)
        {
            view = tv.addView(LTitleView.TVL_RIGHT, param.getString(P_TB_TEXT),
                    param.getIntValue(P_TB_TEXT_SIZE), param.getIntValue(P_TB_TEXT_COLOR), true);
            genBtnInfo(param, view);
            isProc = true;
        } else if (funName == TV_MD_IMG_BTN_ICO)
        {
            tv.mdImgView(LTitleView.TVL_RIGHT, param.getIntValue(P_INDEX), param.getString(P_IB_IMG_URL));
            isProc = true;
        } else if (funName == TV_MD_TXT_BTN_TITLE)
        {
            tv.mdTxtView(LTitleView.TVL_RIGHT, param.getIntValue(P_INDEX), param.getString(P_TB_TEXT));
            isProc = true;
        } else if (funName == TV_CLEAR_ALL_BTN)
        {
            tv.clearView(LTitleView.TVL_RIGHT);
            isProc = true;
        } else if (funName == TV_LEFT_IMG_BTN)
        {
            view = tv.addView(LTitleView.TVL_LEFT, param.getString(P_IB_IMG_URL), true);
            genBtnInfo(param, view);
            isProc = true;
        } else if (funName == TV_ADD_TITLE)
        {
            tv.addView(LTitleView.TVL_RIGHT, param.getString(P_TB_TEXT),
                    param.getIntValue(P_TB_TEXT_SIZE), param.getIntValue(P_TB_TEXT_COLOR), true);
            isProc = true;
        } else if (funName == TV_SET_TITLE)
        {
            tv.mdTxtView(LTitleView.TVL_MIDDLE, 0, param.getString(P_TITLE));
            isProc = true;
        } else if (funName == TV_ADD_RETURN)
        {
            tv.addView(LTitleView.TVL_LEFT, param.getIntValue(P_IB_RES_ID), true);
            isProc = true;
        }
        if (isProc && !TextUtils.isEmpty(callback))
        {
            shell.execJScript("javascript:" + callback);
        }
        return isProc;
    }

    @Override
    public boolean exec(int funName, String json, String callback)
    {
        try
        {
            return exec(funName, JSON.parseObject(json), callback);
        }catch (Exception e){}
        return false;
    }

    @Override
    public boolean exec(String funName, String json, String callback)
    {
        return exec(funName.hashCode(), json, callback);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }

    /*********************** fun ************************/
    private void genBtnInfo(JSONObject param, View view)
    {
        BtnInfo bi = new BtnInfo();
        bi.btnType = param.getIntValue(P_BTN_TYPE);
        bi.closeReload = JsonHelper.getBoolen(param, P_CLOSE_RELOAD, false);
        bi.openUrl = JsonHelper.getString(param, P_URL, "");
        bi.title = JsonHelper.getString(param, P_TITLE, "");
        mapBtnInfo.put(view, bi);
    }

    /*********************** TitleViewListener ************************/
    @Override
    public void onViewClick(int tvl, int index, View view)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return;
        BtnInfo bi;
        switch (tvl)
        {
            case LTitleView.TVL_LEFT:
                bi = mapBtnInfo.get(view);
                if (bi == null)//return button click
                {
                    shell.getActvity().finish();
                } else
                {
                    clickBtnInfo(shell, bi);
                }
                break;
        }
    }

    private void clickBtnInfo(IWebShell shell, BtnInfo bi)
    {
        switch (bi.btnType)
        {
            case BTN_TYPE_NORMAL:

                break;
            case BTN_TYPE_PK:
                Intent i = new Intent(Intent.ACTION_VIEW);
                ComponentName com = new ComponentName(bi.title, bi.openUrl);
                i.setComponent(com);
                shell.getActvity().startActivityForResult(i, IWebShell.REQ_PK);
                break;
            case BTN_TYPE_JS:
                shell.execJScript(bi.openUrl);
                break;
        }
    }
}
