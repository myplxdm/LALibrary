package com.liu.lalibrary.utils.imagecache;//package org.liu.library.utils.imagecache;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.support.v4.util.LruCache;
//import android.text.TextUtils;
//import android.widget.ImageView;
//
//import org.liu.library.log.LogUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.ref.SoftReference;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.WeakHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class ImageLoader
//{
//	private final String 						TAG 				= "ImageLoader";
//	private HashMap<Object, String> 			faceCache   		= new HashMap<Object, String>(200);
//	private LruCache<String, Bitmap>			memoryCache;
//	private Map<String, SoftReference<Bitmap>>	softCache			= Collections
//																			.synchronizedMap(new LinkedHashMap<String, SoftReference<Bitmap>>(
//																					10,
//																					1.5f,
//																					true));
//	private FileCache							fileCache;
//	private Map<ImageView, String>				imageViews			= Collections
//																			.synchronizedMap(new WeakHashMap<ImageView, String>());
//	private ExecutorService						localService;
//	private ExecutorService						netService;
//	private ImageLoadEvent						imageLoadEvent;
//
//	private int									mLimitImageWidth	= 300;
//	private int									mLimitImageHeight	= 300;
//	private int									mScreenWidth;
//	private int									mScreenHeight;
//	private final int							MEM_CACHE_SIZE		= 1024 * 1024 * 8;
//	//
//	private static String						mCachePath;
//	private static ImageLoader					inst;
//
//	public enum ImageScaleOpt
//	{
//		opt_exact, 		// 精确规定尺寸
//		opt_shrink_wh, 	// 按图片高宽缩小
//		opt_shrink_w,	// 按图片宽度缩小
//		opt_fix_zoom_w, // 根据图片宽度缩小或放大图片
//		opt_anyone_wh, // 宽度或高度任一个到达规定尺寸
//	}
//
//	public interface ImageLoadEvent
//	{
//		public void onSuccess(String imgSrc, Bitmap bmp);
//		public void onFail(String imgSrc);
//	}
//
//	public interface ReqImageUrlCallback
//	{
//		public String getImageUrl(Object param);
//	}
//
//	public static ImageLoader init(Context c, String cachePath,
//			ImageLoadEvent ile)
//	{
//		if (inst == null)
//		{
//			mCachePath = cachePath;
//			inst = new ImageLoader(c, ile);
//		}
//		return inst;
//	}
//
//	public static ImageLoader getInst()
//	{
//		return inst;
//	}
//
//	public void setImageLoadEvent(ImageLoadEvent ile)
//	{
//		imageLoadEvent = ile;
//	}
//
//	public void setLimitImageSize(int width, int height)
//	{
//		mLimitImageWidth = width == 0 ? mScreenWidth : width;
//		mLimitImageHeight = height == 0 ? mScreenHeight : height;
//	}
//
//	public void setFaceUrl(Object req, String url)
//	{
//		faceCache.put(req, url);
//	}
//
//	public String getFaceUrl(Object req)
//	{
//		String url = faceCache.get(req);
//		return url == null ? "" : url;
//	}
//
//	public boolean DisplayImage(String url)
//	{
//		return DisplayImage(url, null, null, mLimitImageWidth,
//				mLimitImageHeight, ImageScaleOpt.opt_anyone_wh, false, null, null, false);
//	}
//
//	public boolean DisplayImage(String url, ImageView imageView)
//	{
//		return DisplayImage(url, null, imageView, mLimitImageWidth,
//				mLimitImageHeight, ImageScaleOpt.opt_anyone_wh, false, null, null, false);
//	}
//
//	public boolean DisplayImage(Object req, ReqImageUrlCallback riucb, ImageView imageView, boolean forceUpdate)
//	{
//		return DisplayImage(null, null, imageView, mLimitImageWidth,
//				mLimitImageHeight, ImageScaleOpt.opt_anyone_wh, false, req, riucb, forceUpdate);
//	}
//
//	public boolean DisplayImage(String url, String key, ImageView imageView, int limitWidth, int limitHeight, ImageScaleOpt opt)
//	{
//		return DisplayImage(url, key, imageView, limitWidth,
//				limitHeight, opt, false, null, null, false);
//	}
//
//	public boolean DisplayImage(String url, ImageView imageView,
//			int limitWidth, int limitHeight, boolean isLoadOnlyFromCache)
//	{
//		return DisplayImage(url, null, imageView, limitWidth,
//				limitHeight, ImageScaleOpt.opt_anyone_wh, isLoadOnlyFromCache, null, null, false);
//	}
//
//	public void clear()
//	{
//		memoryCache.evictAll();
//		Iterator<Entry<String, SoftReference<Bitmap>>> it = softCache.entrySet().iterator();
//		Bitmap bmp;
//	    while (it.hasNext())
//	    {
//	    	Entry<String, SoftReference<Bitmap>> entry = it.next();
//	        bmp = entry.getValue().get();
//			if (bmp != null && !bmp.isRecycled())
//			{
//				bmp.recycle();
//			}
//	    }
//	    softCache.clear();
//	    fileCache.clear();
//	}
//
//	/**
//	 *
//	 * @param url
//	 * @param imageView
//	 * @param isLoadOnlyFromCache
//	 * @param limitWidth
//	 * @param limitHeight
//	 * @param opt
//	 * @return true 涓哄�戒腑缂�瀛�
//	 */
//	public boolean DisplayImage(String url, String key, ImageView imageView,
//			int limitWidth, int limitHeight,
//			ImageScaleOpt opt, boolean isLoadOnlyFromCache,
//			Object req, ReqImageUrlCallback riucb, boolean forceUpdate)
//	{
//		limitWidth = limitWidth == 0 ? mScreenWidth : limitWidth;
//		limitHeight = limitHeight == 0 ? mScreenHeight : limitHeight;
//
//		if (req != null)
//		{
//			url = faceCache.get(req);
//			if (TextUtils.isEmpty(url) || forceUpdate)
//			{
//				imageViews.put(imageView, req.toString());
//				PhotoToLoad p = new PhotoToLoad(url, req.toString(), imageView, limitWidth, limitHeight, opt, false, req, riucb);
//				netService.submit(new PhotosLoader(p));
//				return false;
//			}
//		}
//
//		if (key == null)
//		{
//			key = url + limitWidth + limitHeight;
//		}
//
//		String oldUrl = imageViews.put(imageView, key);
//		Bitmap bitmap = getMemoryCache(key);
//
//		if (bitmap != null)
//		{
//			if (imageView != null)
//			{
//				imageView.setImageBitmap(bitmap);
//			} else if (imageLoadEvent != null)
//			{
//				imageLoadEvent.onSuccess(key, bitmap);
//			}
//			return true;
//		} else if (!isLoadOnlyFromCache
//				&& (oldUrl == null || !oldUrl.equals(url)))
//		{
//			boolean bLoacl = !url.startsWith("http");
//			PhotoToLoad p = new PhotoToLoad(url, key, imageView, limitWidth, limitHeight, opt, bLoacl, null, null);
//			if (bLoacl)
//			{
//				localService.submit(new PhotosLoader(p));
//			} else
//			{
//				netService.submit(new PhotosLoader(p));
//			}
//		}
//		return false;
//	}
//
//	public ImageLoader(Context context, ImageLoadEvent ile)
//	{
//		imageLoadEvent = ile;
//		memoryCache = new LruCache<String, Bitmap>(MEM_CACHE_SIZE)
//		{
//			protected void entryRemoved(boolean evicted, String key,
//					Bitmap oldValue, Bitmap newValue)
//			{
//				softCache.put(key, new SoftReference<Bitmap>(oldValue));
//			};
//
//			protected int sizeOf(String key, Bitmap value)
//			{
//				return value.getRowBytes() * value.getHeight();
//			};
//		};
//		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
//		mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
//
//		fileCache = new FileCache(context, mCachePath);
//		localService = Executors.newFixedThreadPool(Runtime.getRuntime()
//				.availableProcessors() * 2);
//		netService = Executors.newFixedThreadPool(1);
//	}
//
//	private Bitmap getMemoryCache(String key)
//	{
//		Bitmap bmp = memoryCache.get(key);
//		if (bmp == null)
//		{
//			SoftReference<Bitmap> bmpRef = softCache.get(key);
//			if (bmpRef != null)
//			{
//				bmp = bmpRef.get();
//				if (bmp == null)
//				{
//					softCache.remove(key);
//				}
//			}
//		}
//		return bmp;
//	}
//
//	private boolean imageViewReused(PhotoToLoad photoToLoad)
//	{
//		if (photoToLoad.imageView != null)
//		{
//			String tag = imageViews.get(photoToLoad.imageView);
//			if (tag == null || !tag.equals(photoToLoad.key))
//				return true;
//		}
//		return false;
//	}
//
//	private Bitmap getBitmap(String url, int limitWidth, int limitHeight,
//			ImageScaleOpt opt)
//	{
//
//		File f = fileCache.getFile(url);
//		Bitmap b = null;
//		if (f != null && f.exists())
//		{
//			b = decodeFile(f, limitWidth, limitHeight, opt);
//		}
//		if (b != null)
//		{
//			return b;
//		}
//		try
//		{
//			Bitmap bitmap = null;
//			URL imageUrl = new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.setConnectTimeout(30000);
//			conn.setReadTimeout(30000);
//			conn.setInstanceFollowRedirects(true);
//			InputStream is = conn.getInputStream();
//			OutputStream os = new FileOutputStream(f);
//			int filesize = copyStream(is, os);
//			os.close();
//			bitmap = decodeFile(f, limitWidth, limitHeight, opt);
//			if (filesize > 0)
//			{
//				fileCache.put(f.getAbsolutePath(), filesize);
//			}
//			return bitmap;
//		} catch (Exception ex)
//		{
//			LogUtils.LOGE(ImageLoader.class, TAG, "getBitmap catch Exception...\nmessage = "
//					+ ex.getMessage());
//		}
//		return null;
//	}
//
//	private int copyStream(InputStream is, OutputStream os)
//	{
//		final int buffer_size = 1024;
//		int filesize = 0;
//		try
//		{
//			byte[] bytes = new byte[buffer_size];
//			for (;;)
//			{
//				int count = is.read(bytes, 0, buffer_size);
//				if (count == -1)
//					break;
//				os.write(bytes, 0, count);
//				filesize += count;
//			}
//		} catch (Exception ex)
//		{
//			LogUtils.LOGE(ImageLoader.class, TAG, "CopyStream catch Exception...");
//		}
//		return filesize;
//	}
//
//	private Bitmap decodeFile(File f, int limitWidth, int limitHeight,
//			ImageScaleOpt scaleOpt)
//	{
//		try
//		{
//			Bitmap b = null;
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//
//			int width_tmp = o.outWidth, height_tmp = o.outHeight;
//			int scale = 1;
//			switch (scaleOpt)
//			{
//			case opt_exact:
//			case opt_anyone_wh:
//				while (true)
//				{
//					if (width_tmp / 2 < limitWidth
//							|| height_tmp / 2 < limitHeight)
//						break;
//					width_tmp /= 2;
//					height_tmp /= 2;
//					scale *= 2;
//				}
//				break;
//			case opt_shrink_wh:
//				while (true)
//				{
//					if (width_tmp / 2 < limitWidth
//							&& height_tmp / 2 < limitHeight)
//						break;
//					width_tmp /= 2;
//					height_tmp /= 2;
//					scale *= 2;
//				}
//				break;
//			case opt_shrink_w:
//			case opt_fix_zoom_w:
//				while (true)
//				{
//					if (width_tmp / 2 < limitWidth)
//						break;
//					width_tmp /= 2;
//					height_tmp /= 2;
//					scale *= 2;
//				}
//				break;
//			}
//			o.inInputShareable = true;
//			o.inPurgeable = true;
//			o.inJustDecodeBounds = false;
//			o.inSampleSize = scale;
//			b = BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//			if (scaleOpt == ImageScaleOpt.opt_fix_zoom_w && width_tmp != limitWidth)
//			{
//				float sx = (float)limitWidth / (float)b.getWidth();
//				Matrix matrix = new Matrix();
//				matrix.postScale(sx, sx);
//				Bitmap bmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
//				b.recycle();
//				b = bmp;
//			}else if (scaleOpt == ImageScaleOpt.opt_exact)
//			{
//				Bitmap bmp = ImageTools.zoomBitmap(b, limitWidth, limitHeight);
//				b.recycle();
//				b = bmp;
//			}
//			return b;
//		} catch (FileNotFoundException e)
//		{
//		}
//		return null;
//	}
//
//	private class PhotoToLoad
//	{
//		public Object				req;
//		public ReqImageUrlCallback riucb;
//		public String				url;
//		public String 				key;
//		public ImageView			imageView;
//		public int					limitWidth;
//		public int					limitHeight;
//		public ImageScaleOpt		opt;
//		public boolean				bLocal;
//
//		public PhotoToLoad(String url, String key, ImageView i, int limitWidth,
//				int limitHeight, ImageScaleOpt opt, boolean bLocal, Object req, ReqImageUrlCallback riucb)
//		{
//			this.url = url;
//			this.key = key;
//			imageView = i;
//			this.opt = opt;
//			this.bLocal = bLocal;
//			this.limitWidth = limitWidth;
//			this.limitHeight = limitHeight;
//			this.req = req;
//			this.riucb = riucb;
//		}
//	}
//
//	class PhotosLoader implements Runnable
//	{
//		PhotoToLoad	photoToLoad;
//
//		PhotosLoader(PhotoToLoad photoToLoad)
//		{
//			this.photoToLoad = photoToLoad;
//		}
//
//		@Override
//		public void run()
//		{
//			if (imageViewReused(photoToLoad))
//				return;
//			Bitmap bmp = null;
//			if (photoToLoad.req != null)
//			{
//				photoToLoad.url = photoToLoad.riucb.getImageUrl(photoToLoad.req);
//				if (TextUtils.isEmpty(photoToLoad.url))
//				{
//					return;
//				}
//				faceCache.put(photoToLoad.req, photoToLoad.url);
//				if (imageViewReused(photoToLoad))
//					return;
//				photoToLoad.key = photoToLoad.url + photoToLoad.limitWidth + photoToLoad.limitHeight;
//				imageViews.put(photoToLoad.imageView, photoToLoad.key);
//				bmp = memoryCache.get(photoToLoad.key);
//			}
//			if (photoToLoad.bLocal)
//			{
//				File file = new File(photoToLoad.url);
//				if (file.exists() && file.isFile())
//				{
//					bmp = decodeFile(new File(photoToLoad.url),
//							photoToLoad.limitWidth, photoToLoad.limitHeight,
//							photoToLoad.opt);
//				}
//			} else if (bmp == null)
//			{
//				bmp = getBitmap(photoToLoad.url, photoToLoad.limitWidth,
//						photoToLoad.limitHeight, photoToLoad.opt);
//				if (bmp != null)
//				{
//					memoryCache.put(photoToLoad.key, bmp);
//				}
//			}
//
//			if (imageViewReused(photoToLoad))
//				return;
//			if (photoToLoad.imageView == null)
//			{
//				if (bmp != null)
//				{
//					imageLoadEvent.onSuccess(photoToLoad.key, bmp);
//				} else
//				{
//					imageLoadEvent.onFail(photoToLoad.key);
//				}
//			} else if (bmp != null)
//			{
//				final Bitmap bmpf = bmp;
//				Activity a = (Activity) photoToLoad.imageView.getContext();
//				a.runOnUiThread(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if (imageViewReused(photoToLoad))
//							return;
//						if (bmpf.isRecycled() == false)
//							photoToLoad.imageView.setImageBitmap(bmpf);
//					}
//				});
//			}
//		}
//	}
//}
