package com.liu.alibrarytest;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;

import com.liu.alibrarytest.keeplive.KeepliveManager;
import com.liu.app.pluginImpl.PluginContacts;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        KeepliveManager.inst().startKepplive(this);
        mToBack = true;

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        KeepliveManager.inst().stopKeeplive(this);
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
                getPluginByName(PluginContacts.NAME).exec(PluginContacts.CMD_OPEN_CONTACTS_VIEW, null, new IPluginEvent()
                {
                    @Override
                    public void pluginResult(boolean isSuccess, String result, Object param)
                    {
                        if (isSuccess)
                        {
                            System.out.print(result);
                        }
                    }

                    @Override
                    public void pluginClose(boolean isSuccess, String result)
                    {

                    }
                });
                break;
            case R.id.btnStop:
                break;
        }
    }
}
