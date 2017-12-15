package com.liu.app.web;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.FlowLayout;
import com.liu.lalibrary.ui.titleview.ITitleView;
import com.liu.lalibrary.ui.titleview.LTitleView;
import com.liu.lalibrary.ui.webview.RLWebViewWrapper;
import com.liu.lalibrary.utils.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by liu on 2017/12/1.
 */

public abstract class WebShellActivity extends AbsActivity implements IWebShell
{
    private LTitleView titleView;
    private RLWebViewWrapper webView;
    private LinearLayout llFlowWrapper;
    private FlowLayout flowLayout;
    //
    private ArrayList<IWebPlugin> webPlugins = new ArrayList<>();
    private boolean bCloseReload;

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
    }

    @Override
    protected void onInitData(Intent data)
    {
        loadWebPlugins(webPlugins);
        activeWebPluginEvent(IWebPlugin.EVENT_INIT, JsonHelper.convert("data",data));
        bCloseReload = data.getBooleanExtra(WC_CLOSE_RELOAD, false);
        webView.loadUrl(data.getStringExtra(WC_URL), true);
    }

    protected void loadWebPlugins(ArrayList<IWebPlugin> list)
    {
        WebTitleViewPlugin tvPlugin = new WebTitleViewPlugin();
        list.add(tvPlugin);
    }

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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OPEN_WEB_WINDOW && resultCode == RESULT_OK)
        {
            activeWebPluginEvent(IWebPlugin.EVENT_RESULT_DATA, JsonHelper.convert("requestCode",requestCode,
                                                                                "resultCode", resultCode,
                                                                                "data",data));
//            if (data.getBooleanExtra(WC_CLOSE_RELOAD, false))
//            {
//                finish();
//            }
//            if (data.getBooleanExtra(WC_CLOSE_RELOAD, false))
//            {
//                web.getWeb().reload();
//            }
//            String js = data.getStringExtra(RUN_JS);
//            if (!TextUtils.isEmpty(js))
//            {
//                web.loadUrl(js, false);
//            }
        }
    }

    /********************  IWebShell   ********************/
    @Override
    public AbsActivity getActvity()
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
    public ITitleView getTitleView()
    {
        return titleView;
    }

    @Override
    public void closeWindow(boolean closeParentClose, String execJs)
    {
        Intent i = new Intent();
        i.putExtra(WC_CLOSE_PARENT_CLOSE, closeParentClose);
        i.putExtra(WC_CLOSE_RELOAD, bCloseReload);
        i.putExtra(WC_CLOSE_EXEC_JS, execJs);
        setResult(RESULT_OK, i);
        finish();
    }
}
