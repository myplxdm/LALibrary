package com.liu.lalibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustViewPager extends ViewPager
{
	private boolean	isCanScroll	= false;

	public CustViewPager(Context context)
	{
		super(context);
	}

	public CustViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll)
	{
		this.isCanScroll = isCanScroll;
	}

	@Override
	public void scrollTo(int x, int y)
	{
		super.scrollTo(x, y);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent arg0)
	{
		if (isCanScroll)
		{
			return super.onTouchEvent(arg0);
		} else
		{
			return false;
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		if (isCanScroll)
		{
			return super.onInterceptTouchEvent(arg0);
		} else
		{
			return false;
		}
	}
}
