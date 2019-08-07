package com.liu.lalibrary.ui.webview;

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewEx extends WebView
{
	private String ERR_URL	= "file:///android_asset/error.html";
	
	public interface WebViewEvent
	{
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

		public void onPageFinished(WebView view, String url);

		public boolean shouldOverrideUrlLoading(WebView view, String url);
	}

	private WebViewClient	client;
	//private int				loadUrlTimeout;
	private WebViewEvent	webEvent;
	private Context			mContext;
	private boolean 		err;
	private String			lastUrl;
	private String 			selfLoadUrl;
	private boolean			isErrRedirect;

	public WebViewEx(Context context)
	{
		super(context);
		setup(context);
	}

	public WebViewEx(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setup(context);
	}

	public WebViewEx(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup(context);
	}

	@Override
	public void loadUrl(String url)
	{
		super.loadUrl(url);
		selfLoadUrl = url;
	}

	public void setHaveErrRedirect(boolean isRedirect)
	{
		isErrRedirect = isRedirect;
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void setup(Context context)
	{
		mContext = context;
		this.requestFocusFromTouch();
		WebSettings settings = this.getSettings();
//		settings.setJavaScriptEnabled(true);
//		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
//		settings.setDomStorageEnabled(true);
//		settings.setBlockNetworkImage(true);
//		 settings.setGeolocationEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
		settings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
		settings.setSupportZoom(true);//是否可以缩放，默认true
		settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
		settings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
		settings.setAppCacheEnabled(true);//是否使用缓存
		settings.setDomStorageEnabled(true);//开启本地DOM存储
		settings.setLoadsImagesAutomatically(true); // 加载图片
		settings.setMediaPlaybackRequiresUserGesture(false);
		client = new WebViewClientEx();
		setWebViewClient(client);
		setWebChromeClient(new WebChromeClientEx(context, this));
	}

	public void setEvent(WebViewEvent webEvent)
	{
		this.webEvent = webEvent;
	}

	@Override
	public void reload()
	{
		if (err)
		{
			loadUrl(lastUrl);
			return;
		}
		super.reload();
	}

	public void clearWebViewCache()
	{
		File file = getContext().getCacheDir();
		if (file.exists())
		{
			file.delete();
		}
		clearCache(true);
		clearHistory();
		clearFormData();
		try
		{
			mContext.deleteDatabase("webview.db");
			mContext.deleteDatabase("webviewCache.db");
		} catch (Exception e)
		{
		}
	}

	public boolean isErr()
	{
		return err;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
		}
		return super.onTouchEvent(ev);
	}

	class WebViewClientEx extends WebViewClient
	{

		protected void error(String failingUrl, WebView view, int errorCode, String description)
		{
			if (isErrRedirect || failingUrl.equals(selfLoadUrl))
			{
				lastUrl = failingUrl;
				loadUrl(ERR_URL);
				err = true;
				if (webEvent != null)
				{
					webEvent.onReceivedError(view, errorCode, description, failingUrl);
				}
			}
		}

		@SuppressLint("NewApi")
		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
		{
			super.onReceivedError(view, request, error);
			error(request.getUrl().toString(), view, error.getErrorCode(), error.getDescription().toString());
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			super.onReceivedError(view, errorCode, description, failingUrl);			
			error(failingUrl, view, errorCode, description);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			err = url.equals(ERR_URL);
//			getSettings().setBlockNetworkImage(false);
			if (webEvent != null)
			{
				webEvent.onPageFinished(view, url);
			}
		}

		@SuppressLint("NewApi")
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
		{
			if (webEvent == null || webEvent.shouldOverrideUrlLoading(view, request.getUrl().toString()))
			{
				view.loadUrl(request.getUrl().toString());
			}
			err = false;
			return true;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (webEvent == null || webEvent.shouldOverrideUrlLoading(view, url))
			{
				view.loadUrl(url);
			}
			err = false;
			return true;
		}
	}
}
