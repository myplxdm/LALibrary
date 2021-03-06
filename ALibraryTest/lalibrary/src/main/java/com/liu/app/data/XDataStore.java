package com.liu.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

/**
 * Created by liu on 2018/2/23.
 */

public class XDataStore
{
    public static String ACCOUNT = "account";
    public static String ACC_SECRET = "acc_secret";
    public static String LOGIN_TYPE = "login_type";
    public static String FIRST_RUN = "firstRun";

    private static class SingletonHolder
    {
        private static final XDataStore INSTANCE = new XDataStore();
    }

    public static final XDataStore inst()
    {
        return XDataStore.SingletonHolder.INSTANCE;
    }

    //
    private SharedPreferences sp;

    public void init(Context c, String name)
    {
       if (sp == null)
       {
           sp = c.getSharedPreferences(name, Context.MODE_PRIVATE);
       }
    }

    public void setAccount(String acc)
    {
        sp.edit().putString(ACCOUNT, acc).commit();
    }

    public String getAccount()
    {
        return sp.getString(ACCOUNT, "");
    }

    public void setSecret(String secret)
    {
        sp.edit().putString(ACC_SECRET, secret).commit();
    }

    public String getSecret()
    {
        return sp.getString(ACC_SECRET, "");
    }

    public void setLoginType(int lt)
    {
        sp.edit().putInt(LOGIN_TYPE, lt).commit();
    }

    public int getLoginType()
    {
        return sp.getInt(LOGIN_TYPE, 0);
    }

    public boolean isFirstRun()
    {
        boolean isHas = sp.getBoolean(FIRST_RUN, false);
        if (!isHas) sp.edit().putBoolean(FIRST_RUN, true).commit();
        return isHas;
    }

    public void resetFirstRun()
    {
        sp.edit().putBoolean(FIRST_RUN, false).commit();
    }

    public void saveObject(Object obj)
    {
        String key = obj.getClass().getName();
        String val = JSON.toJSONString(obj);
        sp.edit().putString(key, val).commit();
    }

    public <T> T getObject(Class<T> cls)
    {
        String json = sp.getString(cls.getName(), "");
        if (!TextUtils.isEmpty(json))
        {
            return JSON.parseObject(json, cls);
        }
        return null;
    }

    public void saveBoolean(String key, boolean val)
    {
        sp.edit().putBoolean(key, val).commit();
    }

    public boolean getBoolean(String key, boolean def)
    {
        return sp.getBoolean(key, def);
    }

    public void saveString(String key, String val)
    {
        sp.edit().putString(key, val).commit();
    }

    public String getString(String key)
    {
        return sp.getString(key, "");
    }

    public SharedPreferences.Editor getPutSP()
    {
        return sp.edit();
    }

    public SharedPreferences getSP()
    {
        return sp;
    }
}
