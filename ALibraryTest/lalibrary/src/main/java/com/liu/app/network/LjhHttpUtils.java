package com.liu.app.network;

import android.text.TextUtils;

import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.INetworkCheck;
import com.liu.lalibrary.utils.NetConnManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liu on 2017/8/23.
 */

public class LjhHttpUtils
{
    private final int TIME_OUT = 10;//sec
    private final String NERR_NETWORK = "网络错误";
    private final String NERR_NO_NETWORK = "当前无网络";
    private final String NERR_NO_NO_200 = "请求出错";
    private final String NERR_CONTENT_EMPTY = "服务器返回空数据";
    //
    public static final int HU_STATE_OK = 0x0;
    public static final int HU_STATE_NO_NET = 0x10;//无网络
    public static final int HU_STATE_ERR = 0x11;//请求出错
    public static final int HU_CONTENT_EMPTY = 0x12;//服务器返回内容空

    private static class SingletonHolder
    {
        private static final LjhHttpUtils INSTANCE = new LjhHttpUtils();
    }

    public interface IHttpRespListener
    {
        public void onHttpReqResult(int state, String result);

        public void onHttpReqProgress(float progress);
    }

    private OkHttpClient client;
    private INetworkCheck networkCheck;

    private LjhHttpUtils()
    {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)//设置写入超时时间
                .build();
    }

    public static final LjhHttpUtils inst()
    {
        return SingletonHolder.INSTANCE;
    }

    public void setNetworkCheck(INetworkCheck check)
    {
        this.networkCheck = check;
    }

    private boolean checkNetwork(IHttpRespListener listener)
    {
        if (networkCheck != null && !networkCheck.isConnect())
        {
            listener.onHttpReqResult(HU_STATE_NO_NET, NERR_NO_NETWORK);
            return false;
        }
        return true;
    }

    private void onHttpReqResult(String url, IHttpRespListener listener, int state, String result)
    {
        try
        {
            if (state == HU_STATE_OK && TextUtils.isEmpty(result))
            {
                listener.onHttpReqResult(HU_CONTENT_EMPTY, NERR_CONTENT_EMPTY);
                return;
            }
            listener.onHttpReqResult(state, result);
        } catch (Exception e)
        {
            listener.onHttpReqResult(HU_STATE_ERR, e.getMessage());
        }
    }

    public Call exeCall(final String url, Call call, final IHttpRespListener listener)
    {
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                onHttpReqResult(url, listener, HU_STATE_ERR, NERR_NETWORK);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                onHttpReqResult(url, listener,
                        response.code() == 200 ? HU_STATE_OK : response.code(),
                        response.body().string());
            }
        });
        return call;
    }

    public Call get(final String url, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        Call call = client.newCall(new Request.Builder().url(url).build());
        return exeCall(url, call, listener);
    }

    public Call postJson(final String url, String json, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        return exeCall(url, call, listener);
    }

    public Call postJson(final String url, String json,
                         HashMap<String,String> header, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request.Builder rb = new Request.Builder().url(url);
        if (header != null)
        {
            for (Map.Entry<String, String> en : header.entrySet())
            {
                rb.addHeader(en.getKey(), en.getValue());
            }
        }
        Call call = client.newCall(rb.post(body).build());
        return exeCall(url, call, listener);
    }

    public Call post(final String url, HashMap<String, String> params,
                     final IHttpRespListener listener)
    {
        return this.post(url, params, null, listener);
    }

    public Call post(final String url, HashMap<String, String> params,
                     HashMap<String, String> header, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null)
        {
            for (Map.Entry<String, String> en : params.entrySet())
            {
                builder.add(en.getKey(), en.getValue());
            }
        }

        Request.Builder rb = new Request.Builder().url(url);//.post(builder.build()).build();
        if (header != null)
        {
            for (Map.Entry<String, String> en : header.entrySet())
            {
                rb.addHeader(en.getKey(), en.getValue());
            }
        }
        Call call = client.newCall(rb.post(builder.build()).build());
        return exeCall(url, call, listener);
    }

    public Call uploadFile(final String url, HashMap<String, String> params,
                           HashMap<String, File> files, final IHttpRespListener listener)
    {
        return this.uploadFile(url, params, null, files, listener);
    }

    public Call uploadFile(final String url, HashMap<String, String> params,
                           HashMap<String, String> header,
                           HashMap<String, File> files, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null)
        {
            for (Map.Entry<String, String> en : params.entrySet())
            {
                builder.addFormDataPart(en.getKey(), en.getValue());
            }
        }
        for (Map.Entry<String, File> file : files.entrySet())
        {
            builder.addFormDataPart(file.getKey(), file.getValue().getName(), RequestBody.create(null, file.getValue()));
        }
        Request.Builder rb = new Request.Builder().url(url);
        if (header != null)
        {
            for (Map.Entry<String, String> en : header.entrySet())
            {
                rb.addHeader(en.getKey(), en.getValue());
            }
        }
        Call call = client.newCall(rb.post(new ProgressRequestBody(builder.build(), listener)).build());
        return exeCall(url, call, listener);
    }

    public Call downFile(final String url, final String saveName, final IHttpRespListener listener)
    {
        if (!checkNetwork(listener)) return null;
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                onHttpReqResult(url, listener, HU_STATE_ERR, NERR_NETWORK);
                LogUtils.LOGE(LjhHttpUtils.class, String.format("%s is err = %s", url, e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(saveName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                        sum += len;
                        listener.onHttpReqProgress((sum * 1.0f / total * 100));
                    }
                    fos.flush();
                    onHttpReqResult(url, listener, HU_STATE_OK, file.getAbsolutePath());
                } catch (Exception e)
                {
                    onHttpReqResult(url, listener, HU_STATE_ERR, e.getMessage());
                    LogUtils.LOGE(LjhHttpUtils.class, String.format("%s is err = %s", url, e.getMessage()));
                } finally
                {
                    try
                    {
                        if (is != null)
                            is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e)
                    {
                    }
                }
            }
        });
        return call;
    }
}
