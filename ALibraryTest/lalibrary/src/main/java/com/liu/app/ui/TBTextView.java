package com.liu.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liu.lalibrary.R;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by liu on 2018/8/23.
 */

public class TBTextView extends LinearLayout
{
    private TextView tvTop;
    private TextView tvBottom;
    private final int DEF_TEXT_SIZE = 20;
    private final int DEF_TB_SPACE = 10;

    public TBTextView(Context context)
    {
        this(context, null, 0);
    }

    public TBTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TBTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.tb_text_view, this, true);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvBottom = (TextView) findViewById(R.id.tvBottom);
        //
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TBTextView);
        tvTop.setTextColor(array.getColor(R.styleable.TBTextView_topTextColor,0xff000000));
        tvTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getInteger(R.styleable.TBTextView_topTextSize,DEF_TEXT_SIZE));
        tvBottom.setTextColor(array.getColor(R.styleable.TBTextView_bottomTextColor,0xff000000));
        tvBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getInteger(R.styleable.TBTextView_bottomTextSize,DEF_TEXT_SIZE));
        ((LinearLayout.LayoutParams)tvBottom.getLayoutParams()).setMargins(0,array.getInteger(R.styleable.TBTextView_topBottomSpace,DEF_TB_SPACE),0,0);
        //
        array.recycle();
        AutoUtils.auto(this);
    }

    public TextView getTopTextView()
    {
        return tvTop;
    }

    public TextView getBottomTextView()
    {
        return tvBottom;
    }

    public void setText(String top, String bottom)
    {
        tvTop.setText(top);
        tvBottom.setText(bottom);
    }
}
