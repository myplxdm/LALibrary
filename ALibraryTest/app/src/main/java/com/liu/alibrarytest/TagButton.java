package com.liu.alibrarytest;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

public class TagButton extends android.support.v7.widget.AppCompatRadioButton
{
    public TagButton(Context context)
    {
        this(context, null, 0);
    }

    public TagButton(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TagButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        int padding = AutoUtils.getPercentHeightSize(30);
        setPadding(padding, padding, padding, padding);
        //setBackgroup
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(0xffff953d));// 状态为true的背景
        stateListDrawable.addState(new int[]{-android.R.attr.state_checked}, new ColorDrawable(0xfff5f7fa));// 状态为false的背景
        setBackground(stateListDrawable);
        //settextcolor
        int[] colors = new int[]{0xffffffff, 0xff303133};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{-android.R.attr.state_checked};
        setTextColor(new ColorStateList(states, colors));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentHeightSize(42));
        setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
    }
}
