package com.liu.app;

/**
 * Created by liu on 2018/2/23.
 */

public class JniApi
{
    static
    {
        System.loadLibrary("native-lib");
    }

    private JniApi(){}

    private static class SingletonHolder
    {
        private static final JniApi INSTANCE = new JniApi();
    }
    public static final JniApi inst() {
        return SingletonHolder.INSTANCE;
    }

    public native String reqEncode(String url, String ver, String[] keys, String[] values);
    public native String decodeResult(String str);
    public native String getString(String value);
}
