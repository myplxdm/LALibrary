package com.liu.lalibrary.ui.circularbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.liu.lalibrary.R;
import com.zhy.autolayout.utils.AutoLayoutHelper;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.autolayout.utils.DimenUtils;

/**
 * TODO: document your custom view class.
 */
public class CircularButton extends View implements View.OnClickListener
{
    private enum BtnState
    {
        idle,play_corner_width,play_circular
    }

    private GradientDrawable            _gdBackground;
    private CircularAnimatedDrawable    _animateDrawable;
    //
    private TextPaint                   _textPaint;
    private float                       _textBaseY = -1;
    //
    private int                         _cornerRadius = -1;
    private int                         _bgColor = -1;
    private String                      _text;
    //
    private OnClickListener             _clickListener;
    private BtnState                    _state = BtnState.idle;
    //
    private int                         _view_width = -1;
    private int                         _view_height = -1;
    private int                         _view_width_xml = 0;
    private int                         _view_height_xml = 0;

    public CircularButton(Context context)
    {
        super(context);
        init(null, 0);
    }

    public CircularButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularButton, defStyle, 0);
        if (a.hasValue(R.styleable.CircularButton_cbCornerRadius))
        {
            _cornerRadius = a.getInt(R.styleable.CircularButton_cbCornerRadius, -1);
        }
        if (a.hasValue(R.styleable.CircularButton_cbBgColor))
        {
            _bgColor = a.getInt(R.styleable.CircularButton_cbBgColor, 0xfffffff);
        }
        //text
        _textPaint = new TextPaint();
        _textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        _textPaint.setTextAlign(Paint.Align.CENTER);
        if (a.hasValue(R.styleable.CircularButton_cbText))
        {
            _text = a.getString(R.styleable.CircularButton_cbText);
            _textPaint.setColor(a.getInt(R.styleable.CircularButton_cbTextColor, 0xfffffff));
            int size = AutoUtils.getPercentHeightSizeBigger((int)a.getDimension(R.styleable.CircularButton_cbTextSize, 10));
            _textPaint.setTextSize(size);
        }
        a.recycle();
        //
        a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_width, android.R.attr.layout_height});
        for (int i = 0;i < a.getIndexCount();i++)
        {
            int index = a.getIndex(i);
            if (DimenUtils.isPxVal(a.peekValue(index)))
            {
                if (index == 0)
                {
                    _view_width_xml = a.getDimensionPixelOffset(index, 0);
                }else
                {
                    _view_height_xml = a.getDimensionPixelOffset(index, 0);
                }
            }
        }
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
        super.setOnClickListener(this);
    }

    private void invalidateTextPaintAndMeasurements()
    {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (_view_width == -1)
        {
            _view_width = getWidth();
            _view_height = getHeight();
        }

        if (_gdBackground == null)
        {
            _gdBackground = (GradientDrawable) getResources().getDrawable(R.drawable.cb_background).mutate();
            _gdBackground.setColor(_bgColor);
            if (_cornerRadius != -1)
            {
                _gdBackground.setCornerRadius(_cornerRadius);
            }
        }
        _gdBackground.draw(canvas);

        if (_state == BtnState.idle && _text != null)
        {
            if (_textBaseY == -1)
            {
                Paint.FontMetrics fm = _textPaint.getFontMetrics();
                float fontHeight = fm.bottom - fm.top;
                _textBaseY = _view_height - (_view_height - fontHeight) / 2 - fm.bottom;
            }
            canvas.drawText(_text, _view_width / 2, _textBaseY, _textPaint);
        }else if (_state == BtnState.play_circular)
        {
            if (_animateDrawable == null)
            {
                _animateDrawable = new CircularAnimatedDrawable(0xffffff, 5);
                _animateDrawable.start();
            }
            _animateDrawable.draw(canvas);
        }

    }

    public void setOnClickListener(OnClickListener listener)
    {
        _clickListener = listener;
    }

    public void stop()
    {
        if (_animateDrawable != null)
        {
            _animateDrawable.stop();
        }

    }

    private void reversePlayAnimate()
    {
//        ObjectAnimator woa = ObjectAnimator.ofInt(new ViewPropertyWrapper(this), "width", _view_height_xml, _view_width_xml);
//        woa.setDuration(600);
//
//        ObjectAnimator coa = ObjectAnimator.ofFloat(_gdBackground, "cornerRadius", _view_height, _cornerRadius == -1 ? 0 : _cornerRadius);
//        coa.setDuration(300);
//
//        AnimatorSet as = new AnimatorSet();
//        as.playSequentially(woa, coa);
//        as.addListener(new AnimatorListenerAdapter()
//        {
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//                _state = BtnState.idle;
//                invalidate();
//            }
//        });
//        as.start();
    }

    private void playAnimate()
    {
//        ObjectAnimator coa = ObjectAnimator.ofFloat(_gdBackground, "cornerRadius", _cornerRadius == -1 ? 0 : _cornerRadius, _view_height);
//        coa.setDuration(300);
//
//        ObjectAnimator woa = ObjectAnimator.ofInt(new ViewPropertyWrapper(this), "width", _view_width_xml, _view_height_xml);
//        woa.setDuration(600);
//        AnimatorSet as = new AnimatorSet();
//        as.playSequentially(coa,woa);
//        as.addListener(new AnimatorListenerAdapter()
//        {
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//                _state = BtnState.play_circular;
//                invalidate();
//            }
//        });
//        as.start();
    }

    /*
        OnClickListener
     */
    @Override
    public void onClick(View view)
    {
        if (_state == BtnState.idle)
        {
            _clickListener.onClick(view);
            playAnimate();
            _state = BtnState.play_corner_width;

        }
    }
}
