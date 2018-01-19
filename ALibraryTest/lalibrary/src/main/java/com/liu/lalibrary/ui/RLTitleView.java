package com.liu.lalibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.liu.lalibrary.R;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.DensityUtils;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.attr.Attrs;
import com.zhy.autolayout.attr.AutoAttr;
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

    private TextView tv_title_left;
    //center
    private LinearLayout ll_center;
    private TextView tv_title_center;
    private ImageView iv_center;
    //
    private ImageView iv_return;
    private ImageView ib_left_btn;
    private LinearLayout ll_right;
    private View vBottomLine;
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
        //
        ll_center = (LinearLayout) findViewById(R.id.ll_center);
        tv_title_center = (TextView) findViewById(R.id.tv_title_center);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        //
        iv_return = (ImageView) findViewById(R.id.iv_return);
        ib_left_btn = (ImageView) findViewById(R.id.iv_left_btn);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        vBottomLine = (View) findViewById(R.id.vBottomLine);
        //
        iv_return.setOnClickListener(this);
        tv_title_left.setOnClickListener(this);
        //center
        ll_center.setOnClickListener(this);

        AutoUtils.auto(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        float i = ll_right.getX();
        float w = ll_right.getWidth();
        LogUtils.LOGD(RLTitleView.class, "x = " + i + "  w = " + w);
    }

    public void setLeftBtn(String leftBtnImgUrl, final String title, final String url,
                           final boolean bReload, final boolean bTrans, final int btnType)
    {
        if (TextUtils.isEmpty(leftBtnImgUrl))
        {
            ib_left_btn.setVisibility(View.GONE);
        } else
        {
            ib_left_btn.setVisibility(View.VISIBLE);
            SimpleTarget target = new SimpleTarget<Bitmap>()
            {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition)
                {
                    ib_left_btn.setImageBitmap(resource);
                    ib_left_btn.getLayoutParams().width = resource.getWidth();
                    ib_left_btn.getLayoutParams().height = resource.getHeight();
                    AutoUtils.auto(ib_left_btn);
                }
            };
            Glide.with(context.getApplicationContext()).asBitmap().load(leftBtnImgUrl).into(target);
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
    }

    public void setLeftBtn(String leftBtnImgUrl, int btImgW, int btImgH,
                           final String title, final String url, final boolean bReload, final boolean bTrans, final int btnType)
    {
        if (!TextUtils.isEmpty(leftBtnImgUrl))
        {
            ib_left_btn.setVisibility(View.VISIBLE);
            if (leftBtnImgUrl.startsWith("http"))
            {
                Glide.with(context.getApplicationContext()).load(Uri.decode(leftBtnImgUrl)).into(ib_left_btn);
            } else
            {
                ib_left_btn.setImageResource(Integer.parseInt(leftBtnImgUrl));
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ib_left_btn.getLayoutParams();
            lp.width = btImgW;
            lp.height = btImgH;
            ib_left_btn.setLayoutParams(lp);
            AutoUtils.auto(ib_left_btn);
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
        } else
        {
            ib_left_btn.setVisibility(View.GONE);
        }
    }

    public void set(int retImgResId, String title, int titleAlignment, long bgc)
    {
        if (retImgResId > 0)
        {
            ViewSize vs = DensityUtils.getResImageSize(getResources(), retImgResId);
            iv_return.setVisibility(View.VISIBLE);
            iv_return.setBackgroundResource(retImgResId);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AutoUtils.getPercentWidthSize(vs.width),
                    AutoUtils.getPercentHeightSize(vs.height));
            iv_return.setLayoutParams(lp);
        } else
        {
            iv_return.setVisibility(View.GONE);
        }
        if (bgc != 0)
        {
            setBackgroundColor((int) bgc);
        }
        //title
        if (titleAlignment == TITLE_ALIG_LEFT) // left
        {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(title);
            ll_center.setVisibility(View.GONE);
        } else if (titleAlignment == TITLE_ALIG_MIDDLE) // middle
        {
            ll_center.setVisibility(View.VISIBLE);
            tv_title_center.setText(title);
            tv_title_left.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title)
    {
        tv_title_left.setText(title);
        tv_title_center.setText(title);
    }

    public void setTitleRightImage(int resId, int width, int height)
    {
        if (ll_center.getVisibility() == View.VISIBLE)
        {
            iv_center.setImageResource(resId);
            iv_center.getLayoutParams().width = width;
            iv_center.getLayoutParams().height = height;
            AutoUtils.auto(iv_center);
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

    public void addImgButton(int resId,
                             int width, int height,
                             final String title, final String url,
                             final boolean bReload, final boolean bTrans,
                             final int btnType)
    {
        RelativeLayout rl = (RelativeLayout) View.inflate(context, R.layout.layout_titleview_imgbtn, null);
        final ImageView iv = (ImageView) rl.findViewById(R.id.iv_btn);
        iv.setBackgroundResource(resId);
        LayoutParams lp = new LayoutParams(width, height);
        lp.setMargins(0, 0, ll_right.getChildCount() > 0 ? DensityUtils.dp2px(context, VIEW_SPACE) : 0, 0);
        ll_right.addView(rl, lp);
        AutoUtils.auto(rl);
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
        RelativeLayout rl = (RelativeLayout) View.inflate(context, R.layout.layout_titleview_imgbtn, null);
        final ImageView iv = (ImageView) rl.findViewById(R.id.iv_btn);
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
        Glide.with(context.getApplicationContext()).asBitmap().load(btImgUrl).into(target);
        iv.setTag(target);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, ll_right.getChildCount() > 0 ? DensityUtils.dp2px(context, VIEW_SPACE) : 0, 0);
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
        View v = LayoutInflater.from(context).inflate(R.layout.layout_titleview_textbtn, ll_right, false);
        final TextView tv = (TextView) v.findViewById(R.id.tv_btn);
        tv.setText(btText);
        tv.setTextColor((int) btTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        AutoUtils.auto(v);
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
            View v = ll_right.getChildAt(index);
            if (v instanceof RelativeLayout)
            {
                ImageView iv = (ImageView) v.findViewById(R.id.iv_btn);
                if (iv != null)
                {
                    Glide.with(context.getApplicationContext()).load(Uri.decode(imgUrl)).into(iv);
                }
            }
        }
    }

    public void mdImgBtnIco(int index, int imgResId)
    {
        if (index < ll_right.getChildCount())
        {
            View v = ll_right.getChildAt(index);
            if (v instanceof RelativeLayout)
            {
                ImageView iv = (ImageView) v.findViewById(R.id.iv_btn);
                if (iv != null)
                {
                    iv.setImageResource(imgResId);
                }
            }
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

    public void setShowBottomLine(boolean bShow)
    {
        vBottomLine.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v)
    {
        if (listener == null) return;
        int id = v.getId();
        if (id == R.id.iv_return)
        {
            listener.onReturnClick();
        } else if (id == R.id.ll_center)
        {
            listener.onTitleClick();
        }
    }

}
