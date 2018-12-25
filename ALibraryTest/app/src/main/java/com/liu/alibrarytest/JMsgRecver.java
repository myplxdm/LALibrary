package com.liu.alibrarytest;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.liu.app.LibContants;
import com.liu.lalibrary.log.LogUtils;

import java.util.ArrayList;
import java.util.Locale;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by liu on 2018/10/24.
 */

public class JMsgRecver implements /*MediaPlayer.OnCompletionListener,*/
        TextToSpeech.OnInitListener
{
    private Context mContext;
    //private MediaPlayer player;
    private TextToSpeech textSpeech;
    private ArrayList<String> audioList = new ArrayList<>(5);
    private Handler handler;


    public JMsgRecver(Context context)
    {
        mContext = context;
        //
//        player = new MediaPlayer();
//        player.setOnCompletionListener(this);
        //
        textSpeech = new TextToSpeech(context, this);
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
//        if (audioList.size() == 0 || player.isPlaying())return;
        if (audioList.size() == 0 || textSpeech.isSpeaking())return;
        try
        {
            String f = audioList.get(0);
            audioList.remove(0);
            if (Build.VERSION.SDK_INT < 21)
            {
                textSpeech.speak(f, TextToSpeech.QUEUE_FLUSH, null);
            }else
            {
                textSpeech.speak(f, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(System.currentTimeMillis()));
            }

            //
//            player.reset();
//            player.setDataSource(f);
//            player.prepare();
//            player.setOnCompletionListener(this);
//            player.start();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
//        if (player != null)
//        {
//            player.reset();
//            player.release();
//            player = null;
//        }
        textSpeech.stop();
        textSpeech.shutdown();
    }

//    @Override
//    public void onCompletion(MediaPlayer mp)
//    {
//        playNext(null);
//    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            int res = textSpeech.setLanguage(Locale.CHINA);
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                textSpeech.setLanguage(Locale.US);
            }
            textSpeech.setPitch(0.5f);
        }
    }
}
