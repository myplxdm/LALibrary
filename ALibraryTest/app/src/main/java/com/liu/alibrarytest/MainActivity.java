package com.liu.alibrarytest;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Button;

import com.liu.app.pluginImpl.PluginContacts;
import com.liu.app.ui.TBButton;
import com.liu.lalibrary.AbsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ryt.mtzf.R;

public class MainActivity extends AbsActivity
{

    @BindView(R.id.add)
    Button add;
    @BindView(R.id.tb)
    TBButton tb;

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

//        fl.setFlowItemListener(new FlowLayout.FlowItemListener()
//        {
//            @Override
//            public void onFlowSelItem(View child, boolean isSel, int index)
//            {
//                ((RadioButton) child).setChecked(isSel);
//                if (!isSel)
//                {
////                    fl.removeView(child);
//                }
//                //String ss = String.format("%s %d", ((RadioButton) child).getText().toString(), isSel ? 1 : 0);
//                //Toast.makeText(MainActivity.this, ss, Toast.LENGTH_LONG).show();
//            }
//        });
//        LayoutInflater li = LayoutInflater.from(this);
//        TagButton tb;
//        for (int i = 0;i < 3;i++)
//        {
//            tb = new TagButton(this);
//            tb.setText("test " + i);
//            fl.addView(tb, true);
//        }

    }

    @Override
    protected void onInitData(Intent data)
    {
        addPlugin(new PluginContacts(this));
    }

    @OnClick(R.id.add)
    public void onViewClicked()
    {
    }

}
