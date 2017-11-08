package com.liu.lalibrary.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by liu on 2017/4/17.
 */

public class VerifyUtils
{
    public static int MIN_PWD_LEN = 6;
    public static int MAX_PWD_LEN = 12;
    public static int VER_CODE_LEN = 6;

    public static boolean verifyPhone(String phone, Context context)
    {
        String hit = "请输入11位手机号";
        boolean isOK = false;
        if (!TextUtils.isEmpty(phone))
        {
            isOK = phone.length() == 11;
        }
        if (!isOK && context != null)
        {
            Toast.makeText(context, hit, Toast.LENGTH_LONG).show();
        }
        return isOK;
    }

    public static boolean verifyPwd(String pwd, Context context)
    {
        String hit = String.format("密码不能小于%d位或不能大于%d位", MIN_PWD_LEN, MAX_PWD_LEN);
        boolean isOK = false;
        if (!TextUtils.isEmpty(pwd))
        {
            isOK = pwd.length() >= MIN_PWD_LEN && pwd.length() <= MAX_PWD_LEN;
        }
        if (!isOK && context != null)
        {
            Toast.makeText(context, hit, Toast.LENGTH_LONG).show();
        }
        return isOK;
    }

    public static boolean verifyVerCode(String vc, int len, Context context)
    {
        if (vc != null && vc.length() != len)
        {
            Toast.makeText(context, "请输入" + len + "位验证码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean verifyIdCard(String idCard)
    {
        if (!TextUtils.isEmpty(idCard))
        {
            String IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";
            return idCard.matches(IDCardRegex);
        }
        return false;
    }
}
