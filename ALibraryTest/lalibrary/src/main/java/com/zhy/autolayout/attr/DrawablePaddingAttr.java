package com.zhy.autolayout.attr;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhy on 15/12/5.
 */
public class DrawablePaddingAttr extends AutoAttr
{
    public DrawablePaddingAttr(int pxVal, int baseWidth, int baseHeight)
    {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected int attrVal()
    {
        return Attrs.PADDING_TOP;
    }

    @Override
    protected boolean defaultBaseWidth()
    {
        return false;
    }

    @Override
    protected void execute(View view, int val)
    {
        if (view instanceof TextView)
        {
            ((TextView)view).setCompoundDrawablePadding(val);
        }
    }

    public static DrawablePaddingAttr generate(int val, int baseFlag)
    {
        DrawablePaddingAttr attr = null;
        switch (baseFlag)
        {
            case AutoAttr.BASE_WIDTH:
                attr = new DrawablePaddingAttr(val, Attrs.DRAWABLE_PADDING, 0);
                break;
            case AutoAttr.BASE_HEIGHT:
                attr = new DrawablePaddingAttr(val, 0, Attrs.DRAWABLE_PADDING);
                break;
            case AutoAttr.BASE_DEFAULT:
                attr = new DrawablePaddingAttr(val, 0, 0);
                break;
        }
        return attr;
    }
}
