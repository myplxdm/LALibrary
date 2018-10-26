package com.liu.alibrarytest.keeplive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;

public class SinglePixelActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (LibContants.DEBUG)
        {
            LogUtils.LOGD(getClass(), "open 1px activity");
        }
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 1;
        attrParams.width = 1;
        mWindow.setAttributes(attrParams);

        KeepliveManager.inst().getReceiver().setActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (LibContants.DEBUG)
        {
            LogUtils.LOGD(getClass(), "onDestroy 1px activity");
        }
    }
}
