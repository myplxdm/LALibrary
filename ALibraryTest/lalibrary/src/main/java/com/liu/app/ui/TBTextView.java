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
import com.liu.lalibrary.ui.NoPaddingTextView;
import com.liu.lalibrary.utils.Utils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by liu on 2018/8/23.
 */

public class TBTextView extends AutoLinearLayout
{
    private NoPaddingTextView tvTop;
    private NoPaddingTextView tvBottom;
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
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TBTextView);
        LayoutInflater.from(context).inflate(R.layout.tb_text_view, this, true);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        tvTop = findViewById(R.id.tvTop);
        tvBottom = findViewById(R.id.tvBottom);
        //

        tvTop.setTextColor(array.getColor(R.styleable.TBTextView_topTextColor,0xff000000));
        tvTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getInteger(R.styleable.TBTextView_topTextSize,DEF_TEXT_SIZE));
        tvBottom.setTextColor(array.getColor(R.styleable.TBTextView_bottomTextColor,0xff000000));
        tvBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getInteger(R.styleable.TBTextView_bottomTextSize,DEF_TEXT_SIZE));
        tvTop.setText(Utils.safeStr(array.getString(R.styleable.TBTextView_topText)));
        tvBottom.setText(Utils.safeStr(array.getString(R.styleable.TBTextView_bottomText)));
        ((LinearLayout.LayoutParams)tvBottom.getLayoutParams()).setMargins(0,array.getInteger(R.styleable.TBTextView_topBottomSpace,DEF_TB_SPACE),0,0);
        //
        array.recycle();

        AutoUtils.auto(tvTop);
        AutoUtils.auto(tvBottom);
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
