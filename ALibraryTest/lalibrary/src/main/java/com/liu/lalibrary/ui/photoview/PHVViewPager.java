package com.liu.lalibrary.ui.photoview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liu on 2017/4/18.
 */

public class PHVViewPager extends ViewPager
{
    public PHVViewPager(Context context)
    {
        super(context);
    }

    public PHVViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onTouchEvent(ev);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
