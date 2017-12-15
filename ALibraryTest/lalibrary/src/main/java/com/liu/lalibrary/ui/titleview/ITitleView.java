package com.liu.lalibrary.ui.titleview;

import android.view.View;

/**
 * Created by liu on 2017/12/1.
 */

public interface ITitleView
{
    public interface TitleViewListener
    {
        public void onViewClick(int tvl, int index, View view);
    }

    public View addView(int tvl, int resId, boolean needClick);
    public View addView(int tvl, String imageUrl, boolean needClick);
    public View addView(int tvl, String text, int textSize, int textColor, boolean needClick);
    public View mdImgView(int tvl, int index, int resId);
    public View mdImgView(int tvl, int index, String imageUrl);
    public View mdTxtView(int tvl, int index, String text);
    public void clearView(int tvl);
    public void setTitleViewListener(LTitleView.TitleViewListener listener);
}
