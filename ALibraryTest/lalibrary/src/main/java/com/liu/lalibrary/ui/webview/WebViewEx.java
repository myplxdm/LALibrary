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

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void setup(Context context)
	{
		mContext = context;
		this.requestFocusFromTouch();
		WebSettings settings = this.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		settings.setDomStorageEnabled(true);
		settings.setBlockNetworkImage(true);
		// settings.setRenderPriority(RenderPriority.HIGH);
		// try
		// {
		// Method gingerbread_getMethod =
		// WebSettings.class.getMethod("setNavDump", new Class[] { boolean.class
		// });
		//
		// if (android.os.Build.VERSION.SDK_INT <
		// android.os.Build.VERSION_CODES.HONEYCOMB
		// && android.os.Build.MANUFACTURER.contains("HTC"))
		// {
		// gingerbread_getMethod.invoke(settings, true);
		// }
		// } catch (Exception e)
		// {
		// }
		// if (android.os.Build.VERSION.SDK_INT >
		// android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		// settings.setAllowUniversalAccessFromFileURLs(true);
		// settings.setDomStorageEnabled(true);
		// settings.setGeolocationEnabled(true);
		// settings.getUserAgentString();
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
		@SuppressLint("NewApi")
		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
		{
			super.onReceivedError(view, request, error);
			lastUrl = request.getUrl().toString();
			loadUrl(ERR_URL);
			err = true;
			if (webEvent != null)
			{
				webEvent.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), lastUrl);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			super.onReceivedError(view, errorCode, description, failingUrl);			
			lastUrl = failingUrl;
			loadUrl(ERR_URL);
			err = true;
			if (webEvent != null)
			{
				webEvent.onReceivedError(view, errorCode, description, failingUrl);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			err = url.equals(ERR_URL);
			getSettings().setBlockNetworkImage(false);
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
