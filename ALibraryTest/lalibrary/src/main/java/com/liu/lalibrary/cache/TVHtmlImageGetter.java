package com.liu.lalibrary.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liu.lalibrary.utils.ScreenUtils;

import java.io.File;

/**
 * Created by liu on 2017/10/20.
 */

public class TVHtmlImageGetter implements Html.ImageGetter, ICache.OnCacheListener
{
    private TextView container;
    private ICache cacheInst;
    private UrlDrawable drawable;

    public TVHtmlImageGetter(TextView tv, ICache cacheInst)
    {
        this.container = tv;
        this.cacheInst = cacheInst;
    }

    @Override
    public Drawable getDrawable(String source)
    {
        drawable = new UrlDrawable();
        if (source.startsWith("http"))
        {
            cacheInst.load(source, this);
        }
        return drawable;
    }

    @Override
    public void onCacheComplete(Object obj)
    {
        if (obj != null && drawable.bitmap == null)
        {
            drawable.bitmap = BitmapFactory.decodeFile(((File) obj).getAbsolutePath());
            int destW = ScreenUtils.getScreenWidth(container.getContext());
            int destH = (int)(((float)destW / drawable.bitmap.getWidth()) * drawable.bitmap.getHeight());
            drawable.setBounds(0, 0, destW, destH);
        }
        drawable.invalidateSelf();
        container.setText(container.getText());
    }

    public class UrlDrawable extends BitmapDrawable
    {
        public Bitmap bitmap;

        @Override
        public void draw(Canvas canvas)
        {
            if (bitmap != null)
            {
                canvas.drawBitmap(bitmap, new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),
                                  new Rect(0,0,getBounds().width(),getBounds().height()), null);
            }
        }
    }
}
