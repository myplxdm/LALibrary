package com.liu.lalibrary.upload;

public abstract class UploadInfo
{
	public static final int	UPLOAD_READY	= 0;
	public static final int	UPLOAD_ING		= 1;
	public static final int	UPLOAD_DEL		= 2;
	//
	public long				progress		= 0;
	public long				totalSize		= 0;
	//
	public String			name;
	public String			filename;
	public String			url;
	public int				paramInt		= 0;
	public Object			paramObject		= null;
	int						state			= UPLOAD_READY; // 0 未下载
	//
	public String			retString;

	//
	public UploadInfo(String url)
	{
		this.url = url;
	}

	//
	public UploadInfo(String url, int state, String name, String filename,
			int paramInt, Object paramObject)
	{
		this.url = url;
		this.name = name;
		this.filename = filename;
		this.paramInt = paramInt;
		this.paramObject = paramObject;
		//
		state = UPLOAD_READY;
		progress = 0;
		totalSize = 0;
	}

	public synchronized boolean setState(int state)
	{
		if (state > this.state)
		{
			this.state = state;
			return true;
		}
		return false;
	}

	public abstract void createEntity(HttpMultipartEntity entity);

}
