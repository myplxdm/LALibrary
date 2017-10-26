package com.liu.lalibrary.utils;

import android.content.Context;

/**
 * Created by liu on 2017/10/23.
 */

public interface INetworkCheck
{
    public void reg(Context c);
    public void unReg(Context c);
    public boolean isConnect();
}
