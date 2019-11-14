package com.liu.lalibrary.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.liu.lalibrary.R;

public class NoPaddingTextView extends AppCompatTextView
{
    public NoPaddingTextView(Context context) {
        this(context, null);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.NoPaddingTextView, 0, 0);
        int textSize = a.getDimensionPixelSize(R.styleable.NoPaddingTextView_ttSize, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        final Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();

        setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.NoPaddingTextView_ttSize, 12));
        int top = (int) Math.ceil(Math.abs((fontMetricsInt.top - fontMetricsInt.ascent) / 2.0));
        setPadding(0, -(Math.abs(fontMetricsInt.top - fontMetricsInt.ascent) + top)
                , 0,
                fontMetricsInt.top - fontMetricsInt.ascent);
        a.recycle();
    }
//    //文本画笔
//    private TextPaint textPaint;
//    //绘制矩形
//    private Rect rect;
//    //默认宽度
//    private int layoutWidth = -1;
//    //获得每行数据
//    private String[] lineContents;
//    //获取行间距的额外空间
//    private float line_space_height = 0.0f;
//    //获取行间距乘法器
//    private float line_space_height_mult = 1.0f;
//
//    /**
//     * 构造方法
//     *
//     * @param context
//     * @param attrs
//     */
//    public NoPaddingTextView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    /**
//     * 构造方法
//     *
//     * @param context
//     * @param attrs
//     * @param defStyleAttr
//     */
//    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    /**
//     * 初始化方法
//     */
//    private void init(Context context, AttributeSet attrs) {
//        //声明画笔对象
//        textPaint = new TextPaint();
//        //声明矩形绘制对象
//        rect = new Rect();
//        //获得行间距额外数据
//        line_space_height = getLineSpacingExtra();
//        //获得行间距方法器
//        line_space_height_mult = getLineSpacingMultiplier();
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Layout _layout = getLayout();
//        if (_layout != null) {
//            //获得文本内容文本内容不可以修改,平切判断当前当前内容是否为null
//            final String _tvContent = TextUtils.isEmpty(getText()) ? "" : getText().toString();
//            //获取文本长度
//            final int _tvLenght = _tvContent.length();
//            //设置文本宽度
//            textPaint.getTextBounds(_tvContent, 0, _tvLenght, rect);
//            //设置文本大小
//            textPaint.setTextSize(getTextSize());
//            //设置文本颜色
//            textPaint.setColor(getCurrentTextColor());
//            //获取行数据
//            getTextContentData(_layout);
//            //获得行高
//            int _lineHeight = -rect.top + rect.bottom;
//            //初始化布局
//            initLayout(_layout);
//            //设置布局区域
//            int[] _area = getWidthAndHeigt(widthMeasureSpec, heightMeasureSpec, layoutWidth, _layout.getLineCount(), _lineHeight);
//            //设置布局
//            setMeasuredDimension(_area[0], _area[1]);
//        }
//    }
//
//    /**
//     * 初始化化布局高度
//     *
//     * @param _layout
//     */
//    private void initLayout(Layout _layout) {
//        //获得布局大小
//        if (layoutWidth < 0) {
//            //获取第一次测量数据
//            layoutWidth = _layout.getWidth();
//        }
//    }
//
//    /**
//     * 获取布局数据
//     *
//     * @param pWidthMeasureSpec
//     * @param pHeightMeasureSpec
//     * @param pWidth
//     * @return 返回宽高数组
//     */
//    private int[] getWidthAndHeigt(int pWidthMeasureSpec, int pHeightMeasureSpec, int pWidth, int pLineCount, int pLineHeight) {
//        int _widthMode = MeasureSpec.getMode(pWidthMeasureSpec);   //获取宽的模式
//        int _heightMode = MeasureSpec.getMode(pHeightMeasureSpec); //获取高的模式
//        int _widthSize = MeasureSpec.getSize(pWidthMeasureSpec);   //获取宽的尺寸
//        int _heightSize = MeasureSpec.getSize(pHeightMeasureSpec); //获取高的尺寸
//        //声明控件尺寸
//        int _width;
//        int _height;
//        //判断模式
//        if (_widthMode == MeasureSpec.EXACTLY) {
//            //如果match_parent或者具体的值，直接赋值
//            _width = _widthSize;
//        } else {
//            _width = pWidth - rect.left;
//        }
//        //高度跟宽度处理方式一样
//        if (_heightMode == MeasureSpec.EXACTLY) {
//            _height = _heightSize;
//        } else {
//            if(pLineCount > 1){
//                _height = pLineHeight * pLineCount + (int) (line_space_height * line_space_height_mult * (pLineCount -1));
//            }else{
//                _height = pLineHeight * pLineCount;
//            }
//        }
//        //初始化宽高数组
//        int[] _area = {
//                _width,
//                _height
//        };
//        return _area;
//    }
//
//    /**
//     * 获取行数据
//     *
//     * @param _layout 文本布局对象（注：该布局其实使用的是Layout子类对象StaticLayout）
//     */
//    private void getTextContentData(Layout _layout) {
//        //初始化航速数据
//        lineContents = new String[_layout.getLineCount()];
//        //获得每行数据
//        for (int i = 0; i < _layout.getLineCount(); i++) {
//            int _start = _layout.getLineStart(i);
//            int _end = _layout.getLineEnd(i);
//            lineContents[i] = getText().subSequence(_start, _end).toString();
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        //行高
//        float _line_height = -rect.top + rect.bottom;
//        //行间距
//        float _line_space = line_space_height * line_space_height_mult;
//        //循环获取每行数据内容
//        for (int i = 0; i < lineContents.length; i++) {
//            //获得数据
//            String _drawContent = lineContents[i];
//            //显示日志
//            //绘制每行数据
//            canvas.drawText(_drawContent, 0, -rect.top + (_line_height + _line_space) * i, textPaint);
//        }
//    }
}