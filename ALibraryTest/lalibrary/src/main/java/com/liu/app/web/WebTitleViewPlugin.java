package com.liu.app.web;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.utils.JsonHelper;

import java.util.HashMap;

/**
 * Created by liu on 2017/12/4.
 */

public class WebTitleViewPlugin extends WebPluginBase implements ITitleView.TitleViewListener
{
    public static final String NAME = WebTitleViewPlugin.class.getSimpleName();

    private final int BTN_TYPE_NORMAL = 0;
    private final int BTN_TYPE_PK = 1;//不使用
    private final int BTN_TYPE_JS = 2;
    private final int BTN_TYPE_UI = 3;
    private final int TITLE_MAX_LEN = 12;

    class BtnInfo
    {
        public String openUrl;
        public String title;
        public boolean closeReload;
        public int btnType;
        public Object data;//用于ui操作或其它功能
    }

    private final String P_URL = "url";
    private final String P_TITLE = "title";
    private final String P_CLOSE_RELOAD = "closeReload";
    private final String P_BTN_TYPE = "btnType";
    private final String P_INDEX = "index";
    private final String P_TITLE_LOCATION = "titleLoc";
    private final String P_ALIAS = "alias";
    //--------------------------------------------
    private final String TV_ADD_IMG_BTN = "addimgbtn";
    private final String P_IB_IMG_URL = "imgurl";
    private final String P_IB_RES_ID = "resid";
    //--------------------------------------------
    private final String TV_ADD_TXT_BTN = "addtxtbtn";
    private final String P_TB_TEXT = "text";
    private final String P_TB_TEXT_COLOR = "tcolor";
    private final String P_TB_TEXT_SIZE = "tsize";
    //--------------------------------------------
    private final String TV_MD_IMG_BTN_ICO = "mdimgbtnico";
    private final String TV_MD_TXT_BTN_TITLE = "mdtxtbtntitle";
    private final String TV_CLEAR_ALL_BTN = "clsallbtn";
    private final String TV_LEFT_IMG_BTN = "addleftimgbtn";
    private final String TV_SHOW_RETURN = "isShowReturn";
    private final String P_IS_SHOW = "isShow";
    //--------------------------------------------
    public static final String TV_ADD_TAG = "addtag";
    private final String P_TAP_DATA = "tapdata";
    //
    private final String TV_SET_TITLE = "settitle";
    private final String TV_SET_RETURN = "setreturn";
    //
    private final String TV_SHOW_BADGE = "showBadge";
    private final String P_BADGE_COUNT = "badgeCount";//数字小于0时显示dot，等于0时隐藏整个Badge，在普通模式下超过99时显示99+，精确模式下显示具体值
    //
    private int returnBtnIndex = -1;
    private int titleLocation = LTitleView.TVL_MIDDLE;
    private int titleIndex = -1;

    private HashMap<View, BtnInfo> mapBtnInfo = new HashMap<>();


    @Override
    public void init(IWebShell ws, Intent data)
    {
        super.init(ws, data);
        ITitleView tv = ws.getTitleView();
        tv.setTitleViewListener(this);
        //
        String title = data.getStringExtra(IWebShell.WS_TITLE);
        int resId = data.getIntExtra(IWebShell.WS_RETURN_RES_ID, -1);
        if (resId != -1)
        {
            exec(TV_SET_RETURN, JsonHelper.convert(P_IB_RES_ID, resId), null);
        }
        //
        titleLocation = data.getIntExtra(IWebShell.WS_TITLE_LOCATION, LTitleView.TVL_MIDDLE);
        exec(TV_SET_TITLE, JsonHelper.convert(P_TB_TEXT, title, P_TITLE_LOCATION, titleLocation), null);
    }

    @Override
    public boolean exec(String funName, JSONObject param, String callback)
    {
        View view;
        IWebShell shell = webShell.get();
        if (shell == null) return false;
        ITitleView tv = shell.getTitleView();
        boolean isProc = false;
        int index;
        if (funName.equals(TV_ADD_IMG_BTN))
        {
            index = tv.addImageView(LTitleView.TVL_RIGHT, param.getString(P_IB_IMG_URL), true);
            genBtnInfo(param, tv.getView(LTitleView.TVL_RIGHT, index));
            isProc = true;
        } else if (funName.equals(TV_ADD_TXT_BTN))
        {
            index = tv.addTextView(LTitleView.TVL_RIGHT, param.getString(P_TB_TEXT),
                    JsonHelper.getInt(param, P_TB_TEXT_SIZE, tv.getContext().getResources().getDimensionPixelOffset(R.dimen.title_view_btn_size)),
                    JsonHelper.getInt(param, P_TB_TEXT_COLOR, 0xff000000), true);
            genBtnInfo(param, tv.getView(LTitleView.TVL_RIGHT, index));
            isProc = true;
        } else if (funName.equals(TV_MD_IMG_BTN_ICO))
        {
            tv.mdImgView(LTitleView.TVL_RIGHT, param.getIntValue(P_INDEX), param.getString(P_IB_IMG_URL));
            isProc = true;
        } else if (funName.equals(TV_MD_TXT_BTN_TITLE))
        {
            tv.mdTxtView(LTitleView.TVL_RIGHT, param.getIntValue(P_INDEX), param.getString(P_TB_TEXT));
            isProc = true;
        } else if (funName.equals(TV_CLEAR_ALL_BTN))
        {
            tv.clearView(LTitleView.TVL_RIGHT);
            isProc = true;
        } else if (funName.equals(TV_LEFT_IMG_BTN))
        {
            index = tv.addImageView(LTitleView.TVL_LEFT, param.getString(P_IB_IMG_URL), true);
            genBtnInfo(param, tv.getView(LTitleView.TVL_LEFT, index));
            isProc = true;
        } else if (funName.equals(TV_SET_TITLE))
        {
            int tl = JsonHelper.getInt(param, P_TITLE_LOCATION, LTitleView.TVL_MIDDLE);
            String txt = JsonHelper.getString(param, P_TB_TEXT, "");
            int ts = JsonHelper.getInt(param, P_TB_TEXT_SIZE, tv.getContext().getResources().getDimensionPixelOffset(R.dimen.title_view_title_text_size));
            int color = JsonHelper.getInt(param, P_TB_TEXT_COLOR, ContextCompat.getColor(shell.getActivity(), R.color.colorTitleViewTitle));
            if (txt.length() > TITLE_MAX_LEN)
            {
                txt = txt.substring(0, TITLE_MAX_LEN) + "...";
            }
            if (titleIndex == -1)
            {
                titleIndex = tv.addTextView(tl, txt, ts, color, true);
                titleLocation = tl;
            }else
            {
                tv.mdTxtView(titleLocation, titleIndex, txt);
            }
            genBtnInfo(param, tv.getView(LTitleView.TVL_MIDDLE, 0));
            isProc = true;
        } else if (funName.equals(TV_SET_RETURN))
        {
            returnBtnIndex = tv.addImageView(LTitleView.TVL_LEFT, param.getIntValue(P_IB_RES_ID), true);
            isProc = true;
        } else if (funName.equals(TV_SHOW_RETURN))
        {
            tv.showView(LTitleView.TVL_LEFT, returnBtnIndex, param.getBooleanValue(P_IS_SHOW));
            isProc = true;
        } else if (funName.equals(TV_ADD_TAG))
        {
            index = tv.addImageView(LTitleView.TVL_MIDDLE, param.getString(P_IB_IMG_URL), true);
            BtnInfo bi = new BtnInfo();
            bi.btnType = BTN_TYPE_UI;
            bi.data = funName;
            mapBtnInfo.put(tv.getView(ITitleView.TVL_MIDDLE, index), bi);
            shell.jsCall(funName, JsonHelper.getString(param, P_TAP_DATA, ""));
            isProc = true;
        } else if (funName.equals(TV_SHOW_BADGE))
        {
            tv.showViewBadge(LTitleView.TVL_RIGHT, param.getIntValue(P_INDEX), param.getIntValue(P_BADGE_COUNT));
            isProc = true;
        }
        isProc = isProc || !(execOther(funName, param, callback) == IWebPlugin.EXEC_OTHER_NO_PROC);
        if (!isProc)return false;
        procCallback(true, callback, param, null);
        return true;
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
        bi.btnType = JsonHelper.getInt(param, P_BTN_TYPE, BTN_TYPE_NORMAL);
        bi.closeReload = JsonHelper.getBoolen(param, P_CLOSE_RELOAD, false);
        bi.openUrl = JsonHelper.getString(param, P_URL, "");
        bi.title = JsonHelper.getString(param, P_TITLE, "");
        bi.data = JsonHelper.getString(param, P_ALIAS, "");
        mapBtnInfo.put(view, bi);
    }

    /*********************** TitleViewListener ************************/
    @Override
    public void onViewClick(int tvl, int index, View view)
    {
        IWebShell shell = webShell.get();
        if (shell == null) return;
        switch (tvl)
        {
            case LTitleView.TVL_LEFT:
                if (index == returnBtnIndex)
                {
                    shell.getActivity().finish();
                }else
                {
                    clickBtnInfo(shell, mapBtnInfo.get(view));
                }
                break;
            case LTitleView.TVL_MIDDLE:
            case LTitleView.TVL_RIGHT:
                clickBtnInfo(shell, mapBtnInfo.get(view));
                break;
        }
    }

    private void clickBtnInfo(IWebShell shell, BtnInfo bi)
    {
        switch (bi.btnType)
        {
            case BTN_TYPE_NORMAL:
                shell.openWindow(true, bi.openUrl, bi.title, LTitleView.TVL_RIGHT);
                break;
            case BTN_TYPE_PK:
                Intent i = new Intent(Intent.ACTION_VIEW);
                ComponentName com = new ComponentName(bi.title, bi.openUrl);
                i.setComponent(com);
                shell.getActivity().startActivityForResult(i, IWebShell.REQ_PK);
                break;
            case BTN_TYPE_JS:
                shell.execJScript("javascript:" + "event_callback(#)".replaceAll("#", JsonHelper.convertToStr("method", (String) bi.data)));
                break;
            case BTN_TYPE_UI:
                shell.pluginCallback((String) bi.data, null);
                break;
        }
    }
}
