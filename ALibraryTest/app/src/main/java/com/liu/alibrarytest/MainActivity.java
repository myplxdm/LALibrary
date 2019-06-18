package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liu.app.data.XDataStore;
import com.liu.app.pluginImpl.PluginContacts;
import com.liu.app.web.WebShellActivity;
import com.liu.app.wx.WXSDK;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.RLTitleView;
import com.tencent.mm.opensdk.constants.ConstantsAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ryt.mtzf.R;

public class MainActivity extends AbsActivity
{
    @BindView(R.id.btnOpen)
    Button btnOpen;
    @BindView(R.id.btnStop)
    Button btnStop;

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onInitView()
    {
        ButterKnife.bind(this);
        mToBack = true;
        XDataStore.inst().init(this, "test");
        TestObj t = new TestObj();
        t.access_token = "111";
        t.expires_in = 20;
        t.refresh_token = "qqq";
        t.scope = "qwe";
        XDataStore.inst().saveObject(t);
        TestObj s = XDataStore.inst().getObject(TestObj.class);
        System.out.println(s.access_token);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginContacts(this));
    }

    @OnClick({R.id.btnOpen, R.id.btnStop})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnOpen:
                WebShellImpl.openWindow(this, WebShellImpl.class,
                        R.mipmap.i_exit, "http://192.168.1.100:1024/#/11.html",
                        "", RLTitleView.TITLE_ALIG_MIDDLE, false);
                break;
            case R.id.btnStop:
                break;
        }
    }
}
