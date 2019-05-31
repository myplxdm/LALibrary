package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.app.wx.WXSDK;
import com.liu.lalibrary.AbsActivity;
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
                //WXSDK.inst().showShareMenu(this,"","","","",WXSDK.WX_SHARE_TYPE_FIREND);
                break;
            case R.id.btnStop:
                break;
        }
    }
}
