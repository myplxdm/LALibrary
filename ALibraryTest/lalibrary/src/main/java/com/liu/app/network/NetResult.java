package com.liu.app.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by liu on 2017/11/21.
 */

public class NetResult
{
    public int state;
    public String ret;
    public String obj;

    public NetResult(){}

    public NetResult(int state, String ret, String obj)
    {
        this.state = state;
        this.ret = ret;
        this.obj = obj;
    }

    public JSONObject getObj()
    {
        if (obj == null) return null;
        return JSON.parseObject(obj);
    }

}
