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

    public void addView(final int tvl, int resId, boolean needClick);
    public void addView(final int tvl, String imageUrl, boolean needClick);
    public void addView(final int tvl, String text, int textSize, int textColor, boolean needClick);
    public void setTitleViewListener(LTitleView.TitleViewListener listener);
}
