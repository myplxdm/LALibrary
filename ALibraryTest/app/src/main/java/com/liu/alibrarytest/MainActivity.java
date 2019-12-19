package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.ui.FlowLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ryt.mtzf.R;

public class MainActivity extends AbsActivity
{

    @BindView(R.id.add)
    Button add;
    @BindView(R.id.fl)
    FlowLayout fl;

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
        LayoutInflater li = LayoutInflater.from(this);
        RadioButton rb = null;
        fl.setMultilSel(true);
        for (int i = 0; i < 3; i++)
        {
            View.inflate(this, R.layout.tag_item, fl); //li.inflate(R.layout.tag_item, null, false);
            rb = (RadioButton) fl.getChildAt(i);
            rb.setText("test" + i);
        }
        fl.setFlowItemListener(new FlowLayout.FlowItemListener()
        {
            @Override
            public void onFlowSelItem(View child, boolean isSel, int index)
            {
                ((RadioButton) child).setChecked(isSel);
                //String ss = String.format("%s %d", ((RadioButton) child).getText().toString(), isSel ? 1 : 0);
                //Toast.makeText(MainActivity.this, ss, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginContacts(this));
    }

    @OnClick(R.id.add)
    public void onViewClicked()
    {
        ArrayList list = fl.getSelectList();
        toast("size = " + (list == null ? 0 : list.size()));
    }
}
