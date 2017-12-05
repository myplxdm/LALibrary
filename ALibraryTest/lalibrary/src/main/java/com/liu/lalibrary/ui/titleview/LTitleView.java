package com.liu.lalibrary.ui.titleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liu.lalibrary.R;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by liu on 2017/4/20.
 */

public class LTitleView extends AutoRelativeLayout implements ITitleView
{
    public static final int TVL_LEFT = 0;
    public static final int TVL_MIDDLE = 1;
    public static final int TVL_RIGHT = 2;

    private final int COLOR_BG = 0xffffff;
    private final int COLOR_TITLE_TEXT = 0xff000000;
    private final int SIZE_TITLE_TEXt = 58;
    private final int SPACE_ = 10;
    //
    private boolean middelClickSetup;
    private int space;
    //
    private LinearLayout llSet[] = new LinearLayout[3];
    //
    private TitleViewListener listener;

    public LTitleView(Context context)
    {
        this(context, null, 0);
    }

    public LTitleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LTitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.title_view, this, true);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LTitleView);
        if (attr != null)
        {
            int bgColor = attr.getColor(R.styleable.LTitleView_bgColor, COLOR_BG);
            setBackgroundColor(bgColor);
            //
            space = attr.getDimensionPixelSize(R.styleable.LTitleView_viewSpace, SPACE_);
            //
            attr.recycle();
        }
        llSet[0] = (LinearLayout) findViewById(R.id.llLeft);
        llSet[1] = (LinearLayout) findViewById(R.id.llMiddle);
        llSet[2] = (LinearLayout) findViewById(R.id.llRight);
    }


    private View addImageViewTo(int resId, LinearLayout ll, int space)
    {
        Drawable d = getResources().getDrawable(resId);
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ll.addView(iv, lp);
        if (space != 0)
        {
            if (space < 0) lp.setMargins(space, 0, 0, 0);
            else lp.setMargins(0, 0, space, 0);
        }
        return iv;
    }

    private View addImageViewTo(String url, LinearLayout ll, int space)
    {
        final ImageView iv = new ImageView(getContext());
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>()
        {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition)
            {
                try
                {
                    iv.setImageBitmap(resource);
                    iv.getLayoutParams().width = resource.getWidth();
                    iv.getLayoutParams().height = resource.getHeight();
                    AutoUtils.auto(iv);
                    iv.setTag(null);
                } catch (Exception e)
                {
                }
            }
        };
        Glide.with(getContext().getApplicationContext()).asBitmap().load(url).into(target);
        iv.setTag(target);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (space != 0)
        {
            if (space < 0) lp.setMargins(space, 0, 0, 0);
            else lp.setMargins(0, 0, space, 0);
        }
        ll.addView(iv, lp);
        return iv;
    }

    private View addTextViewTo(String text, int textSize, int textColor, LinearLayout ll, int space)
    {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        ll.addView(tv, lp);
        if (space != 0)
        {
            if (space < 0) lp.setMargins(space, 0, 0, 0);
            else lp.setMargins(0, 0, space, 0);
        }
        return tv;
    }

    private void clickProc(View view, final int index, final int tvl)
    {
        if (tvl == TVL_MIDDLE)
        {
            if (!middelClickSetup)
            {
                middelClickSetup = true;
                llSet[tvl].setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        listener.onViewClick(tvl, 0, v);
                    }
                });
            }
            return;
        }
        view.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onViewClick(tvl, index, v);
            }
        });
    }

    @Override
    public void addView(int tvl, int resId, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addImageViewTo(resId, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
    }

    @Override
    public void addView(int tvl, String imageUrl, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addImageViewTo(imageUrl, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
    }

    @Override
    public void addView(int tvl, String text, int textSize, int textColor, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addTextViewTo(text, textSize, textColor, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
    }

    @Override
    public void setTitleViewListener(TitleViewListener listener)
    {
        this.listener = listener;
    }

}
