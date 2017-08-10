package com.liu.lalibrary.upload;

import com.liu.lalibrary.log.LogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UploadFileManager
{
	public interface UploadProgressEvent
	{
		public void onStartUpload(UploadInfo ui);

		public void onProgress(UploadInfo ui);

		public void onUploadComplet(UploadInfo ui);

		public void onUploadAllComplet();

		public void onUploadError(UploadInfo ui);
	}

	private static UploadFileManager				inst			= null;

	//
	private final int								RETRY_COUNT		= 1;
	// thread
	private int										mThreadCount	= 0;
	private int										mAllocThradID	= 0;
	private ExecutorService							mExecutorService;
	private ArrayList<UploadThread>					mThreadList;
	//
	private ArrayList<UploadInfo>					mUploadList;
	private final Set<UploadProgressEvent>			mEventSet;
	private boolean									mIsRun			= false;
	private final String 							TAG				= "UploadFileManager";						

	public static UploadFileManager getInst()
	{
		if (inst == null)
		{
			inst = new UploadFileManager();
		}
		return inst;
	}

	private UploadFileManager()
	{
		mUploadList = new ArrayList<UploadInfo>(100);
		mThreadCount = 1;
		mExecutorService = Executors.newFixedThreadPool(mThreadCount);
		mThreadList = new ArrayList<UploadThread>(
				mThreadCount);
		mEventSet = Collections
				.synchronizedSet(new HashSet<UploadProgressEvent>());
	}

	public synchronized void add(UploadInfo ui)
	{
		if (mIsRun == false)
		{
			start();
		}
		mUploadList.add(ui);
		mThreadList.get(mAllocThradID).add(ui);
		mAllocThradID = (mAllocThradID + 1) % mThreadCount;
	}

	public synchronized void remove(UploadInfo ui)
	{
		ui.setState(UploadInfo.UPLOAD_DEL);
		int pos = mUploadList.indexOf(ui);
		if (pos != -1)
		{
			mUploadList.remove(pos);
		}
	}
	
	public int size()
	{
		return mUploadList.size();
	}
	
	void del(UploadInfo ui, boolean err)
	{
		synchronized (this)
		{
			int size = mUploadList.size();
			if (size > 0 && (size = mUploadList.indexOf(ui)) != -1)
			{
				mUploadList.remove(size);
			}
		}
		synchronized (mEventSet)
		{
			for (final UploadProgressEvent event : mEventSet)
			{
				if (err)
				{
					event.onUploadError(ui);
				}else
				{
					event.onUploadComplet(ui);
				}
				if (mUploadList.size() == 0)
				{
					event.onUploadAllComplet();
				}
			}
		}
		ui = null;
	}

	public ArrayList<UploadInfo> getList()
	{
		return mUploadList;
	}

	public void addEventListener(UploadProgressEvent event)
	{
		mEventSet.add(event);
	}

	public void removeEventListener(UploadProgressEvent event)
	{
		mEventSet.remove(event);
	}
	
	public boolean start()
	{
		if (mIsRun == false)
		{
			mIsRun = true;
			UploadThread thread;
			for (int i = 0; i < mThreadCount; i++)
			{
				thread = new UploadThread();
				mThreadList.add(thread);
				mExecutorService.submit(thread);
			}
		}
		return mIsRun;
	}

	public void stop()
	{
		if (mIsRun)
		{
			mIsRun = false;
			for (UploadThread thread : mThreadList)
			{
				thread.abortPost();
			}
			mExecutorService.shutdown();
			try
			{

				if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS))
				{
					mExecutorService.shutdownNow();
				}
			} catch (InterruptedException ie)
			{
				mExecutorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	class UploadThread implements Runnable, HttpMultipartEntity.UploadEvent
	{
		private ArrayList<UploadInfo>	ulist	= new ArrayList<UploadInfo>(100);
		private HttpClient				client	= null;
		private HttpPost				post	= null;

		public void add(UploadInfo ui)
		{
			ulist.add(ui);
		}

		void abortPost()
		{
			if (post != null)
			{
				try
				{
					post.abort();
				} catch (Exception e2)
				{
				}
			}
			post = null;
			client = null;
		}

		@Override
		public void run()
		{
			UploadInfo ui = null;
			HttpMultipartEntity entity;
			int reTryCount = 0;
			while (mIsRun)
			{
				if (ulist.size() > 0)
				{
					ui = ulist.get(0);
					if (ui.state == UploadInfo.UPLOAD_DEL && !ui.setState(UploadInfo.UPLOAD_ING)) 
					{
						ulist.remove(0);
						continue;
					}
					try
					{
						ui.progress = 0;						
						client = new DefaultHttpClient();
						client.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
						client.getParams().setParameter(
								CoreConnectionPNames.SO_TIMEOUT, 15000);
						post = new HttpPost(ui.url);
						entity = new HttpMultipartEntity(this, ui);
						ui.createEntity(entity);
						ui.totalSize = (int) entity.getContentLength();
						post.setEntity(entity);
						synchronized (mEventSet)
						{
							for (final UploadProgressEvent event : mEventSet)
							{
								event.onStartUpload(ui);
							}
						}
						HttpResponse response = client.execute(post);
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
						{
							reTryCount++;
						}else
						{
							reTryCount = 0;
							ui.retString = EntityUtils.toString(response.getEntity());
						}
						
					} catch (Exception e)
					{
						reTryCount++;
						LogUtils.LOGE(UploadFileManager.class, TAG, e.getMessage());
					} finally
					{
						try
						{
							if (reTryCount == 0 || reTryCount >= RETRY_COUNT)
							{
								ulist.remove(0);
								del(ui, reTryCount == RETRY_COUNT);
							}
							abortPost();
						} catch (Exception e2)
						{
						}

					}
				} else
				{
					try
					{
						Thread.sleep(500);
					} catch (Exception e)
					{
					}
				}
			}
		}

		@Override
		public void onProgress(UploadInfo ui)
		{
			synchronized (mEventSet)
			{
				for (final UploadProgressEvent event : mEventSet)
				{
					event.onProgress(ui);
				}
			}
		}
	}
}
