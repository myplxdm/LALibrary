package com.liu.lalibrary.ui.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class RLWebViewWrapper extends RelativeLayout implements WebViewEx.WebViewEvent
{
	private ProgressBar		pb;
	private WebViewEx		web;
	private WebCallback		listener;

	public interface WebCallback
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url);
	}


	public RLWebViewWrapper(Context context)
	{
		this(context, null, 0);
	}

	public RLWebViewWrapper(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public RLWebViewWrapper(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void init(Context c)
	{
		web = new WebViewEx(c);
		LayoutParams lp_web = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(web, lp_web);
		//
		pb = new ProgressBar(c);
		LayoutParams lp_pb = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_pb.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(pb, lp_pb);
		//
		web.setEvent(this);
	}

	public void setWebCallback(WebCallback wc)
	{
		listener = wc;
	}

	public void loadUrl(String url)
	{
		web.loadUrl(url);		
		pb.setVisibility(View.VISIBLE);
	}
	
	public void loadUrl(String url, boolean showPB)
	{
		web.loadUrl(url);
		pb.setVisibility(showPB ? View.VISIBLE : View.GONE);
	}

	public void reload()
	{
		web.reload();
		web.setVisibility(View.INVISIBLE);
		pb.setVisibility(View.VISIBLE);
	}

	//true, 只要网页有错就加载本地出错页面
	public void setHaveErrRedirect(boolean isRedirect)
	{
		web.setHaveErrRedirect(isRedirect);
	}
	
	@SuppressLint("JavascriptInterface")
	public void setJsInterface(Object js, String jsName)
	{
		web.addJavascriptInterface(js, jsName);
	}
	
	public WebViewEx getWeb()
	{
		return web;
	}
	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	{
		pb.setVisibility(View.GONE);
		web.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPageFinished(WebView view, String url)
	{
		pb.setVisibility(View.GONE);
		web.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url)
	{
		if (listener != null)
			return listener.shouldOverrideUrlLoading(view, url);
		return true;
	}
}
