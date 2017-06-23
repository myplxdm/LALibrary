package com.liu.lalibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.liu.lalibrary.R;
import com.liu.lalibrary.utils.DensityUtils;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.concurrent.ExecutionException;

public class RLTitleView extends AutoRelativeLayout implements OnClickListener
{
    private static final int VIEW_SPACE = 20;
    public static final int TITLE_ALIG_LEFT = 0;
    public static final int TITLE_ALIG_MIDDLE = 1;
    //
    public static final int BTN_TYPE_NORMAL = 0;
    public static final int BTN_TYPE_PK = 1;
    public static final int BTN_TYPE_JS = 2;

    public interface OnTitleViewBtnClickListener
    {
        public void onReturnClick();

        public void onTitleViewBtnClick(View owner, String title, String url, boolean bReload, boolean bTrans, int btnType);

        public void onTitleClick();
    }
    //title view
    //private RelativeLayout  rl_title_view;
    private TextView        tv_title_left;
    private TextView        tv_title_center;
    private ImageView       iv_return;
    private ImageView       ib_left_btn;
    private LinearLayout    ll_right;
    //
    private Context context;
    private OnTitleViewBtnClickListener listener;

    public RLTitleView(Context context)
    {
        this(context, null, 0);
    }

    public RLTitleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RLTitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_title_view, this, true);
        tv_title_left = (TextView) findViewById(R.id.tv_title_left);
        tv_title_center = (TextView) findViewById(R.id.tv_title_center);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        ib_left_btn = (ImageView) findViewById(R.id.iv_left_btn);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        //
        iv_return.setOnClickListener(this);
        tv_title_left.setOnClickListener(this);
        tv_title_center.setOnClickListener(this);

        AutoUtils.auto(this);
    }

    public void setLeftBtn(String leftBtnImgUrl, int btImgW, int btImgH,
                           final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        if (leftBtnImgUrl != null)
        {
            ib_left_btn.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(Uri.decode(leftBtnImgUrl)).into(ib_left_btn);
        }else
        {
            ib_left_btn.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ib_left_btn.getLayoutParams();
        lp.width = DensityUtils.dp2px(context, btImgW);
        lp.height = DensityUtils.dp2px(context, btImgH);
        ib_left_btn.setLayoutParams(lp);
        ib_left_btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (listener != null)
                {
                    listener.onTitleViewBtnClick(ib_left_btn, title, url, bReload, bTrans, btnType);
                }
            }
        });
    }

    public void set(int retImgResId, String title, int titleAlignment, long bgc)
    {
        if (retImgResId > 0)
        {
            iv_return.setVisibility(View.VISIBLE);
            iv_return.setBackgroundResource(retImgResId);
        }else
        {
            iv_return.setVisibility(View.GONE);
        }
        if (bgc != 0)
        {
            setBackgroundColor((int)bgc);
        }
        //title
        if (titleAlignment == TITLE_ALIG_LEFT) // left
        {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(title);
            tv_title_center.setVisibility(View.GONE);
        } else if (titleAlignment == TITLE_ALIG_MIDDLE) // middle
        {
            tv_title_center.setVisibility(View.VISIBLE);
            tv_title_center.setText(title);
            tv_title_left.setVisibility(View.GONE);
        }
    }

    public void setTitleSizeAndColor(int textSize, int textColor)
    {
        tv_title_left.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_title_left.setTextColor(textColor);
        tv_title_center.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_title_center.setTextColor(textColor);
        AutoUtils.auto(tv_title_center);
        AutoUtils.auto(tv_title_left);
    }

    public void setTitleListener(OnTitleViewBtnClickListener l)
    {
        listener = l;
    }

    public void addButton(final ViewGroup vg, final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        ll_right.addView(vg);
        vg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onTitleViewBtnClick(vg, title, url, bReload, bTrans, btnType);
                }
            }
        });
    }

    public void addImgButton(int resId, final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        RelativeLayout rl = (RelativeLayout)View.inflate(context, R.layout.layout_titleview_imgbtn, null);
        final ImageView iv = (ImageView) rl.findViewById(R.id.iv_btn);
        iv.setBackgroundResource(resId);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //lp.setMargins(0, 0, DensityUtils.dp2px(context, VIEW_SPACE), 0);
        ll_right.addView(rl, lp);
        rl.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onTitleViewBtnClick(iv, title, url, bReload, bTrans, btnType);
                }
            }
        });
    }

    public void addImgButton(String btImgUrl, final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        RelativeLayout rl = (RelativeLayout)View.inflate(context, R.layout.layout_titleview_imgbtn, null);
        final ImageView iv = (ImageView) rl.findViewById(R.id.iv_btn);
        //Glide.with(context).load(btImgUrl).into(iv);
        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition)
            {
                if (resource != null)
                {
                    iv.getLayoutParams().width = resource.getWidth();
                    iv.getLayoutParams().height = resource.getHeight();
                    AutoUtils.auto(iv);
                }
            }
        };
        Glide.with(context)
                .asBitmap()
                .load(btImgUrl)
                .into(target);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, DensityUtils.dp2px(context, VIEW_SPACE), 0);
        ll_right.addView(rl, lp);
        rl.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onTitleViewBtnClick(iv, title, url, bReload, bTrans, btnType);
                }
            }
        });
    }

    public void addTextButton(String btText, long btTextColor, int textSize, final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        //RelativeLayout rl = (RelativeLayout)View.inflate(context, R.layout.layout_titleview_textbtn, null);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_titleview_textbtn, ll_right, false);
        final TextView tv = (TextView) v.findViewById(R.id.tv_btn);
        tv.setText(btText);
        tv.setTextColor((int) btTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 3);
        AutoUtils.auto(v);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //lp.setMargins(0, 0, DensityUtils.dp2px(context, VIEW_SPACE), 0);
        ll_right.addView(v);
        v.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onTitleViewBtnClick(tv, title, url, bReload, bTrans, btnType);
                }
            }
        });
    }

    public void showButton(int index, boolean bShow)
    {
        if (index < ll_right.getChildCount())
        {
            ll_right.getChildAt(index).setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }

    public void showButtonHot(int index, boolean bShow)
    {
        if (index < ll_right.getChildCount())
        {
            RelativeLayout rl = (RelativeLayout) ll_right.getChildAt(index);
            rl.findViewById(R.id.iv_hot).setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }

    public void mdImgBtnIco(int index, String imgUrl)
    {
        if (index < ll_right.getChildCount())
        {
            ImageView iv = (ImageView) ll_right.getChildAt(index);
            Glide.with(getContext()).load(Uri.decode(imgUrl)).into(iv);
        }
    }

    public void mdTextBtnTitle(int index, String text)
    {
        if (index < ll_right.getChildCount())
        {
            TextView tv = (TextView) ll_right.getChildAt(index);
            tv.setText(text);
        }
    }

    public void clearBtn()
    {
        ll_right.removeAllViews();
    }

    @Override
    public void onClick(View v)
    {
        if (listener != null)
        {
            if (v.getId() == R.id.iv_return)
            {
                listener.onReturnClick();
            }else
            {
                listener.onTitleClick();
            }
        }
    }
}
