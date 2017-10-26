package com.liu.lalibrary.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by liu on 2017/10/24.
 */

public class WaveView extends View
{
    private int waveSpeed;//波浪的速度，值越大越慢
    private float progress;
    private int bgColor;
    private Path skinClipPath;//用于裁剪外观
    //
    private int viewWidth;
    private int viewHeight;
    private int waveWidth;//一个波浪的宽度
    private int waveHeight;//波浪的高度
    private int timeOffsetX;//波形X时间偏移
    //
    private ValueAnimator animator;
    private Path wavePath;
    private Paint wavePaint;
    private ArrayList<Integer> waveColors = new ArrayList<>();

    public WaveView(Context context)
    {
        this(context, null, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        bgColor = Color.LTGRAY;
        waveSpeed = 2000;
        wavePath = new Path();
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        wavePaint.setStrokeWidth(8);
        waveColors.add(Color.BLUE);
        //
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(waveSpeed);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                timeOffsetX = (int) ((float)animation.getAnimatedValue() * waveWidth);
                invalidate();
            }
        });
    }

    public void wave(boolean isStart)
    {
        if (isStart)
        {
            animator.start();
        }else
        {
            animator.cancel();
        }
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }

    public void setWaveHeight(int height)
    {
        this.waveHeight = height;
    }

    public void setWaveSpeed(int speed)
    {
        this.waveSpeed = speed;
        animator.setDuration(speed);
    }

    public void addWave(int color)
    {
        waveColors.add(color);
    }

    public void setBgColor(int color)
    {
        this.bgColor = color;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        waveWidth = (int)(w * 0.75);
        viewWidth = w;
        viewHeight = h;
        if (skinClipPath == null)
        {
            skinClipPath = new Path();
            skinClipPath.addCircle(w / 2, h / 2, h / 2, Path.Direction.CW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.clipPath(skinClipPath);
        canvas.drawColor(bgColor);
        int x = 0, endX = 0;
        float by = viewHeight * (1 - progress);
        int wcs = waveColors.size();
        //
//        wavePaint.setColor(waveColors.get(0));
//        wavePaint.setStyle(Paint.Style.FILL);
//        x = 0 + timeOffsetX;
//        endX = x + waveWidth / 2;
//        wavePath.moveTo(x, by);
//        wavePath.quadTo(x + waveWidth / 4, by - waveHeight, endX, by);
//        wavePath.quadTo(x + waveWidth * 0.75f, by + waveHeight, endX + waveWidth / 2, by);
//        wavePath.lineTo(endX + waveWidth / 2, viewHeight);
//        wavePath.lineTo(0,viewHeight);
//        wavePath.close();
//        canvas.drawPath(wavePath, wavePaint);
        for (int i = 0; i < wcs; i++)
        {
            wavePaint.setColor(waveColors.get(i));
            wavePath.reset();
            x = -(waveWidth + i * 10) + timeOffsetX;
            wavePath.moveTo(x, by);
            for (; x < viewWidth + waveWidth; x += waveWidth)
            {
                endX = x + waveWidth / 2;
                wavePath.quadTo(x + waveWidth / 4, by - waveHeight, endX, by);
                endX = x + waveWidth;
                wavePath.quadTo(x + waveWidth * 0.75f, by + waveHeight, endX, by);
            }
            wavePath.lineTo(endX, viewHeight);
            wavePath.lineTo(0, viewHeight);
            wavePath.close();
            canvas.drawPath(wavePath, wavePaint);
        }
    }

}
