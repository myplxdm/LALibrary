package com.liu.lalibrary.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.liu.lalibrary.R;

/**
 * TODO: document your custom view class.
 */
public class CircularProgressBar extends View
{
    private static final long START_ANIMATOR_DURATION = 2000;
    private static final long SWEEP_ANGLE = 240;
    private static final long APPEAR_SWEEP_INC = 8;
    private static final long DISAPPEAR_SWEEP_DEC = 6;
    private static final long DISAPPEAR_START_INC = 8;
    private static final int DEFAULT_WIDTH_HEIGHT = 300;
    private static final float DEFAULT_LINE_WIDHT = 15;
    private static final int DEFAULT_LINE_COLOR = 0xffffffff;

    private ValueAnimator _startAngleAnimator;
    //
    private float _currStartAngle = 0;
    private float _currSweepAngle = 0;
    private Paint _paint = new Paint();
    private RectF _paintRect;
    private boolean _appear = true;
    private float _line_width = DEFAULT_LINE_WIDHT;
    private int _line_color = DEFAULT_LINE_COLOR;

    public CircularProgressBar(Context context)
    {
        super(context);
        init(null, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, defStyle, 0);
        _line_width = a.getFloat(R.styleable.CircularProgressBar_lineWidth, DEFAULT_LINE_WIDHT);
        _line_color = a.getInt(R.styleable.CircularProgressBar_lineColor, DEFAULT_LINE_COLOR);
        //
        _paint.setAntiAlias(true);
        _paint.setColor(_line_color);
        _paint.setStrokeWidth(_line_width);
        _paint.setStyle(Paint.Style.STROKE);
        //
        _startAngleAnimator = ValueAnimator.ofFloat(0f, 360f);
        _startAngleAnimator.setInterpolator(new LinearInterpolator());
        _startAngleAnimator.setDuration(START_ANIMATOR_DURATION);
        _startAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        _startAngleAnimator.setRepeatMode(ValueAnimator.RESTART);
        _startAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                invalidate();
            }
        });
    }

    public void startAnimation()
    {
        _startAngleAnimator.cancel();
        _startAngleAnimator.start();
        setVisibility(View.VISIBLE);
    }

    public void stopAnimation()
    {
        _startAngleAnimator.cancel();
        _currSweepAngle = 0;
        _currSweepAngle = 0;
        setVisibility(View.INVISIBLE);
    }

    public void setLineWidht(float widht)
    {
        _line_width = widht;
    }

    public void setLineColor(int color)
    {
        _line_color = color;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 这里要计算一下控件的实际大小，然后调用setMeasuredDimension来设置
        int width = this.getMeasuredSize(widthMeasureSpec, true);
        int height = this.getMeasuredSize(heightMeasureSpec, false);
        setMeasuredDimension(width, height);
    }

    /**
     * 计算控件的实际大小
     *
     * @param length  onMeasure方法的参数，widthMeasureSpec或者heightMeasureSpec
     * @param isWidth 是宽度还是高度
     * @return int 计算后的实际大小
     */
    private int getMeasuredSize(int length, boolean isWidth)
    {
        // 模式
        int specMode = MeasureSpec.getMode(length);
        // 尺寸
        int specSize = MeasureSpec.getSize(length);
        // 计算所得的实际尺寸，要被返回
        int retSize = 0;
        // 得到两侧的padding（留边）
        int padding = (isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom());

        // 对不同的指定模式进行判断
        if (specMode == MeasureSpec.EXACTLY)
        {  // 显式指定大小，如40dp或fill_parent
            retSize = specSize;
        } else
        {                              // 如使用wrap_content
            retSize = (isWidth ? DEFAULT_WIDTH_HEIGHT + padding : DEFAULT_WIDTH_HEIGHT + padding);
            if (specMode == MeasureSpec.UNSPECIFIED)
            {
                retSize = Math.min(retSize, specSize);
            }
        }
        return retSize;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (_paintRect == null)
        {
            _paintRect = new RectF();
            _paintRect.set(_line_width, _line_width, getWidth() - _line_width, getHeight() - _line_width);
        }
        _currStartAngle = (_currStartAngle + 1) % 360f;
        if (_appear)
        {
            _currSweepAngle += APPEAR_SWEEP_INC;
            _appear = !(_currSweepAngle >= SWEEP_ANGLE);
        } else
        {
            _currStartAngle += DISAPPEAR_START_INC;
            _currSweepAngle -= DISAPPEAR_SWEEP_DEC;
            _appear = _currSweepAngle <= 0;
        }
        //
        canvas.drawArc(_paintRect, _currStartAngle, _currSweepAngle, false, _paint);
    }
}
