package com.liu.lalibrary.camera.background;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by liu on 2017/1/6.
 */

public class ImageViewEx extends ImageView
{
    private int lastX,lastY;
    private int parentWidth,parentHeight;
    private boolean isEditable = true;
    private boolean isDouble;
    private float doublePointLastDist = 0;
    private float scale = 1;
    private boolean isLimitInParent = true;

    public ImageViewEx(Context context)
    {
        this(context, null, 0);
    }

    public ImageViewEx(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ImageViewEx(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setEditable(boolean isEdt)
    {
        if (isEditable != isEdt)
        {
            isEditable = isEdt;
        }
    }

    public void setLimitInParent(boolean isLimit)
    {
        isLimitInParent = isLimit;
    }

    public float getScale()
    {
        return scale;
    }

    private void onDoubleEvent(MotionEvent event)
    {
        if (event.getPointerCount() < 2) return;

        float newDist = distance(event);
        if (newDist > 10f)
        {
            float s = newDist / doublePointLastDist;
            int w = (int)(getWidth() * s);
            int h = (int)(getHeight() * s);

            scale += s >= 1 ? 1 - s : s - 1;

            int x = (getLeft() + getRight()) / 2 - w / 2;
            int y = (getTop() + getBottom()) / 2 - h / 2;

            layout(x, y, x + w, y + h);
        }
        doublePointLastDist = newDist;
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isEditable) return false;
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                isDouble = false;
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isLimitInParent) return true;
                isDouble = true;
                doublePointLastDist = distance(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDouble)
                {
                    onDoubleEvent(event);
                    return true;
                }

                if (parentWidth == 0 || parentHeight == 0)
                {
                    parentWidth = ((ViewGroup)getParent()).getWidth();
                    parentHeight = ((ViewGroup)getParent()).getHeight();
                }

                int x = (int) event.getRawX() - lastX;
                int y = (int) event.getRawY() - lastY;

                int l = getLeft() + x;
                int t = getTop() + y;
                int r = getRight() + x;
                int b = getBottom() + y;

                if (isLimitInParent)
                {
                    if (l < parentWidth - getWidth())
                    {
                        l = parentWidth - getWidth();
                    }else if (l > 0) l = 0;
                    r = l + getWidth();

                    if (t < parentHeight - getHeight())
                    {
                        t = parentHeight - getHeight();
                    }else if (t > 0) t = 0;
                    b = t + getHeight();
                }
//                if (parentWidth < getWidth())
//                {
//                    if (l < parentWidth - getWidth() || l > 0) x = 0;
//                }else
//                {
//                    //if (l < 0 || r > parentWidth) x = 0;
//                }
//                //y
//                if (parentHeight < getHeight())
//                {
//                    if (t < parentHeight - getHeight() || t > 0) y = 0;
//                }else
//                {
//                    //if (t < 0 || b > parentHeight) y = 0;
//                }

                layout(l, t, r, b);


                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;//处理了触摸消息，消息不再传递
    }
}
