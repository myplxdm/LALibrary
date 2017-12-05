package com.liu.lalibrary.ui.titleview;

/**
 * Created by liu on 2017/12/1.
 */

public interface ITitleView
{
    public void addView(final int tvl, int resId, boolean needClick);
    public void addView(final int tvl, String imageUrl, boolean needClick);
    public void addView(final int tvl, String text, int textSize, int textColor, boolean needClick);
}
