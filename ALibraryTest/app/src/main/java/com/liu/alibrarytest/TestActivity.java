package com.liu.alibrarytest;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;

/**
 * Created by liu on 2017/11/24.
 */

public class TestActivity extends AbsActivity
{
    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_test;
    }

    @Override
    protected void onInitView()
    {
        findViewById(R.id.tvTest).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getPluginByName(PluginContacts.NAME).exec(PluginContacts.CMD_OPEN_CONTACTS_VIEW, null, new IPluginEvent()
                {
                    @Override
                    public void pluginResult(boolean isSuccess, String result, Object param)
                    {
                        Toast.makeText(TestActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void pluginClose(boolean isSuccess, String result)
                    {

                    }
                });
            }
        });
    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginContacts(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }
}
