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
import com.liu.lalibrary.ui.NoPaddingTextView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.attr.AutoAttr;
import com.zhy.autolayout.utils.AutoUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liu on 2018/4/10.
 */

public class TBButton extends AutoLinearLayout
{
    protected ImageView ivPhoto;
    protected NoPaddingTextView tvName;
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
        boolean photoCircular = array.getBoolean(R.styleable.TBButton_photocircular, false);
        int dir = array.getInt(R.styleable.TBButton_dir, 0);
        array.recycle();
        /////////////////////
        setOrientation(dir < 2 ? VERTICAL : HORIZONTAL);
        setGravity(Gravity.CENTER);
        //////// init photo
        if (photoCircular)
        {
            ivPhoto = new CircleImageView(context);
        } else
        {
            ivPhoto = new ImageView(context);
        }
        if (photoSrcRes != -1)
        {
            ivPhoto.setImageResource(photoSrcRes);
        }
        LayoutParams lpPhoto, lpText;
        if (pw != 0 && ph != 0)
        {
            lpPhoto = new LayoutParams(pw, ph);
        } else
        {
            lpPhoto = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ////// init textview
        tvName = new NoPaddingTextView(context);
        lpText = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
        tvName.setTextColor(textColor);
        tvName.setText(text);
        /////////
        switch (dir)
        {
            case 0://top image
                 lpPhoto.setMargins(0, 0, 0, space);
                 ivPhoto.setLayoutParams(lpPhoto);
                 addView(ivPhoto);
                 tvName.setLayoutParams(lpText);
                 addView(tvName);
                break;
            case 1://bottom image
                lpText.setMargins(0, 0, 0, space);
                tvName.setLayoutParams(lpText);
                addView(tvName);
                ivPhoto.setLayoutParams(lpPhoto);
                addView(ivPhoto);
                break;
            case 2://left image
                lpPhoto.setMargins(0, 0, space, 0);
                ivPhoto.setLayoutParams(lpPhoto);
                addView(ivPhoto);
                tvName.setLayoutParams(lpText);
                addView(tvName);
                break;
            case 3://right image
                lpText.setMargins(0, 0, space, 0);
                tvName.setLayoutParams(lpText);
                addView(tvName);
                ivPhoto.setLayoutParams(lpPhoto);
                addView(ivPhoto);
                break;
        }
        AutoUtils.autoSize(ivPhoto, AutoAttr.BASE_HEIGHT);
        AutoUtils.auto(tvName);
        setSelect(sel);
//        setOrientation(VERTICAL);
//        setGravity(Gravity.CENTER);
//        if (useCir)
//        {
//            ivPhoto = new CircleImageView(context);
//        }else
//        {
//            ivPhoto = new ImageView(context);
//        }
//        if (photoSrcRes != -1)
//        {
//            ivPhoto.setImageResource(photoSrcRes);
//        }
//        if (pw != 0 && ph != 0)
//        {
//            LayoutParams lp = new LayoutParams(pw, ph);
//            ivPhoto.setLayoutParams(lp);
//            addView(ivPhoto);
//        }else
//        {
//            addView(ivPhoto, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        }
//        ////////////////////////////////
//        tvName = new NoPaddingTextView(context);
//        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, space, 0, 0);
//        tvName.setLayoutParams(lp);
//        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
//        tvName.setTextColor(textColor);
//        tvName.setText(text);
//        addView(tvName);
//        //
//        AutoUtils.autoSize(ivPhoto, AutoAttr.BASE_HEIGHT);
//        AutoUtils.auto(tvName);
//        setSelect(sel);
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
