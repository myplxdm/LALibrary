package com.liu.lalibrary.ui.titleview;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

/**
 * Created by liu on 2017/12/1.
 */

public interface ITitleView
{
    public static final int TVL_LEFT = 0;
    public static final int TVL_MIDDLE = 1;
    public static final int TVL_RIGHT = 2;

    public interface TitleViewListener
    {
        public void onViewClick(int tvl, int index, View view);
    }

    public int addImageView(int tvl, int resId, boolean needClick);
    public int addImageView(int tvl, String imageUrl, boolean needClick);
    public int addTextView(int tvl, String text, int textSize, int textColor, boolean needClick);
    //
    public View getView(int tvl, int index);
    public View mdImgView(int tvl, int index, int resId);
    public View mdImgView(int tvl, int index, String imageUrl);
    public View mdTxtView(int tvl, int index, String text);
    public void showView(int tvl, int index, boolean isShow);
    public void clearView(int tvl);
    public void setTitleViewListener(LTitleView.TitleViewListener listener);
    public void setBottomLine(int color, int height);
    public Context getContext();
}
