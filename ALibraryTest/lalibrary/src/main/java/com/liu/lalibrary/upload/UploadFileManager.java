package com.liu.lalibrary.upload;

import com.liu.lalibrary.log.LogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

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

        public void onUploadError(UploadInfo ui);
    }

    private static UploadFileManager inst = null;

    // thread
    private ExecutorService mExecutorService;
    //
    private final String TAG = "UploadFileManager";

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
        mExecutorService = Executors.newCachedThreadPool();
    }

    public synchronized void add(UploadInfo ui, UploadProgressEvent event)
    {
        mExecutorService.submit(new UploadTask(ui, event));
    }

    public void stop()
    {
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

    class UploadTask implements Runnable, HttpMultipartEntity.UploadEvent
    {
        private HttpClient client;
        private HttpPost post;
        private UploadInfo info;
        private UploadProgressEvent event;


        public UploadTask(UploadInfo ui, UploadProgressEvent event)
        {
            this.info = ui;
            this.event = event;
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

        void complete(UploadInfo ui, boolean err)
        {
            if (err)
            {
                event.onUploadError(ui);
            } else
            {
                event.onUploadComplet(ui);
            }
        }

        @Override
        public void run()
        {
            HttpMultipartEntity entity = null;
            boolean isErr = false;
            try
            {
                info.progress = 0;
                client = new DefaultHttpClient();
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
                post = new HttpPost(info.url);
                entity = new HttpMultipartEntity(this, info);
                info.createEntity(entity);
                info.totalSize = (int) entity.getContentLength();
                post.setEntity(entity);
                event.onStartUpload(info);
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                {
                    isErr = true;
                    info.retString = String.valueOf(response.getStatusLine().getStatusCode());
                    LogUtils.LOGE(UploadFileManager.class, TAG, EntityUtils.toString(response.getEntity()));
                } else
                {
                    info.retString = EntityUtils.toString(response.getEntity());
                }

            } catch (Exception e)
            {
                LogUtils.LOGE(UploadFileManager.class, TAG, e.getMessage());
                isErr = true;
            } finally
            {
                complete(info, isErr);
                abortPost();
            }
        }

        @Override
        public void onProgress(UploadInfo ui)
        {
            event.onProgress(ui);
        }
    }

//	class UploadThread implements Runnable, HttpMultipartEntity.UploadEvent
//	{
//		private ArrayList<UploadInfo>	ulist	= new ArrayList<UploadInfo>(100);
//		private HttpClient				client	= null;
//		private HttpPost				post	= null;
//
//		public void add(UploadInfo ui)
//		{
//			ulist.add(ui);
//		}
//
//		void abortPost()
//		{
//			if (post != null)
//			{
//				try
//				{
//					post.abort();
//				} catch (Exception e2)
//				{
//				}
//			}
//			post = null;
//			client = null;
//		}
//
//		@Override
//		public void run()
//		{
//			UploadInfo ui = null;
//			HttpMultipartEntity entity;
//			int reTryCount = 0;
//			while (mIsRun)
//			{
//				if (ulist.size() > 0)
//				{
//					ui = ulist.get(0);
//					if (ui.state == UploadInfo.UPLOAD_DEL && !ui.setState(UploadInfo.UPLOAD_ING))
//					{
//						ulist.remove(0);
//						continue;
//					}
//					try
//					{
//						ui.progress = 0;
//						client = new DefaultHttpClient();
//						client.getParams().setParameter(
//								CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
//						client.getParams().setParameter(
//								CoreConnectionPNames.SO_TIMEOUT, 15000);
//						post = new HttpPost(ui.url);
//						entity = new HttpMultipartEntity(this, ui);
//						ui.createEntity(entity);
//						ui.totalSize = (int) entity.getContentLength();
//						post.setEntity(entity);
//						synchronized (mEventSet)
//						{
//							for (final UploadProgressEvent event : mEventSet)
//							{
//								event.onStartUpload(ui);
//							}
//						}
//						HttpResponse response = client.execute(post);
//						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
//						{
//							reTryCount++;
//						}else
//						{
//							reTryCount = 0;
//							ui.retString = EntityUtils.toString(response.getEntity());
//						}
//
//					} catch (Exception e)
//					{
//						reTryCount++;
//						LogUtils.LOGE(UploadFileManager.class, TAG, e.getMessage());
//					} finally
//					{
//						try
//						{
//							if (reTryCount == 0 || reTryCount >= RETRY_COUNT)
//							{
//								ulist.remove(0);
//								del(ui, reTryCount == RETRY_COUNT);
//							}
//							abortPost();
//						} catch (Exception e2)
//						{
//						}
//
//					}
//				} else
//				{
//					try
//					{
//						Thread.sleep(500);
//					} catch (Exception e)
//					{
//					}
//				}
//			}
//		}
//
//		@Override
//		public void onProgress(UploadInfo ui)
//		{
//			synchronized (mEventSet)
//			{
//				for (final UploadProgressEvent event : mEventSet)
//				{
//					event.onProgress(ui);
//				}
//			}
//		}
//	}
}
