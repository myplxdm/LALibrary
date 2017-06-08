package com.google.zxing.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.camera.CameraManager;
import com.google.zxing.client.decode.CaptureActivityHandler;
import com.google.zxing.client.decode.FinishListener;
import com.google.zxing.client.decode.InactivityTimer;
import com.google.zxing.client.view.ViewfinderView;
import com.liu.lalibrary.R;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class CaptureActivity extends Activity implements Callback
{
	public static int					REQ_SCAN_CODE	= 230;
	public static String				RESULT_TEXT		= "res_text";
	public static String				RESULT_BITMAP	= "res_bitmap";

	private static final String			TAG				= CaptureActivity.class.getSimpleName();

	private CameraManager				cameraManager;
	private CaptureActivityHandler		handler;
	private ViewfinderView				viewfinderView;
	//
	private boolean						hasSurface;
	private InactivityTimer				inactivityTimer;
	private BeepManager					beepManager;
	private AmbientLightManager			ambientLightManager;
	private Result						savedResultToShow;
	//
	private Collection<BarcodeFormat>	decodeFormats;
	private Map<DecodeHintType, ?>		decodeHints;
	private String						characterSet;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_capture);
		//
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		cameraManager = new CameraManager(getApplication());
		//
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//
		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);
		//
		inactivityTimer.onResume();
		//
		decodeFormats = null;
		characterSet = null;
		//
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		} else
		{
			surfaceHolder.addCallback(this);
		}
	}

	@Override
	protected void onPause()
	{
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		beepManager.close();
		cameraManager.closeDriver();
		// historyManager = null; // Keep for onActivityResult
		if (!hasSurface)
		{
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder)
	{
		if (surfaceHolder == null)
		{
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen())
		{
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try
		{
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null)
			{
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe)
		{
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e)
		{
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result)
	{
		// Bitmap isn't used yet -- will be used soon
		if (handler == null)
		{
			savedResultToShow = result;
		} else
		{
			if (result != null)
			{
				savedResultToShow = result;
			}
			if (savedResultToShow != null)
			{
				Message message = Message.obtain(handler, CaptureActivityHandler.DECODE_SUCCEEDED, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	private void displayFrameworkBugMessageAndExit()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor)
	{
		inactivityTimer.onActivity();
		if (barcode != null)
		{
			beepManager.playBeepSoundAndVibrate();
		}
		String resultString = rawResult.getText();
		if (resultString.equals(""))
		{
			Toast.makeText(this, R.string.scaner_error, Toast.LENGTH_SHORT).show();
		} else
		{
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(RESULT_TEXT, resultString);
			bundle.putParcelable(RESULT_BITMAP, barcode);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);
		}
		finish();
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();
	}

	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public CameraManager getCameraManager()
	{
		return cameraManager;
	}

	public Handler getHandler()
	{
		return handler;
	}

	/************************************************************/
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		if (holder == null)
		{
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
	}
}
