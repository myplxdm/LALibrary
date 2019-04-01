package com.liu.alibrarytest.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.liu.app.wx.WXSDK;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		WXSDK.inst().handle(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);

		setIntent(intent);
		WXSDK.inst().handle(intent, this);
		finish();
	}

	@Override
	public void onReq(BaseReq req)
	{
		WXSDK.inst().onReq(req);
		finish();
	}

	@Override
	public void onResp(BaseResp resp)
	{
		WXSDK.inst().onResp(resp);
		finish();
	}
}