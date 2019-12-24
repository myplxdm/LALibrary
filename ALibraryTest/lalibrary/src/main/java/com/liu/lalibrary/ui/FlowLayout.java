package com.liu.lalibrary.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.liu.lalibrary.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pinger on 2016/10/6.
 */

public class FlowLayout extends ViewGroup implements View.OnClickListener
{
    public interface FlowItemListener
    {
        public void onFlowSelItem(View child, boolean isSel, int index);

    }

    /**
     * 存储行的集合，管理行
     */
    private List<Line> mLines = new ArrayList<>();

    /**
     * 水平和竖直的间距
     */
    private float vertical_space;
    private float horizontal_space;

    // 当前行的指针
    private Line mCurrentLine;

    // 行的最大宽度，除去边距的宽度
    private int mMaxWidth;

    // 状态
    private HashMap<View, Integer> stateList = new HashMap<>(50);
    private boolean isMultilSel;
    private int curSelIndex = -1;
    private FlowItemListener listener;
    private int selCount = 0;
    //
    private boolean useMinWidth;//开启后整个组件宽度为一行最大宽度

    public FlowLayout(Context context)
    {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // 获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontal_space = AutoUtils.getPercentHeightSize(array.getDimensionPixelSize(R.styleable.FlowLayout_hor_space, 0));
        vertical_space = AutoUtils.getPercentHeightSize(array.getDimensionPixelSize(R.styleable.FlowLayout_ver_space, 0));
        useMinWidth = array.getBoolean(R.styleable.FlowLayout_useMinWidth, false);
        isMultilSel = array.getBoolean(R.styleable.FlowLayout_multilSel, false);
        array.recycle();
    }

    @Override
    public void addView(View child, LayoutParams params)
    {
        addView(child, params, false);
    }

    @Override
    public void addView(View child)
    {
        addView(child, null, false);
    }

    public void addView(View child, LayoutParams params, boolean isSel)
    {
        if (params != null)
        {
            super.addView(child, params);
        }else
        {
            super.addView(child);
        }
        stateList.put(child, isSel ? 1 : 0);
        child.setOnClickListener(this);
        AutoUtils.auto(child);
        if (listener != null) listener.onFlowSelItem(child, isSel, getChildCount() - 1);
    }

    public void addView(View child, boolean isSel)
    {
        addView(child, null, isSel);
        if (listener != null) listener.onFlowSelItem(child, isSel, getChildCount() - 1);
    }

    @Override
    public void removeView(View view)
    {
        super.removeView(view);
        stateList.remove(view);
    }

    @Override
    public void removeViewAt(int index)
    {
        View view = getChildAt(index);
        stateList.remove(view);
        super.removeViewAt(index);
    }

    @Override
    public void removeAllViews()
    {
        super.removeAllViews();
        stateList.clear();
    }

    public void setFlowItemListener(FlowItemListener listener)
    {
        this.listener = listener;
    }

    public void setMultilSel(boolean isMultilSel)
    {
        this.isMultilSel = isMultilSel;
    }

    public ArrayList<Integer> getSelectList()
    {
        if (selCount == 0)return null;
        ArrayList<Integer> list = new ArrayList<>(selCount);
        for (int i = 0;i < getChildCount();i++)
        {
            if (stateList.get(i) == 1)
            {
                list.add(i);
            }
        }
        return list;
    }

    @Override
    public void onClick(View view)
    {
        int index = indexOfChild(view);
        if (!isMultilSel)
        {
            int newSel = curSelIndex;
            if (curSelIndex != -1)
            {
                if (listener != null) listener.onFlowSelItem(getChildAt(curSelIndex), false, curSelIndex);
                stateList.put(view, 0);
                newSel = -1;
                selCount = 0;
            }
            if (curSelIndex != index)
            {
                if (listener != null) listener.onFlowSelItem(view, true, index);
                stateList.put(view, 1);
                newSel = index;
                selCount = 1;
            }
            curSelIndex = newSel;
        } else
        {
            boolean isSel = stateList.get(view) == 1;
            if (listener != null) listener.onFlowSelItem(view, !isSel, index);
            stateList.put(view, isSel ? 0 : 1);
            selCount += !isSel ? 1 : -1;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 每次测量之前都先清空集合，不让会覆盖掉以前
        mLines.clear();
        mCurrentLine = null;

        // 获取总宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 计算最大的宽度
        mMaxWidth = width - getPaddingLeft() - getPaddingRight();

        // ******************** 测量孩子 ********************
        // 遍历获取孩子
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View childView = getChildAt(i);
            // 测量孩子
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            // 测量完需要将孩子添加到管理行的孩子的集合中，将行添加到管理行的集合中

            if (mCurrentLine == null)
            {
                // 初次添加第一个孩子的时候
                mCurrentLine = new Line(mMaxWidth, horizontal_space);

                // 添加孩子
                mCurrentLine.addView(childView);
                // 添加行
                mLines.add(mCurrentLine);

            } else
            {
                // 行中有孩子的时候，判断时候能添加
                if (mCurrentLine.canAddView(childView))
                {
                    // 继续往该行里添加
                    mCurrentLine.addView(childView);
                } else
                {
                    //  添加到下一行
                    mCurrentLine = new Line(mMaxWidth, horizontal_space);
                    mCurrentLine.addView(childView);
                    mLines.add(mCurrentLine);
                }
            }
        }

        // ******************** 测量自己 *********************
        // 测量自己只需要计算高度，宽度肯定会被填充满的
        int maxLineWidth = 0;
        int height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mLines.size(); i++)
        {
            // 所有行的高度
            height += mLines.get(i).height;
            maxLineWidth = Math.max(mLines.get(i).usedWidth, maxLineWidth);
        }
        // 所有竖直的间距
        height += (mLines.size() - 1) * vertical_space;

        // 测量
        setMeasuredDimension(useMinWidth ? maxLineWidth : width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        // 这里只负责高度的位置，具体的宽度和子孩子的位置让具体的行去管理
        l = getPaddingLeft();
        t = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++)
        {
            // 获取行
            Line line = mLines.get(i);
            // 管理
            line.layout(t, l);

            // 更新高度
            t += line.height;
            if (i != mLines.size() - 1)
            {
                // 不是最后一条就添加间距
                t += vertical_space;
            }
        }
    }

    /**
     * 内部类，行管理器，管理每一行的孩子
     */
    public class Line
    {
        // 定义一个行的集合来存放子View
        private List<View> views = new ArrayList<>();
        // 行的最大宽度
        private int maxWidth;
        // 行中已经使用的宽度
        private int usedWidth;
        // 行的高度
        private int height;
        // 孩子之间的距离
        private float space;

        // 通过构造初始化最大宽度和边距
        public Line(int maxWidth, float horizontalSpace)
        {
            this.maxWidth = maxWidth;
            this.space = horizontalSpace;
        }

        /**
         * 往集合里添加孩子
         */
        public void addView(View view)
        {
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            // 更新行的使用宽度和高度
            if (views.size() == 0)
            {
                // 集合里没有孩子的时候
                if (childWidth > maxWidth)
                {
                    usedWidth = maxWidth;
                    height = childHeight;
                } else
                {
                    usedWidth = childWidth;
                    height = childHeight;
                }
            } else
            {
                usedWidth += childWidth + space;
                height = childHeight > height ? childHeight : height;
            }

            // 添加孩子到集合
            views.add(view);
        }


        /**
         * 判断当前的行是否能添加孩子
         *
         * @return
         */
        public boolean canAddView(View view)
        {
            // 集合里没有数据可以添加
            if (views.size() == 0)
            {
                return true;
            }

            // 最后一个孩子的宽度大于剩余宽度就不添加
            if (view.getMeasuredWidth() > (maxWidth - usedWidth - space))
            {
                return false;
            }

            // 默认可以添加
            return true;
        }

        /**
         * 指定孩子显示的位置
         *
         * @param t
         * @param l
         */
        public void layout(int t, int l)
        {
            // 平分剩下的空间
            int avg = (maxWidth - usedWidth) / views.size();

            // 循环指定孩子位置
            for (View view : views)
            {
                // 获取宽高
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                // 重新测量
                //view.measure(MeasureSpec.makeMeasureSpec(measuredWidth + avg, MeasureSpec.EXACTLY),
                //        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                // 重新获取宽度值
                //measuredWidth = view.getMeasuredWidth();


                int top = t;
                int left = l;
                int right = measuredWidth + left;
                int bottom = measuredHeight + top;
                // 指定位置
                view.layout(left, top, right, bottom);

                // 更新数据
                l += measuredWidth + space;
            }
        }
    }
}
