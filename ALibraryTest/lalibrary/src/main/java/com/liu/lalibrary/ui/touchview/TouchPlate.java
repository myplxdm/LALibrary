package com.liu.lalibrary.ui.touchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by liu on 16/6/29.
 */
public class TouchPlate extends FrameLayout implements TouchTextView.TouchTextViewDoubleTapListener
{
    public interface TouchPlateListener
    {
        public void onShowTextInput();
    }

    private ArrayList<Object> _overlapViewList;
    private int _curOverlapIndex;
    private float _downX,_downY;
    private boolean _isSwitch;
    private TouchView _lastView;
    private boolean _isEditable;
    private boolean _isInitRect;
    private TouchPlateListener _listener;

    public TouchPlate(Context context)
    {
        super(context);
    }

    public TouchPlate(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TouchPlate(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (!_isInitRect)
        {
            _isInitRect = true;
            layout(0, 0, r - l, b - t);
        }
    }

    public void setListener(TouchPlateListener listener)
    {
        _listener = listener;
    }

    public void setEditable(boolean isEdt)
    {
        if (_isEditable != isEdt)
        {
            _isEditable = isEdt;
            TouchView tv = getLastView();
            if (tv != null)
            {
                tv.setEditable(isEdt);
            }
        }
    }

    public void addView(View child)
    {
        if (child instanceof TouchView)
        {
            if (child instanceof TouchTextView)
            {
                ((TouchTextView)child).setDoubleTapListener(this);
            }
            child.setId(View.NO_ID);
            ((TouchView) child).setEditable(true);
            if (getChildCount() > 0)
            {
                ((TouchView)getChildAt(getChildCount() - 1)).setEditable(false);
            }
            super.addView(child);
            requestLayout();
        }else
        {
            throw new TouchView.NotSupportedException("Does not support the addition of touchview other than view");
        }
    }

    private ArrayList<Object> getPointInRect(float x, float y)
    {
        int count = getChildCount();
        if (count < 2) return null;
        ArrayList<Object> vl = new ArrayList<>();
        ArrayList<Integer> il = new ArrayList<>();
        TouchView tv;
        for (int i = count - 1;i > -1;i--)
        {
            tv = (TouchView)getChildAt(i);
            if (tv.isInRect(x, y))
            {
                vl.add(tv);
                il.add(tv.getId());
            }
        }
        if (vl.size() == 0) return null;
        Collections.sort(il);
        count = il.size();
        StringBuffer ids = new StringBuffer();
        for (int i = 0;i < count;i++)
        {
            ids.append(il.get(i));
        }
        vl.add(ids.toString());
        return vl;
    }

    private void clearList(ArrayList list)
    {
        if (list != null)list.clear();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (!_isEditable) return true;
        switch (ev.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                _downX = ev.getX();
                _downY = ev.getY();
                _isSwitch = true;
                ArrayList<Object> list = getPointInRect(ev.getX(), ev.getY());
                if (list != null && list.size() == 2)
                {
                    TouchView tv = (TouchView) list.get(0);
                    if (getChildAt(getChildCount() - 1) != tv)
                    {
                        tv.setEditable(true);
                        ((TouchView)getChildAt(getChildCount() - 1)).setEditable(false);
                        tv.bringToFront();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                list = getPointInRect(ev.getX(), ev.getY());
                _isSwitch = Math.abs(_downX - ev.getX()) <= 10 && Math.abs(_downY - ev.getY()) < 10
                && list != null && list.size() > 2;
                break;
            case MotionEvent.ACTION_UP:
                if (_isSwitch)
                {
                    list = getPointInRect(ev.getX(), ev.getY());
                    if (list != null)
                    {
                        if (_overlapViewList == null ||
                                !((String) _overlapViewList.get(_overlapViewList.size() - 1)).equals((String)list.get(list.size() - 1)))
                        {
                            clearList(_overlapViewList);
                            ((TouchView)getChildAt(getChildCount() - 1)).setEditable(false);
                            _overlapViewList = list;
                            _curOverlapIndex = 0;
                            _curOverlapIndex = _curOverlapIndex + 1 % (_overlapViewList.size() - 1);
                            _lastView = ((TouchView)_overlapViewList.get(_curOverlapIndex));

                        }else
                        {
                            _lastView.setEditable(false);
                            _curOverlapIndex = (_curOverlapIndex + 1) % (_overlapViewList.size() - 1);
                            _lastView = ((TouchView)_overlapViewList.get(_curOverlapIndex));
                        }
                        _lastView.setEditable(true);
                        _lastView.bringToFront();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public TouchView getLastView()
    {
        if (getChildCount() > 0)
        {
            return (TouchView)getChildAt(getChildCount() - 1);
        }
        return null;
    }

    public void drawInBmp(Bitmap bmp, Rect bgRc)
    {
        for (int i = 0;i < getChildCount();i++)
        {
            ((TouchView)getChildAt(i)).drawInBmp(bmp, bgRc);
        }
    }

    /****************  TouchTextViewDoubleTapListener  *****************/
    @Override
    public void onTTVDoubleTap()
    {
        if (_listener != null)
        {
            _listener.onShowTextInput();
        }
    }
}
