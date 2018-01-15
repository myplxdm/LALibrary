package com.liu.lalibrary.ui.titleview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        llSet[0] = new LinearLayout(context);
        llSet[0].setOrientation(LinearLayout.HORIZONTAL);
        AutoRelativeLayout.LayoutParams lp = new AutoRelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(llSet[0], lp);
        //
        llSet[1] = new LinearLayout(context);
        llSet[1].setOrientation(LinearLayout.HORIZONTAL);
        lp = new AutoRelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(llSet[1], lp);
        //
        llSet[2] = new LinearLayout(context);
        llSet[2].setOrientation(LinearLayout.HORIZONTAL);
        lp = new AutoRelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(llSet[2], lp);
        //
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LTitleView);
        if (attr != null)
        {
            space = attr.getDimensionPixelSize(R.styleable.LTitleView_viewSpace, SPACE_);
            //
            attr.recycle();
        }
    }

    private View addImageViewTo(int resId, LinearLayout ll, int space)
    {
        Bitmap b = BitmapFactory.decodeResource(getResources(), resId);
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(b.getWidth(), b.getHeight());
        iv.setLayoutParams(lp);
        iv.setImageBitmap(b);
        ll.addView(iv);
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
        AutoUtils.auto(tv);
        return tv;
    }

    private void clickProc(View view, final int index, final int tvl)
    {
//        if (tvl == TVL_MIDDLE)
//        {
//            if (!middelClickSetup)
//            {
//                middelClickSetup = true;
//                llSet[tvl].setOnClickListener(new OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        listener.onViewClick(tvl, 0, v);
//                    }
//                });
//            }
//            return;
//        }
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
    public int addImageView(int tvl, int resId, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addImageViewTo(resId, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
        return index;
    }

    @Override
    public int addImageView(int tvl, String imageUrl, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addImageViewTo(imageUrl, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
        return index;
    }

    @Override
    public int addTextView(int tvl, String text, int textSize, int textColor, boolean needClick)
    {
        LinearLayout ll = llSet[tvl];
        int index = ll.getChildCount();
        View view = addTextViewTo(text, textSize, textColor, ll, (Math.min(index, 1) * space * (tvl < TVL_RIGHT ? -1 : 1)));
        if (needClick) clickProc(view, index, tvl);
        return index;
    }

    @Override
    public View getView(int tvl, int index)
    {
        LinearLayout ll = llSet[tvl];
        if (index >= ll.getChildCount()) return null;
        return ll.getChildAt(index);
    }

    @Override
    public View mdImgView(int tvl, int index, int resId)
    {
        LinearLayout ll = llSet[tvl];
        if (index >= ll.getChildCount()) return null;
        ImageView iv = (ImageView) ll.getChildAt(index);
        iv.setImageResource(resId);
        AutoUtils.auto(iv);
        return iv;
    }

    @Override
    public View mdImgView(int tvl, int index, String imageUrl)
    {
        LinearLayout ll = llSet[tvl];
        if (index >= ll.getChildCount()) return null;
        final ImageView iv = (ImageView) ll.getChildAt(index);
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
        Glide.with(getContext().getApplicationContext()).asBitmap().load(imageUrl).into(target);
        iv.setTag(target);
        return iv;
    }

    @Override
    public View mdTxtView(int tvl, int index, String text)
    {
        LinearLayout ll = llSet[tvl];
        if (index >= ll.getChildCount()) return null;
        TextView tv = (TextView) ll.getChildAt(index);
        tv.setText(text);
        return tv;
    }

    @Override
    public void showView(int tvl, int index, boolean isShow)
    {
        LinearLayout ll = llSet[tvl];
        if (index >= ll.getChildCount()) return;
        ll.getChildAt(index).setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearView(int tvl)
    {
        LinearLayout ll = llSet[tvl];
        ll.removeAllViews();
    }

    @Override
    public void setTitleViewListener(TitleViewListener listener)
    {
        this.listener = listener;
    }

}
