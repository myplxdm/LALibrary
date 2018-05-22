package com.liu.app.web;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.pluginImpl.PluginFileUpload;
import com.liu.app.pluginImpl.PluginPhotoChoose;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.FlowLayout;
import com.liu.lalibrary.ui.RLTitleView;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.ui.webview.RLWebViewWrapper;
import com.liu.lalibrary.ui.webview.WebViewEx;
import com.liu.lalibrary.utils.JsonHelper;

import java.util.ArrayList;

/**
 * Created by liu on 2017/12/1.
 */

public abstract class WebShellActivity extends AbsActivity implements IWebShell
{
    protected LTitleView titleView;
    protected RLWebViewWrapper webView;
    protected LinearLayout llFlowWrapper;
    protected FlowLayout flowLayout;
    //
    protected ArrayList<IWebPlugin> webPlugins = new ArrayList<>();
    //
    protected boolean bCloseReload;

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_web_shell;
    }

    @Override
    protected void onInitView()
    {
        titleView = (LTitleView) findViewById(R.id.titleView);
        webView = (RLWebViewWrapper) findViewById(R.id.webView);
        llFlowWrapper = (LinearLayout) findViewById(R.id.llFlowWrapper);
        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        webView.setJsInterface(new JsInterface(this), JsInterface.JS_NAME);
    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginPhotoChoose(this));
        addPlugin(new PluginFileUpload(this));
        bCloseReload = data.getBooleanExtra(IWebShell.WS_CLOSE_RELOAD, false);

        loadWebPlugins(webPlugins);
        activeWebPluginEvent(IWebPlugin.EVENT_INIT, JsonHelper.convert("data",data));
        bCloseReload = data.getBooleanExtra(WS_CLOSE_RELOAD, false);
        webView.loadUrl(data.getStringExtra(WS_URL), true);
    }

    protected void loadWebPlugins(ArrayList<IWebPlugin> list)
    {
        list.add(new WebTitleViewPlugin());
        list.add(new WebSystemPlugin());
        list.add(new WebWindowPlugin());
        list.add(new WebWXPlugin());
        list.add(new WebPhotoPlugin());
    }

    protected abstract int getReturnBtnResId();

    private void activeWebPluginEvent(int event, JSONObject data)
    {
        switch (event)
        {
            case IWebPlugin.EVENT_INIT:
                for (IWebPlugin p : webPlugins)
                {
                    p.init(this, (Intent) data.get("data"));
                }
                break;
            case IWebPlugin.EVENT_DEINIT:
                for (IWebPlugin p : webPlugins)
                {
                    p.deInit();
                }
                break;
            case IWebPlugin.EVENT_RESULT_DATA:
                for (IWebPlugin p : webPlugins)
                {
                    p.onActivityResult((Integer) data.get("requestCode"),
                            (Integer) data.get("resultCode"),
                            (Intent) data.get("data"));
                }
                break;
            case IWebPlugin.EVENT_EXEC:
                for (IWebPlugin p : webPlugins)
                {
                    if (p.exec(data.getString("funName"), (JSONObject) data.get("param"), data.getString("callback"))) break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQ_OPEN_WEB_WINDOW || requestCode == REQ_CODE_EXIT_TO) && data != null)
        {
            activeWebPluginEvent(IWebPlugin.EVENT_RESULT_DATA, JsonHelper.convert("requestCode",requestCode,
                                                                                "resultCode", resultCode,
                                                                                "data",data));
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        activeWebPluginEvent(IWebPlugin.EVENT_DEINIT, null);
    }

//    @Override
//    public void finish()
//    {
//        closeWindow(nCloseLevel, bCloseReload, "");
//    }

    public static void openWindow(AbsActivity activity, Class cls, int returnResId, String url, String title, int titleLoc, boolean bCloseReload)
    {
        Intent i = new Intent(activity, cls);
        i.putExtra(IWebShell.WS_TITLE, title);
        i.putExtra(IWebShell.WS_URL, url);
        i.putExtra(IWebShell.WS_CLOSE_RELOAD, bCloseReload);
        i.putExtra(IWebShell.WS_RETURN_RES_ID, returnResId);
        i.putExtra(IWebShell.WS_TITLE_LOCATION, titleLoc);
        activity.startActivityForResult(i, IWebShell.REQ_OPEN_WEB_WINDOW);
    }

    public static void openWindow(AbsActivity activity, Class cls, int returnResId, String url, String title, boolean bCloseReload)
    {
        Intent i = new Intent(activity, cls);
        i.putExtra(IWebShell.WS_TITLE, title);
        i.putExtra(IWebShell.WS_URL, url);
        i.putExtra(IWebShell.WS_CLOSE_RELOAD, bCloseReload);
        i.putExtra(IWebShell.WS_RETURN_RES_ID, returnResId);
        i.putExtra(IWebShell.WS_TITLE_LOCATION, RLTitleView.TITLE_ALIG_MIDDLE);
        activity.startActivityForResult(i, IWebShell.REQ_OPEN_WEB_WINDOW);
    }

    /********************  IWebShell   ********************/
    @Override
    public void execPlugin(String funName, JSONObject param, String callback)
    {
        activeWebPluginEvent(IWebPlugin.EVENT_EXEC, JsonHelper.convert("funName",funName, "param", param, "callback", callback));
    }

    @Override
    public void openWindow(boolean isShowReturn, String url, String title, int titleLoc, boolean bCloseReload)
    {
        Intent i = new Intent(this, getWebActivityClass());
        i.putExtra(IWebShell.WS_TITLE, title);
        i.putExtra(IWebShell.WS_URL, url);
        i.putExtra(IWebShell.WS_CLOSE_RELOAD, bCloseReload);
        if (isShowReturn)
        {
            i.putExtra(IWebShell.WS_RETURN_RES_ID, getReturnBtnResId());
        }
        startActivityForResult(i, IWebShell.REQ_OPEN_WEB_WINDOW);
    }

    @Override
    public AbsActivity getActivity()
    {
        return this;
    }

    @Override
    public IWebPlugin getWebPluginByName(String name)
    {
        for (IWebPlugin p : webPlugins)
        {
            if (p.getName().equals(name))return p;
        }
        return null;
    }

    @Override
    public View getView(int resId)
    {
        return findViewById(resId);
    }

    @Override
    public void execJScript(String js)
    {
        webView.loadUrl(js, false);
    }

    @Override
    public WebViewEx getWeb()
    {
        return webView.getWeb();
    }

    @Override
    public ITitleView getTitleView()
    {
        return titleView;
    }

    @Override
    public void closeWindow(int closeLevel, boolean bCloseReload, String execJs)
    {
        Intent i = new Intent();
        i.putExtra(WS_CLOSE_RELOAD, bCloseReload);
        i.putExtra(WS_CLOSE_EXEC_JS, execJs);
        AbsActivity.popNum(closeLevel, i);
    }
}
