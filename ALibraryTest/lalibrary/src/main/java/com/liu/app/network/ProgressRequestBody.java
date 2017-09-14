package com.liu.app.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by liu on 2017/8/25.
 */

public class ProgressRequestBody extends RequestBody
{
    private RequestBody requestBody;
    private LjhHttpUtils.IHttpRespListener listener;
    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody body, LjhHttpUtils.IHttpRespListener listener)
    {
        requestBody = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType()
    {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException
    {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException
    {
        if (bufferedSink == null)
        {
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    private Sink sink(BufferedSink sink)
    {
        return new ForwardingSink(sink)
        {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException
            {
                super.write(source, byteCount);
                if (contentLength == 0)
                {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                listener.onHttpReqProgress(1.0f * bytesWritten / contentLength);
            }
        };
    }
}
