package com.liu.app.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu on 2017/8/25.
 */

public class NetParamUtils
{
    public static String catParam(String url, HashMap<String, String> params)
    {
        StringBuffer sb = new StringBuffer(url);
        for (Map.Entry<String, String> en : params.entrySet())
        {
            sb.append(String.format("%s=%s&", en.getKey(), en.getValue()));
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public static String catParam(String url, String[] keys, String[] values)
    {
        StringBuffer sb = new StringBuffer(url);
        if (keys.length != values.length)
        {
            throw new RuntimeException("keys.length is not equal to values.length");
        }
        for (int i = 0; i < keys.length; i++)
        {
            sb.append(String.format("%s=%s&", keys[i], values[i]));
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public static HashMap<String, String> objToMap(Object obj)
    {
        if (obj == null)
        {
            return null;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        JSONObject jo = (JSONObject) JSON.toJSON(obj);
        for (Map.Entry<String, Object> m: jo.entrySet())
        {
            map.put(m.getKey(), m.getValue().toString());
        }
        return map;
    }

    public static String formatToJson(Object... args)
    {
        if (args.length % 2 == 0)
        {
            JSONObject jo = new JSONObject();
            for (int i = 0;i < args.length;i+=2)
            {
                jo.put((String) args[i], args[i + 1]);
            }
            return JSON.toJSONString(jo);
        }
        return null;
    }

    public static HashMap<String,String> formatParam(Object... args)
    {
        if (args.length % 2 == 0)
        {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0;i < args.length;i+=2)
            {
                map.put((String)args[i], String.valueOf(args[i + 1]));
            }
            return map;
        }
        return null;
    }
}
