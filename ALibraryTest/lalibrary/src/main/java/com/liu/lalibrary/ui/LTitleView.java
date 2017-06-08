package com.liu.lalibrary.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.liu.lalibrary.R;

/**
 * Created by liu on 2017/4/20.
 */

public class LTitleView extends RelativeLayout
{
    private final int BG_COLOR = 0xffffff;
    private final int TITLE_TEXT_COLOR = 0xff000000;
    //
    private LinearLayout llLeft;
    private LinearLayout llMiddle;
    private LinearLayout llRight;
    //
    //private

    public LTitleView(Context context)
    {
        this(context, null, 0);
    }

    public LTitleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LTitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.title_view, this, true);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LTitleView);
        if (attr != null)
        {
            int bgColor = attr.getColor(R.styleable.LTitleView_bgColor, BG_COLOR);
           // int titleTextColor =

        }

    }
}
