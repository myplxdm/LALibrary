package com.liu.lalibrary.upload;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpMultipartEntity extends MultipartEntity
{
	public interface UploadEvent
	{
		public void onProgress(UploadInfo ui);
	}

	private UploadEvent	event;
	private UploadInfo	ui;

	public HttpMultipartEntity(UploadEvent event, UploadInfo ui)
	{
		this(HttpMultipartMode.STRICT, event, ui);
	}

	public HttpMultipartEntity(HttpMultipartMode mode, UploadEvent event,
			UploadInfo ui)
	{
		super(mode);
		this.event = event;
		this.ui = ui;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException
	{
		super.writeTo(new CountingOutputStream(outstream));
	}

	class CountingOutputStream extends FilterOutputStream
	{
		public CountingOutputStream(final OutputStream out)
		{
			super(out);
		}

		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
			ui.progress += len;
			event.onProgress(ui);
		}

		public void write(int b) throws IOException
		{
			out.write(b);
			ui.progress++;
			event.onProgress(ui);
		}
	}
}
