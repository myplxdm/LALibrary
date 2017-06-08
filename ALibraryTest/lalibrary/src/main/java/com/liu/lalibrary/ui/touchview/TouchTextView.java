package com.liu.lalibrary.ui.touchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by liu on 16/7/4.
 */
public class TouchTextView extends TouchView
{
    private final int MIN_WIDHT                 = 5;
    private final int MIN_HEIGHT                = 5;
    private final int DEFAULT_TEXT_COLOR        = Color.BLACK;
    private final int DEFAULT_TEXT_SIZE         = 80;
    private final int DEFAULT_TEXT_VER_SPACE    = 3;

    public enum TextOrientation
    {
        to_horizontal,to_vertical
    }

    public interface TouchTextViewDoubleTapListener
    {
        public void onTTVDoubleTap();
    }

    private TouchTextViewDoubleTapListener      _listener;
    private GestureDetector                     _detector;
    private String                              _drawText;
    private TextOrientation                     _orientation = TextOrientation.to_horizontal;
    private Paint.FontMetrics                   _fontMetrics;
    //
    private Paint                               _textPaint;
    private int                                 _textColor = DEFAULT_TEXT_COLOR;
    private int                                 _textSize = DEFAULT_TEXT_SIZE;

    public TouchTextView(Context context)
    {
        this(context, null, 0);
    }

    public TouchTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TouchTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        _detector = new GestureDetector(context, new GestureListenerImpl());
        _orientation = TextOrientation.to_horizontal;
        //
        _textPaint = new Paint();
        _textPaint.setAntiAlias(true);
        _textPaint.setTextSize(_textSize);
        _textPaint.setColor(_textColor);
        _fontMetrics = _textPaint.getFontMetrics();
    }

    public void setDoubleTapListener(TouchTextViewDoubleTapListener listener)
    {
        _listener = listener;
    }

    private int getFontHeight()
    {
        return (int)Math.ceil(_fontMetrics.descent - _fontMetrics.ascent);
    }

    private Bitmap createTextBmp(String text, TextOrientation or)
    {
        Rect bound = new Rect();
        _textPaint.getTextBounds(text, 0, text.length(), bound);
        if (or == TextOrientation.to_horizontal)
        {
            int w = bound.width() + bound.left;
            Bitmap bit = Bitmap.createBitmap(w, bound.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bit);
            canvas.drawText(text, 0, -bound.top, _textPaint);
            return bit;
        }else
        {
            int w = 0;
            for (int i = 0;i < text.length();i++)
            {
                w = Math.max((int)_textPaint.measureText(text, i, i + 1), w);
            }
            int fh = getFontHeight();
            int h = (fh + DEFAULT_TEXT_VER_SPACE) * text.length() - DEFAULT_TEXT_VER_SPACE;
            Bitmap bit = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bit);
            for (int i = 0;i < text.length();i++)
            {
                canvas.drawText(text.substring(i, i + 1), 0, -bound.top + i * (fh + DEFAULT_TEXT_VER_SPACE), _textPaint);
            }
            return bit;
        }
    }

    private void update()
    {
        setImageBitamp(createTextBmp(_drawText, _orientation));
    }

    public void setFont(Typeface tf)
    {
        _textPaint.setTypeface(tf);
        _fontMetrics = _textPaint.getFontMetrics();
        update();
    }

    public void setOrientation(TextOrientation or)
    {
        if (or != _orientation)
        {
            _orientation = or;
            update();
        }
    }

    public void setText(String text)
    {
        if (TextUtils.isEmpty(text) || text.equals(_drawText))return;
        _drawText = text;
        update();
    }

    public void setText(String text, Typeface tf, TextOrientation or)
    {
        _drawText = text;
        _textPaint.setTypeface(tf);
        _orientation = or;
        update();
    }

    public void setTextColor(int color)
    {
        _textPaint.setColor(color);
        update();
    }

    public void setTextColorAlpha(int alpha)
    {
        _textPaint.setAlpha(alpha);
        update();
    }

    public void setText(String text, TextOrientation or)
    {
        _drawText = text;
        _orientation = or;
        update();
    }
//    protected boolean onDrawContent(Canvas canvas)
//    {
//        if (TextUtils.isEmpty(_drawText))return false;
//        canvas.save();
//        canvas.rotate(_degree, _viewWidth / 2.0f, _viewHeight / 2.0f);
//        canvas.scale(_scale, _scale, _viewWidth / 2.0f, _viewHeight / 2.0f);
//        int len = _drawText.length();
//        for (int i = 0;i < len;i++)
//        {
//
//        }
//        if (_orientation == TextOrientation.to_horizontal)
//        {
//            canvas.drawText(_drawText, _leftTopPoint.x, _leftTopPoint.y, _textPaint);
//        }else
//        {
//
//        }
//        canvas.restore();
//    }

//    protected int getContentWidth()
//    {
//        if (_drawText == null) return MIN_WIDHT;
//        Rect rect = new Rect();
//        _textPaint.getTextBounds("测", 0, 1, rect);
//        int len = _drawText.length();
//        if (_orientation == TextOrientation.to_horizontal)
//        {
//            return rect.width() * len + (DEFAULT_TEXT_SPACE * (len - 1));
//        }else
//        {
//            return rect.width();
//        }
//    }
//
//    protected int getContentHeight()
//    {
//        if (_drawText == null) return MIN_HEIGHT;
//        Rect rect = new Rect();
//        _textPaint.getTextBounds("测", 0, 1, rect);
//        int len = _drawText.length();
//        if (_orientation == TextOrientation.to_horizontal)
//        {
//            return rect.height();
//        }else
//        {
//            return rect.height() * len + (DEFAULT_TEXT_SPACE * (len - 1));
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (_detector.onTouchEvent(event))
        {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class GestureListenerImpl extends GestureDetector.SimpleOnGestureListener
    {
        public boolean onDoubleTapEvent(MotionEvent e)
        {
            if (_listener != null)
            {
                _listener.onTTVDoubleTap();
                return true;
            }
            return false;
        }
    }
}
