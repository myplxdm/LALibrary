package com.liu.lalibrary.camera.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by liu on 2016/12/30.
 */

public class BgCanvaLayout extends ViewGroup
{
    private final float RESERVE_SPACE_RATION = 1.0f;

    private ImageViewEx     iv_image;
    private Bitmap          image;
    //
    private int             parentWidth;
    private int             parentHeight;
    //
    private boolean         isFill;
    private float           canvaRation;
    private boolean         isEditable = true;
    private boolean         isDirectSave;
    private boolean         isInitIVRect;
    private boolean         isSelfRect;

    public BgCanvaLayout(Context context)
    {
        this(context, null, 0);
    }

    public BgCanvaLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BgCanvaLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        calcCanvaSize();
        if (!isInitIVRect && image != null)
        {
            calcImageViewSize();
            isInitIVRect = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup)
        {
            parentWidth = mViewGroup.getWidth();
            parentHeight = mViewGroup.getHeight();
            if (parentWidth == 0 || parentHeight == 0)return;
            calcCanvaSize();
            calcImageViewSize();
        }
    }

    private void getParentSize()
    {
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup)
        {
            parentWidth = mViewGroup.getWidth();
            parentHeight = mViewGroup.getHeight();
        }
    }

    private void init(Context context)
    {
        isFill = true;
        canvaRation = 1;
        iv_image = new ImageViewEx(context);
        addView(iv_image, new LayoutParams(0,0));
    }

    private void calcCanvaSize()
    {
        if (parentWidth == 0 || parentHeight == 0)
        {
            getParentSize();
        }
        int w = parentWidth,h = parentHeight;
        if (canvaRation == 1)
        {
            w = h = parentWidth;
        }else if (canvaRation < 1)
        {
            w = (int)(h * canvaRation);
        }else
        {
            h = (int)(w / canvaRation);
        }
        int x = (parentWidth - w) / 2;
        int y = (parentHeight - h) / 2;
        setLeft(x);
        setRight(x + w);
        setTop(y);
        setBottom(y + h);
    }

    private void calcFillImageView(int w, int h)
    {
        boolean isHor = w > h;
        if (isHor) //hor
        {
            isDirectSave = h <= getHeight();
            w = (int)(getHeight() * 1.0f / h * w);
            iv_image.layout(0, 0, w, getHeight());
        }else
        {
            isDirectSave = w <= getWidth();
            h = (int)(getWidth() * 1.0f / w * h);
            iv_image.layout(0, 0, getWidth(), h);
        }
    }

    private void calcSpaceImageView(int w, int h)
    {
        isDirectSave = w < getWidth() || h < getHeight();
        boolean isHor = w >= h;
        float r = w * 1.0f / h;
        int l,t;
        if (!isDirectSave)
        {
            if (isHor)
            {
                w = (int)(getWidth() * iv_image.getScale());
                h = (int)(w / r);
            }else
            {
                h = (int)(getHeight() * iv_image.getScale());
                w = (int)(h * r);
            }
        }
        l = (getWidth() - w) / 2;
        t = (getHeight() - h) / 2;
        iv_image.layout(l, t, l + w, t + h);
    }

    private void calcImageViewSize()
    {
        if (image == null || getWidth() == 0) return;
        if (isFill)
        {
            calcFillImageView(image.getWidth(), image.getHeight());
            return;
        }
        calcSpaceImageView(image.getWidth(), image.getHeight());
    }

    private void calcSize()
    {
        calcCanvaSize();
        calcImageViewSize();
    }

    public void setRation(float whRatio)
    {
        canvaRation = whRatio;
    }

    public void setFill(boolean isFill)
    {
        this.isFill = isFill;
        iv_image.setLimitInParent(isFill);
        calcImageViewSize();
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
        iv_image.setImageBitmap(image);
        requestLayout();
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setEditable(boolean isEdt)
    {
        if (isEditable != isEdt)
        {
            isEditable = isEdt;
            iv_image.setEditable(isEdt);
        }
    }

    /******************  export fun  *************************/
    private Bitmap exportDirect()
    {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    private Bitmap exportFillImage()
    {
        if (isDirectSave) return exportDirect();

        float xr = Math.abs(iv_image.getLeft()) / iv_image.getWidth();
        float yr = Math.abs(iv_image.getTop()) / iv_image.getHeight();
        boolean isHor = image.getWidth() >= image.getHeight();
        int x,y,w,h;
        if (isHor)
        {
            h = image.getHeight();
            w = h * getWidth() / getHeight();
            x = (int)(xr * image.getWidth());
            y = 0;
        }else
        {
            w = image.getWidth();
            h = w * getHeight() / getWidth();
            y = (int)(yr * image.getHeight());
            x = 0;
        }
        return Bitmap.createBitmap(image,x,y,w,h);
    }

    private int[] calcRationOutSize()
    {
        boolean isHor = image.getWidth() >= image.getHeight();
        int w,h;
        if (canvaRation == 1)
        {
            w = h = (isHor ? image.getWidth() : image.getHeight());
        }else
        {
            if (isHor)
            {
                w = image.getWidth();
                h = (int)(w / canvaRation);
            }else
            {
                h = image.getHeight();
                w = (int)(h * canvaRation);
            }
        }
        return new int[]{w,h};
    }

    private Bitmap exportSpaceImage()
    {
        if (isDirectSave) return exportDirect();
        int[] wh = calcRationOutSize();
        float xr = iv_image.getLeft() * 1.0f / getWidth();
        float yr = iv_image.getTop() * 1.0f / getHeight();
        int x = (int)(xr * wh[0]);
        int y = (int)(yr * wh[1]);
        int w = (int)(image.getWidth() * iv_image.getScale());
        int h = (int)(image.getHeight() * iv_image.getScale());
        Bitmap newImage = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(newImage);
        c.drawColor(((ColorDrawable)getBackground()).getColor());
        Rect dest = new Rect(x, y, x + w, y + h);
        c.drawBitmap(image, null, dest, null);
        return newImage;
    }

    public Bitmap exportImage()
    {
        if (isFill) return exportFillImage();
        return exportSpaceImage();
    }
}
