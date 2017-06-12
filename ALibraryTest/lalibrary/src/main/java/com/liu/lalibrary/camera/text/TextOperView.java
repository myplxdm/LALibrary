package com.liu.lalibrary.camera.text;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.touchview.TouchPlate;
import com.liu.lalibrary.ui.touchview.TouchTextView;
import com.liu.lalibrary.ui.view.BaseView;
import com.liu.lalibrary.utils.FontLoader;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by liu on 2017/1/12.
 */

public class TextOperView extends BaseView implements ViewPager.OnPageChangeListener
{
    //oper
    private ImageView iv_font;
    private ImageView iv_font_color;
    private ImageView iv_lines;
    private ImageView iv_add_text;
    //
    private ViewPager vp;
    private View[] pageViews;
    //font list
    //font color
    private View v_white_color;
    private View v_black_color;
    private ImageView iv_dec_alpha;
    private ImageView iv_inc_alpha;
    private int font_alpha = 0xff;
    //lines
    private TextView tv_line_hor;
    private TextView tv_line_ver;
    //
    private TouchPlate touchPlate;
    //
    private Context context;

    public TextOperView(AbsActivity activity, ViewGroup vg, int rid)
    {
        super(activity, vg, rid);
        createOper(rootViewGroup);

        vp = (ViewPager) rootViewGroup.findViewById(R.id.vp);
        pageViews = new View[3];
        vp.addOnPageChangeListener(this);
        vp.setAdapter(contentAdapter);

        createFontList();
        createFontColor();
        createLines();
    }

    public void setTouchPlate(TouchPlate tp)
    {
        touchPlate = tp;
    }

    private void createOper(View v)
    {
        iv_font = (ImageView) v.findViewById(R.id.iv_font);
        iv_font.setOnClickListener(operClickList);

        iv_font_color = (ImageView) v.findViewById(R.id.iv_font_color);
        iv_font_color.setOnClickListener(operClickList);

        iv_lines = (ImageView) v.findViewById(R.id.iv_lines);
        iv_lines.setOnClickListener(operClickList);

        iv_add_text = (ImageView) v.findViewById(R.id.iv_add_text);
        iv_add_text.setOnClickListener(operClickList);
    }

    private void createFontList()
    {
        View v = View.inflate(context, R.layout.camera_text_vp_font_list, null);
        pageViews[0] = v;
        LinearLayout ll = (LinearLayout)v.findViewById(R.id.ll_font_list);
        //FontLoader.getInst().addFontFromAsset(getContext(),"fonts/ccfst.ttf");
        FontLoader.getInst().addFontFromAsset(context, "fonts/fz.ttf");
        FontLoader.getInst().addFontFromAsset(context,"fonts/fzyx.ttf");
        FontLoader.getInst().addFontFromAsset(context,"fonts/ht.ttf");
        FontLoader.getInst().addFontFromAsset(context,"fonts/jhc.ttf");
        //FontLoader.getInst().addFontFromAsset(getContext(),"fonts/jqs.ttf");
        int fc = FontLoader.getInst().getFontCount();
        TextView tv;
        LinearLayout.LayoutParams lp;
        for (int i = 0;i < fc;i++)
        {
            tv = new TextView(context);
            tv.setBackgroundResource(R.drawable.font_bg);
            tv.setText("茶");
            tv.setTypeface(FontLoader.getInst().getFont(i));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 69);
            tv.setGravity(Gravity.CENTER);
            tv.setTag(i);
            tv.setOnClickListener(fontClick);
            lp = new LinearLayout.LayoutParams(150, 150);
            lp.leftMargin = 50;
            tv.setLayoutParams(lp);
            AutoUtils.auto(tv);
            ll.addView(tv);
        }
    }

    private void createFontColor()
    {
        View v = View.inflate(context, R.layout.camera_text_vp_font_color, null);
        pageViews[1] = v;

        v_white_color = (View) v.findViewById(R.id.v_white_color);
        v_white_color.setOnClickListener(colorClick);

        v_black_color = (View) v.findViewById(R.id.v_black_color);
        v_black_color.setOnClickListener(colorClick);

        iv_dec_alpha = (ImageView) v.findViewById(R.id.iv_dec_alpha);
        iv_dec_alpha.setOnClickListener(colorClick);

        iv_inc_alpha = (ImageView) v.findViewById(R.id.iv_inc_alpha);
        iv_inc_alpha.setOnClickListener(colorClick);
    }

    private void createLines()
    {
        View v = View.inflate(context, R.layout.camera_text_vp_lines, null);
        pageViews[2] = v;

        tv_line_hor = (TextView) v.findViewById(R.id.tv_line_hor);
        tv_line_hor.setOnClickListener(linesClick);

        tv_line_ver = (TextView) v.findViewById(R.id.tv_line_ver);
        tv_line_ver.setOnClickListener(linesClick);
    }

    View.OnClickListener operClickList = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v.getId() == R.id.iv_add_text)
            {
                TouchTextView ttv = new TouchTextView(touchPlate.getContext());
                ttv.setText("双击修改");
                touchPlate.addView(ttv);
            }else
            {
                iv_font.setBackgroundColor(0xffffffff);
                iv_font_color.setBackgroundColor(0xffffffff);
                iv_lines.setBackgroundColor(0xffffffff);
                int i = v.getId();
                if (i == R.id.iv_font)
                {
                    iv_font.setBackgroundColor(0xffaad993);
                    vp.setCurrentItem(0);

                } else if (i == R.id.iv_font_color)
                {
                    iv_font_color.setBackgroundColor(0xffaad993);
                    vp.setCurrentItem(1);

                } else if (i == R.id.iv_lines)
                {
                    iv_lines.setBackgroundColor(0xffaad993);
                    vp.setCurrentItem(2);

                }
            }
        }
    };

    View.OnClickListener colorClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TouchTextView ttv = (TouchTextView) touchPlate.getLastView();
            if (ttv == null) return;
            int i = v.getId();
            if (i == R.id.v_white_color)
            {
                ttv.setTextColor(0xffffffff);

            } else if (i == R.id.v_black_color)
            {
                ttv.setTextColor(0xff000000);

            } else if (i == R.id.iv_dec_alpha)
            {
                font_alpha -= 20;
                if (font_alpha < 0) font_alpha = 0;
                ttv.setTextColorAlpha(font_alpha);

            } else if (i == R.id.iv_inc_alpha)
            {
                font_alpha += 20;
                if (font_alpha > 255) font_alpha = 255;
                ttv.setTextColorAlpha(font_alpha);

            }
        }
    };

    View.OnClickListener linesClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TouchTextView ttv = (TouchTextView) touchPlate.getLastView();
            if (ttv == null) return;

            tv_line_hor.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.camera_horizontal,0,0);
            tv_line_ver.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.camera_vertical,0,0);
            int i = v.getId();
            if (i == R.id.tv_line_hor)
            {
                tv_line_hor.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.camera_horizontal_sel, 0, 0);
                ttv.setOrientation(TouchTextView.TextOrientation.to_horizontal);

            } else if (i == R.id.tv_line_ver)
            {
                tv_line_ver.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.camera_vertical_sel, 0, 0);
                ttv.setOrientation(TouchTextView.TextOrientation.to_vertical);

            }
        }
    };

    View.OnClickListener fontClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int i = (Integer) v.getTag();
            TouchTextView ttv = (TouchTextView) touchPlate.getLastView();
            if (ttv != null)
            {
                ttv.setFont(FontLoader.getInst().getFont(i));
            }
        }
    };

    private PagerAdapter contentAdapter = new PagerAdapter()
    {

        @Override
        public boolean isViewFromObject(View view, Object object)
        {//必须实现
            return view == object;
        }

        @Override
        public int getCount()
        {
            return pageViews.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {//必须实现，实例化
            container.addView(pageViews[position]);
            return pageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {//必须实现，销毁
            container.removeView(pageViews[position]);
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        switch (position)
        {
            case 0:
                operClickList.onClick(iv_font);
                break;
            case 1:
                operClickList.onClick(iv_font_color);
                break;
            case 2:
                operClickList.onClick(iv_lines);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}
