package com.liu.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liu.lalibrary.R;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.attr.AutoAttr;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by liu on 2018/4/10.
 */

public class TBButton extends AutoLinearLayout
{
    protected ImageView ivPhoto;
    protected TextView tvName;
    private int photoSrcRes;
    private int photoSelSrcRes;
    private int textColor;
    private int textSelColor;
    private boolean isSel;

    public TBButton(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TBButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TBButton);
        int pw = array.getInteger(R.styleable.TBButton_photoWidth,0);
        int ph =  array.getInteger(R.styleable.TBButton_photoHeight,0);
        textColor = array.getColor(R.styleable.TBButton_tbTextColor,0);
        textSelColor = array.getColor(R.styleable.TBButton_tbTextSelColor,textColor);
        int ts = array.getInteger(R.styleable.TBButton_tbTextSize,0);
        int space = array.getInteger(R.styleable.TBButton_tbSpace,0);
        boolean sel = array.getBoolean(R.styleable.TBButton_select,false);
        String text = array.getString(R.styleable.TBButton_tbText);
        photoSrcRes = array.getResourceId(R.styleable.TBButton_photoscr,-1);
        photoSelSrcRes = array.getResourceId(R.styleable.TBButton_photoselscr,-1);
        array.recycle();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        ivPhoto = new ImageView(context);
        if (photoSrcRes != -1)
        {
            ivPhoto.setBackgroundResource(photoSrcRes);
        }
        if (pw != 0 && ph != 0)
        {
            LayoutParams lp = new LayoutParams(pw, ph);
            ivPhoto.setLayoutParams(lp);
            addView(ivPhoto);
        }else
        {
            addView(ivPhoto, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        ////////////////////////////////
        tvName = new TextView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, space, 0, 0);
        tvName.setLayoutParams(lp);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
        tvName.setTextColor(textColor);
        tvName.setText(text);
        addView(tvName);
        //
        AutoUtils.autoSize(ivPhoto, AutoAttr.BASE_HEIGHT);
        AutoUtils.auto(tvName);
        setSelect(sel);
    }

    public ImageView getImageView()
    {
        return ivPhoto;
    }

    public TextView getTextView()
    {
        return tvName;
    }

    public void setSelect(boolean isSel)
    {
        if (this.isSel != isSel)
        {
            this.isSel = isSel;
            ivPhoto.setImageResource(isSel ? photoSelSrcRes : photoSrcRes);
            tvName.setTextColor(isSel ? textSelColor : textColor);
        }
    }

}
