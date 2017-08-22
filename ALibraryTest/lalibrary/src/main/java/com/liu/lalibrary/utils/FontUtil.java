package com.liu.lalibrary.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by liu on 16/6/1.
 */
public class FontUtil
{
    private static Typeface stTf;

    public static void initFont(Context context, String fontPath)
    {
        stTf = Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    public static void setButtonAndTextViewFont(Typeface tf, ViewGroup vg, boolean bSubView)
    {
        for (int i = vg.getChildCount() - 1; i > -1; i--)
        {
            View v = vg.getChildAt(i);
            if (v instanceof TextView)
            {
                ((TextView) v).setTypeface(tf);
            }
            if (bSubView && v instanceof ViewGroup)
            {
                FontUtil.setButtonAndTextViewFont(tf, (ViewGroup) v, bSubView);
            }
        }
    }

    public static void setTextViewFont(TextView tv)
    {
        tv.setTypeface(stTf);
    }

    public static void setButtonAndTextViewFont(ViewGroup vg, boolean bSubView)
    {
        if (stTf == null)return;
        setButtonAndTextViewFont(stTf,vg,bSubView);
    }

    public static void setButtonAndTextViewFont(Context c, String fontPath, ViewGroup vg, boolean bSubView)
    {
        Typeface tf = Typeface.createFromAsset(c.getAssets(), fontPath);
        setButtonAndTextViewFont(tf,vg,bSubView);
    }
}
