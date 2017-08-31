package com.liu.lalibrary.plugins;

/**
 * Created by liu on 2017/8/31.
 */

public interface IPluginEvent
{
    public void pluginResult(boolean isSuccess, String result, Object param);
    public void pluginClose(boolean isSuccess, String result);
}
