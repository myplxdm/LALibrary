package com.liu.lalibrary.ui.touchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.liu.lalibrary.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class TouchView extends View
{
    private enum TVState
    {
        init,drag,zoom,remove,doubleZomm
    }

    private final float MAX_SCALE               = 5.0f;
    private final float MIN_SCALE               = 0.3f;
    //
    private final int DEFAULT_FRAME_PADDING     = 28;
    private final int DEFAULT_FRAME_WIDTH       = 2;
    private final int DEFAULT_FRAME_COLOR       = Color.BLUE;
    //
    private Bitmap _bitmap;
    protected int _viewWidth;
    protected int _viewHeight;
    protected float _degree;
    protected float _scale = 1;
    protected Matrix _matrix = new Matrix();
    //ctrl
    protected Drawable _ctrlCloseDrawable;
    protected int _closeDrawableWidht;
    protected int _closeDrawableHeight;

    protected Drawable _ctrlScaleDrawable;
    protected int _scaleDrawableWidth;
    protected int _scaleDrawableHeight;
    //frame
    protected Path _framePath = new Path();
    protected Paint _framePaint = new Paint();
    protected int _framePadding = DEFAULT_FRAME_PADDING;
    protected int _frameColor = DEFAULT_FRAME_COLOR;
    protected int _frameWidth = DEFAULT_FRAME_WIDTH;
    //point
    protected PointF _parentCenter = new PointF();
    protected PointF _preMove = new PointF();
    protected PointF _curMove = new PointF();
    /**
     * 距离父类布局的左间距
     */
    protected int _viewPaddingLeft;

    /**
     * 距离父类布局的上间距
     */
    protected int _viewPaddingTop;
    /**
     * 图片四个点坐标
     */
    protected Point _leftTopPoint;
    protected Point _leftBottomPoint;
    protected Point _rightTopPoint;
    protected Point _rightBottomPoint;
    /**
     * 图片在旋转时x方向的偏移量
     */
    protected int _offsetX;
    /**
     * 图片在旋转时y方向的偏移量
     */
    protected int _offsetY;
    protected boolean _isEditable;
    protected TVState _status = TVState.init;
    protected int _initOffsetX, _initOffsetY;
    protected boolean _initOffset;

    public TouchView(Context context)
    {
        super(context);
        init(null, 0);
    }

    public TouchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public TouchView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TouchView, defStyle, 0);
        _framePadding = a.getDimensionPixelSize(R.styleable.TouchView_framePadding, DEFAULT_FRAME_PADDING);
        _frameWidth = a.getDimensionPixelSize(R.styleable.TouchView_frameWidth, DEFAULT_FRAME_WIDTH);
        _frameColor = a.getColor(R.styleable.TouchView_frameColor, DEFAULT_FRAME_COLOR);
        //
        if (a.hasValue(R.styleable.TouchView_src))
        {
            Drawable drawable = a.getDrawable(R.styleable.TouchView_src);
            _bitmap = drawable2Bitmap(drawable);
        }
        a.recycle();
        //
        _framePaint.setAntiAlias(true);
        _framePaint.setColor(_frameColor);
        _framePaint.setStrokeWidth(_frameWidth);
        _framePaint.setStyle(Paint.Style.STROKE);
        //ctrl
        _ctrlCloseDrawable = getContext().getResources().getDrawable(R.drawable.icon_close);
        _closeDrawableWidht = _ctrlCloseDrawable.getIntrinsicWidth();
        _closeDrawableHeight = _ctrlCloseDrawable.getIntrinsicHeight();

        _ctrlScaleDrawable = getContext().getResources().getDrawable(R.drawable.icon_scale);
        _scaleDrawableWidth = _ctrlScaleDrawable.getIntrinsicWidth();
        _scaleDrawableHeight = _ctrlScaleDrawable.getIntrinsicHeight();

        updateMatrix();
    }

    //返回第一次初始化偏移，以避免生成在同一个位置
    public void getCreateOffset()
    {
        ViewGroup vg = (ViewGroup)getParent();
        if (!_initOffset && vg != null)
        {
            _initOffsetX = _initOffsetY = vg.getChildCount() * 50;
            _initOffset = true;
        }
    }

    protected int getContentWidth()
    {
        if (_bitmap == null) return -1;
        return _bitmap.getWidth();
    }

    protected int getContentHeight()
    {
        if (_bitmap == null) return -1;
        return _bitmap.getHeight();
    }

    protected void updateMatrix()
    {
        int w = getContentWidth();
        int h = getContentHeight();
        if (w == -1 || h == -1) return;
        int bw = (int)(w * _scale);
        int bh = (int)(h * _scale);

        computeRect(-_framePadding, -_framePadding, bw + _framePadding, bh + _framePadding, _degree);

        //设置缩放比例
        _matrix.setScale(_scale, _scale);
        //绕着图片中心进行旋转
        _matrix.postRotate(_degree % 360, bw / 2, bh / 2);
        //设置画该图片的起始点
        _matrix.postTranslate(_offsetX + _scaleDrawableWidth / 2, _offsetY + _scaleDrawableHeight / 2);

        invalidate();
    }

    protected void computeRect(int left, int top, int right, int bottom, float degree)
    {
        Point lt = new Point(left, top);
        Point rt = new Point(right, top);
        Point rb = new Point(right, bottom);
        Point lb = new Point(left, bottom);
        Point cp = new Point((left + right) / 2, (top + bottom) / 2);

        _leftTopPoint = caleRoationPoint(cp, lt, degree);
        _rightTopPoint = caleRoationPoint(cp, rt, degree);
        _rightBottomPoint = caleRoationPoint(cp, rb, degree);
        _leftBottomPoint = caleRoationPoint(cp, lb, degree);

        //计算X坐标最大的值和最小的值
        int maxCoordinateX = getMaxValue(_leftTopPoint.x, _rightTopPoint.x, _rightBottomPoint.x, _leftBottomPoint.x);
        int minCoordinateX = getMinValue(_leftTopPoint.x, _rightTopPoint.x, _rightBottomPoint.x, _leftBottomPoint.x);;

        _viewWidth = maxCoordinateX - minCoordinateX ;


        //计算Y坐标最大的值和最小的值
        int maxCoordinateY = getMaxValue(_leftTopPoint.y, _rightTopPoint.y, _rightBottomPoint.y, _leftBottomPoint.y);
        int minCoordinateY = getMinValue(_leftTopPoint.y, _rightTopPoint.y, _rightBottomPoint.y, _leftBottomPoint.y);

        _viewHeight = maxCoordinateY - minCoordinateY ;


        //View中心点的坐标
        Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);

        _offsetX = _viewWidth / 2 - viewCenterPoint.x;
        _offsetY = _viewHeight / 2 - viewCenterPoint.y;

        int halfDrawableWidth = _scaleDrawableWidth / 2;
        int halfDrawableHeight = _scaleDrawableWidth /2;

        //将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
        _leftTopPoint.x += (_offsetX + halfDrawableWidth);
        _leftBottomPoint.x += (_offsetX + halfDrawableWidth);
        _rightTopPoint.x += (_offsetX + halfDrawableWidth);
        _rightBottomPoint.x += (_offsetX + halfDrawableWidth);

        //将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
        _leftTopPoint.y += (_offsetY + halfDrawableHeight);
        _leftBottomPoint.y += (_offsetY + halfDrawableHeight);
        _rightTopPoint.y += (_offsetY + halfDrawableHeight);
        _rightBottomPoint.y += (_offsetY + halfDrawableHeight);
    }

    protected Point caleRoationPoint(Point center, Point src, float degree)
    {
        //两者之间的距离
        Point disPoint = new Point();
        disPoint.x = src.x - center.x;
        disPoint.y = src.y - center.y;

        //没旋转之前的弧度
        double originRadian = 0;

        //没旋转之前的角度
        double originDegree = 0;

        //旋转之后的角度
        double resultDegree = 0;

        //旋转之后的弧度
        double resultRadian = 0;

        //经过旋转之后点的坐标
        Point resultPoint = new Point();

        double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y * disPoint.y);
        if (disPoint.x == 0 && disPoint.y == 0) // 第一象限
        {
            return center;
        } else if (disPoint.x >= 0 && disPoint.y >= 0) // 第二象限
        {
            // 计算与x正方向的夹角
            originRadian = Math.asin(disPoint.y / distance);
        } else if (disPoint.x < 0 && disPoint.y >= 0) // 第三象限
        {
            // 计算与x正方向的夹角
            originRadian = Math.asin(Math.abs(disPoint.x) / distance);
            originRadian = originRadian + Math.PI / 2;
        } else if (disPoint.x < 0 && disPoint.y < 0)
        {
            // 计算与x正方向的夹角
            originRadian = Math.asin(Math.abs(disPoint.y) / distance);
            originRadian = originRadian + Math.PI;
        } else if (disPoint.x >= 0 && disPoint.y < 0)
        {
            // 计算与x正方向的夹角
            originRadian = Math.asin(disPoint.x / distance);
            originRadian = originRadian + Math.PI * 3 / 2;
        }

        // 弧度换算成角度
        originDegree = radianToDegree(originRadian);
        resultDegree = originDegree + degree;

        // 角度转弧度
        resultRadian = degreeToRadian(resultDegree);

        resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
        resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
        resultPoint.x += center.x;
        resultPoint.y += center.y;

        return resultPoint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取SingleTouchView所在父布局的中心点
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup)
        {
            int parentWidth = mViewGroup.getWidth();
            int parentHeight = mViewGroup.getHeight();
            _parentCenter.set(parentWidth / 2, parentHeight / 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (left != _viewPaddingLeft || top != _viewPaddingTop)
        {
            int actualWidth = _viewWidth + _scaleDrawableWidth;
            int actualHeight = _viewHeight + _scaleDrawableHeight;
            layout(_viewPaddingLeft, _viewPaddingTop, _viewPaddingLeft + actualWidth, _viewPaddingTop + actualHeight);
        }
    }

    /**
     * 调整View的大小，位置
     */
    public void adjustLayout()
    {
        int actualWidth = _viewWidth + _scaleDrawableWidth;
        int actualHeight = _viewHeight + _scaleDrawableHeight;

        int newPaddingLeft = (int) (_parentCenter.x - actualWidth /2);
        int newPaddingTop = (int) (_parentCenter.y - actualHeight/2);

        getCreateOffset();
        if(_viewPaddingLeft != newPaddingLeft || _viewPaddingTop != newPaddingTop)
        {
            _viewPaddingLeft = newPaddingLeft + _initOffsetX;
            _viewPaddingTop = newPaddingTop + _initOffsetX;

            layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
        }
    }

    public void drawInBmp(Bitmap bmp, Rect bgRc)
    {
        Bitmap tmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tmp);
        onDrawContent(c);

        c = new Canvas(bmp);

        float x,y,ws,hs;
        ws = bmp.getWidth() * 1.0f / bgRc.width();
        hs = bmp.getHeight() * 1.0f / bgRc.height();
        x = (getLeft() - bgRc.left) * ws;
        y = (getTop() - bgRc.top) * hs;
        Matrix m = new Matrix();
        m.setScale(ws, ws);
        m.postTranslate(x, y);
        c.drawBitmap(tmp, m, null);

        tmp.recycle();
    }

    /**
     * 设置旋转图
     * @param bitmap
     */
    public void setImageBitamp(Bitmap bitmap)
    {
        if (_bitmap != null)
        {
            _bitmap.recycle();
            _bitmap = null;
        }
        _bitmap = bitmap;
        updateMatrix();
    }

    /**
     * 设置旋转图
     * @param drawable
     */
    public void setImageDrawable(Drawable drawable)
    {
        if(drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            _bitmap = bd.getBitmap();

            updateMatrix();
        }else
        {
            throw new NotSupportedException("SingleTouchView not support this Drawable " + drawable);
        }
    }

    /**
     * 根据id设置旋转图
     * @param resId
     */
    public void setImageResource(int resId)
    {
        Drawable drawable = getContext().getResources().getDrawable(resId);
        setImageDrawable(drawable);
    }

    public boolean isEditable()
    {
        return _isEditable;
    }

    public void setEditable(boolean isEdt)
    {
        if (_isEditable != isEdt)
        {
            _isEditable = isEdt;
            invalidate();
        }
    }

    public boolean isInRect(float x, float y)
    {
        RectF r = new RectF(getLeft(),getTop(),getRight(),getBottom());
        return r.contains(x,y);
    }

    public boolean onDrawContent(Canvas canvas)
    {
        if(_bitmap == null) return false;
        canvas.drawBitmap(_bitmap, _matrix, null);
        return true;
    }

    protected void onDrawFrameAndCtrl(Canvas canvas)
    {
        //处于可编辑状态才画边框和控制图标
        if(_isEditable)
        {
            _framePath.reset();
            _framePath.moveTo(_leftTopPoint.x, _leftTopPoint.y);
            _framePath.lineTo(_rightTopPoint.x, _rightTopPoint.y);
            _framePath.lineTo(_rightBottomPoint.x, _rightBottomPoint.y);
            _framePath.lineTo(_leftBottomPoint.x, _leftBottomPoint.y);
            _framePath.lineTo(_leftTopPoint.x, _leftTopPoint.y);
            _framePath.lineTo(_rightTopPoint.x, _rightTopPoint.y);
            canvas.drawPath(_framePath, _framePaint);
            //画旋转, 缩放图标
            _ctrlScaleDrawable.setBounds(_rightBottomPoint.x - _scaleDrawableWidth / 2,
                    _rightBottomPoint.y - _scaleDrawableHeight / 2, _rightBottomPoint.x + _scaleDrawableWidth
                            / 2, _rightBottomPoint.y + _scaleDrawableHeight / 2);
            _ctrlScaleDrawable.draw(canvas);
            //
            _ctrlCloseDrawable.setBounds(_leftTopPoint.x - _closeDrawableWidht / 2, _leftTopPoint.y - _closeDrawableHeight / 2,
                    _leftTopPoint.x + _closeDrawableWidht / 2, _leftTopPoint.y + _closeDrawableHeight / 2);
            _ctrlCloseDrawable.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        adjustLayout();

        super.onDraw(canvas);

        if (!onDrawContent(canvas))return;

        onDrawFrameAndCtrl(canvas);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if(!_isEditable)
        {
            return super.onTouchEvent(event);
        }
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                _preMove.set(event.getX() + _viewPaddingLeft, event.getY() + _viewPaddingTop);
                _status = JudgeStatus(event.getX(), event.getY());
                if (_status == TVState.remove)
                {
                    ((ViewGroup)getParent()).removeView(this);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                _status = TVState.init;
                break;
            case MotionEvent.ACTION_MOVE:
                _curMove.set(event.getX() + _viewPaddingLeft, event.getY() + _viewPaddingTop);
                if (_status == TVState.zoom)
                {
                    float scale = 1f;

                    int halfBitmapWidth = _bitmap.getWidth() / 2;
                    int halfBitmapHeight = _bitmap.getHeight() /2 ;

                    //图片某个点到图片中心的距离
                    float bitmapToCenterDistance = (float)Math.sqrt(halfBitmapWidth * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);

                    //移动的点到图片中心的距离
                    float moveToCenterDistance = distance4PointF(_parentCenter, _curMove);

                    //计算缩放比例
                    scale = moveToCenterDistance / bitmapToCenterDistance;


                    //缩放比例的界限判断
                    if (scale <= MIN_SCALE)
                    {
                        scale = MIN_SCALE;
                    } else if (scale >= MAX_SCALE)
                    {
                        scale = MAX_SCALE;
                    }

                    // 角度
                    double a = distance4PointF(_parentCenter, _preMove);
                    double b = distance4PointF(_preMove, _curMove);
                    double c = distance4PointF(_parentCenter, _curMove);

                    double cosb = (a * a + c * c - b * b) / (2 * a * c);

                    if (cosb >= 1)
                    {
                        cosb = 1f;
                    }

                    double radian = Math.acos(cosb);
                    float newDegree = (float) radianToDegree(radian);

                    //center -> proMove的向量， 我们使用PointF来实现
                    PointF centerToProMove = new PointF((_preMove.x - _parentCenter.x), (_preMove.y - _parentCenter.y));

                    //center -> curMove 的向量
                    PointF centerToCurMove = new PointF((_curMove.x - _parentCenter.x), (_curMove.y - _parentCenter.y));

                    //向量叉乘结果, 如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
                    float result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x;

                    if (result < 0)
                    {
                        newDegree = -newDegree;
                    }

                    _degree = _degree + newDegree;
                    _scale = scale;

                    updateMatrix();
                }
                else if (_status == TVState.drag)
                {
                    // 修改中心点
                    _parentCenter.x += _curMove.x - _preMove.x;
                    _parentCenter.y += _curMove.y - _preMove.y;

                    adjustLayout();
                }

                _preMove.set(_curMove);
                break;
        }
        return true;
    }

    /**
     * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
     * @param x
     * @param y
     * @return
     */
    protected TVState JudgeStatus(float x, float y)
    {
        PointF touchPoint = new PointF(x, y);
        PointF controlPointF = new PointF(_rightBottomPoint);
        //点击的点到控制旋转，缩放点的距离
        float distanceToControl = distance4PointF(touchPoint, controlPointF);

        //如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
        if(distanceToControl < Math.min(_scaleDrawableWidth / 2, _scaleDrawableHeight / 2))
        {
            return TVState.zoom;
        }else
        {
            controlPointF.set(_leftTopPoint.x, _leftTopPoint.y);
            distanceToControl = distance4PointF(touchPoint, controlPointF);
            if (distanceToControl < Math.min(_closeDrawableWidht / 2, _closeDrawableHeight / 2))
            {
                return TVState.remove;
            }
            return TVState.drag;
        }
    }

    /**
     * 两个点之间的距离
     * @param pf1
     * @param pf2
     * @return
     */
    public static float distance4PointF(PointF pf1, PointF pf2)
    {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        double value = (disX * disX + disY * disY);
        return (float)Math.sqrt(value);
    }

    /**
     * 弧度换算成角度
     * @return
     */
    protected double radianToDegree(double radian)
    {
        return radian * 180 / Math.PI;
    }

    /**
     * 角度换算成弧度
     * @param degree
     * @return
     */
    protected double degreeToRadian(double degree)
    {
        return degree * Math.PI / 180;
    }

    /**
     * 获取变长参数最大的值
     * @param array
     * @return
     */
    public int getMaxValue(Integer...array)
    {
        List<Integer> list = Arrays.asList(array);
        Collections.sort(list);
        return list.get(list.size() -1);
    }


    /**
     * 获取变长参数最大的值
     * @param array
     * @return
     */
    public int getMinValue(Integer...array)
    {
        List<Integer> list = Arrays.asList(array);
        Collections.sort(list);
        return list.get(0);
    }

    protected Bitmap drawable2Bitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable)
        {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else
        {
            return null;
        }
    }

    @SuppressWarnings("serial")
    public static class NotSupportedException extends RuntimeException
    {
        private static final long serialVersionUID = 1674773263868453754L;

        public NotSupportedException()
        {
            super();
        }

        public NotSupportedException(String detailMessage)
        {
            super(detailMessage);
        }

    }
}
