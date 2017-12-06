package com.liu.lalibrary.ui.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebChromeClientEx extends WebChromeClient
{
	private Context	mContext;
	private WebView	mWebView;
	
	public WebChromeClientEx(Context c, WebView wv)
	{
		mContext = c;
		mWebView = wv;
	}

//	@Override
//	public boolean onJsAlert(WebView view, String url, String message,
//			final JsResult result)
//	{
//		AlertDialogEx.Builder dlg = new AlertDialogEx.Builder(mContext);
//		dlg.setMessage(message);
//		dlg.setTitle("Alert");
//		// Don't let alerts break the back button
//		dlg.setCancelable(true);
//		dlg.setPositiveButton(android.R.string.ok,
//				new AlertDialog.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int which)
//					{
//						result.confirm();
//					}
//				});
//		dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
//		{
//			public void onCancel(DialogInterface dialog)
//			{
//				result.cancel();
//			}
//		});
//		dlg.setOnKeyListener(new DialogInterface.OnKeyListener()
//		{
//			// DO NOTHING
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event)
//			{
//				if (keyCode == KeyEvent.KEYCODE_BACK)
//				{
//					result.confirm();
//					return false;
//				} else
//					return true;
//			}
//		});
//		dlg.create();
//		dlg.show();
//		return true;
//	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result)
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
		dlg.setMessage(message);
		dlg.setTitle("Confirm");
		dlg.setCancelable(true);
		dlg.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						result.confirm();
					}
				});
		dlg.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						result.cancel();
					}
				});
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface dialog)
			{
				result.cancel();
			}
		});
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			// DO NOTHING
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					result.cancel();
					return false;
				} else
					return true;
			}
		});
		dlg.create();
		dlg.show();
		return true;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress)
	{
		super.onProgressChanged(view, newProgress);
		if (newProgress >= 100)
		{
			mWebView.getSettings().setBlockNetworkImage(false);
		}
	}
	
//	@Override
//	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)
//	{
//		final JsPromptResult res = result;
//		AlertDialogEx.Builder dlg = new AlertDialogEx.Builder(mContext);
//		dlg.setMessage(message);
//		final EditText input = new EditText(mContext);
//		if (defaultValue != null)
//		{
//			input.setText(defaultValue);
//		}
//		dlg.setView(input);
//		dlg.setCancelable(false);
//		dlg.setPositiveButton(android.R.string.ok,
//				new DialogInterface.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int which)
//					{
//						String usertext = input.getText().toString();
//						res.confirm(usertext);
//					}
//				});
//		dlg.setNegativeButton(android.R.string.cancel,
//				new DialogInterface.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int which)
//					{
//						res.cancel();
//					}
//				});
//		dlg.create();
//		dlg.show();
//		return true;
//	}
}
