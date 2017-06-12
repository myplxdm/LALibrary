package com.liu.lalibrary.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.liu.lalibrary.camera.background.BgCanvaLayout;
import com.liu.lalibrary.ui.touchview.TouchPlate;

/**
 * Created by liu on 2016/12/29.
 */

public class CameraContentFrameLayout extends FrameLayout
{
    private int cur_view_index;
    private boolean isInit;

    public CameraContentFrameLayout(Context context)
    {
        this(context, null, 0);
    }

    public CameraContentFrameLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CameraContentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0xffdcdcdc);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev)
//    {
//        ViewGroup v = (ViewGroup)getChildAt(cur_view_index);
//        v.onInterceptTouchEvent(ev);
//        return true;
//    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
//    {
//        for (int i = 0;i < getChildCount();i++)
//        {
//            getChildAt(i).layout(left, top, right, bottom);
//        }
//    }

    /*******************************/
    private void procClick()
    {
        if (cur_view_index == 0)
        {
            ((BgCanvaLayout)getChildAt(0)).setEditable(true);
            ((TouchPlate)getChildAt(1)).setEditable(false);
        }else
        {
            ((BgCanvaLayout)getChildAt(0)).setEditable(false);
            ((TouchPlate)getChildAt(1)).setEditable(true);
        }
    }

    public void incViewIndex()
    {
        cur_view_index = (cur_view_index + 1) % getChildCount();
        procClick();
    }

    public void decViewIndex()
    {
        cur_view_index = (cur_view_index - 1) % getChildCount();
        procClick();
    }
}
