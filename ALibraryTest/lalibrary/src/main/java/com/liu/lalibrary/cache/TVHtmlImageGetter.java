package com.liu.lalibrary.cache;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by liu on 2017/10/20.
 */

public class TVHtmlImageGetter implements Html.ImageGetter
{
    private TextView container;
    private int qualityImage = 0;//1-100, 0 is disable

    public TVHtmlImageGetter(TextView tv, int qualityImage)
    {

    }

    @Override
    public Drawable getDrawable(String source)
    {
        return null;
    }
}
