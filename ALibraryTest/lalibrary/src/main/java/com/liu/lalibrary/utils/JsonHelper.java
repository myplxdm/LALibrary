package com.liu.lalibrary.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by liu on 2017/12/7.
 */

public class JsonHelper
{
    public static int getInt(JSONObject json, String key, int defInt)
    {
        return json.containsKey(key) ? json.getIntValue(key) : defInt;
    }

    public static String getString(JSONObject json, String key, String defStr)
    {
        return json.containsKey(key) ? json.getString(key) : defStr;
    }

    public static boolean getBoolen(JSONObject json, String key, boolean defBool)
    {
        return json.containsKey(key) ? json.getBoolean(key) : defBool;
    }

    public static JSONObject convert(Object... args)
    {
        JSONObject jo = new JSONObject();
        for (int i = 0;i < args.length;i += 2)
        {
            jo.put((String) args[i], args[i + 1]);
        }
        return jo;
    }

    public static String convertToStr(Object... args)
    {
        JSONObject jo = new JSONObject();
        for (int i = 0;i < args.length;i += 2)
        {
            jo.put((String) args[i], args[i + 1]);
        }
        return JSON.toJSONString(jo);
    }
}
