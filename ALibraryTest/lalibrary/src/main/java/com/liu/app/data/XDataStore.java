package com.liu.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.liu.app.DirManager;
import com.liu.lalibrary.utils.SPUtils;

/**
 * Created by liu on 2018/2/23.
 */

public class XDataStore
{
    public static String ACCOUNT = "account";
    public static String ACC_SECRET = "acc_secret";
    public static String LOGIN_TYPE = "login_type";

    public static final int LOGIN_WAY_MAIN = 0x0;
    public static final int LOGIN_WAY_LOGIN = 0x1;
    public static final int LOGIN_WAY_FIRST = 0x2;

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
        if (!sp.contains(LOGIN_TYPE))
        {
            sp.edit().putInt(LOGIN_TYPE, LOGIN_WAY_LOGIN);
            return LOGIN_WAY_FIRST;
        }
        return sp.getInt(LOGIN_TYPE, LOGIN_WAY_LOGIN);
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
