package com.liu.app.web;

import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by liu on 2017/12/11.
 */

public abstract class BaseWebPlugin implements IWebPlugin
{
    protected WeakReference<IWebShell> webShell;

    @Override
    public void init(IWebShell ws, Intent data)
    {
        webShell = new WeakReference<IWebShell>(ws);
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public void deInit()
    {
        webShell = null;
    }
}
