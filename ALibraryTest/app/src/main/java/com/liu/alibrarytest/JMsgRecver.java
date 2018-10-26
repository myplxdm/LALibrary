package com.liu.alibrarytest;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;

import java.util.ArrayList;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by liu on 2018/10/24.
 */

public class JMsgRecver implements MediaPlayer.OnCompletionListener
{
    private Context mContext;
    private MediaPlayer player;
    private ArrayList<String> audioList = new ArrayList<>(5);
    private Handler handler;


    public JMsgRecver(Context context)
    {
        mContext = context;
        player = new MediaPlayer();
        player.setOnCompletionListener(this);

        handler = new Handler(Looper.getMainLooper());
    }

    public void onEvent(MessageEvent event)
    {
        Message msg = event.getMessage();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(mContext, "消息", Toast.LENGTH_LONG).show();
            }
        });
        switch (msg.getContentType())
        {
            case text:
                //处理文字消息
                TextContent tc = (TextContent) msg.getContent();
                playNext(tc.getText());
                break;
        }
    }

    private synchronized void playNext(String url)
    {
        if (url != null) audioList.add(url);
        if (audioList.size() == 0 || player.isPlaying())return;
        try
        {
            String f = audioList.get(0);
            audioList.remove(0);
            //
            player.reset();
            player.setDataSource(f);
            player.prepare();
            player.setOnCompletionListener(this);
            player.start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        if (player != null)
        {
            player.reset();
            player.release();
            player = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        playNext(null);
    }
}
