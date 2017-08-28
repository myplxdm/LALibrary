package com.liu.alibrarytest;

import android.view.View;
import android.widget.Toast;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.utils.NetConnManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liu on 2017/8/28.
 */

public class TestActivity extends AbsActivity implements NetConnManager.INetworkLisener
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
                finish();
            }
        });
    }

    @Override
    protected void onInitData()
    {
        NetConnManager.inst().addListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RefWatcher refWatcher = TestApplication.getRefWatcher(this);
        refWatcher.watch(this);

        //NetConnManager.inst().removeListener(this);
    }

    @Override
    public void onNetworkChange(boolean isConn)
    {
        Toast.makeText(TestActivity.this, "", Toast.LENGTH_LONG).show();
    }
}
