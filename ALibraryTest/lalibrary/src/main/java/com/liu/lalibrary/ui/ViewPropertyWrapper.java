package com.liu.lalibrary.ui;//package org.liu.library.ui;
//
//import android.view.View;
//
//import com.zhy.autolayout.AutoLinearLayout;
//import com.zhy.autolayout.attr.Attrs;
//
///**
// * Created by liu on 16/5/9.
// */
//public class ViewPropertyWrapper
//{
//    private View mTarget;
//
//    public ViewPropertyWrapper(View target)
//    {
//        mTarget = target;
//    }
//
//    public int getWidth()
//    {
//        return mTarget.getLayoutParams().width;
//    }
//
//    public int getHeight()
//    {
//        return mTarget.getLayoutParams().height;
//    }
//
//    public void setWidth(int width)
//    {
//        AutoLinearLayout.LayoutParams lp = (AutoLinearLayout.LayoutParams)mTarget.getLayoutParams();
//        lp.getAutoLayoutInfo().setAttr(Attrs.WIDTH, width);
//        mTarget.setLayoutParams(lp);
//    }
//
//    public void setHeight(int height)
//    {
//        AutoLinearLayout.LayoutParams lp = (AutoLinearLayout.LayoutParams)mTarget.getLayoutParams();
//        lp.getAutoLayoutInfo().setAttr(Attrs.HEIGHT, height);
//        mTarget.setLayoutParams(lp);
//    }
//}
