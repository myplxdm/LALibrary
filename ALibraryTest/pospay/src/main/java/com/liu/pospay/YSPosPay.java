package com.liu.pospay;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.utils.Utils;
import com.ums.AppHelper;
import com.ums.anypay.service.IOnTransEndListener;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class YSPosPay extends BasePosPay
{
    private final String RC_SUCCESS = "0";
    private final String APP_ID = "54e85add425c4b718a1970a3e70a228b";

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != AppHelper.TRANS_REQUEST_CODE) return null;
        JSONObject json = null;
        if (data != null && !TextUtils.isEmpty(data.getStringExtra("data")))
        {
            json = JSON.parseObject(data.getStringExtra("data"));
        }else
        {
            json = new JSONObject();
        }
        String code = json.getString(AppHelper.RESULT_CODE);
        String msg = json.getString(AppHelper.RESULT_MSG);
        String td = json.getString(AppHelper.TRANS_DATA);
        JSONObject res = null;
        if (!TextUtils.isEmpty(td) && code.equals(RC_SUCCESS))
        {
            res = JSONObject.parseObject(td);
            res.put(OPER_STATUS, res.getString("resCode").equals("00"));
            res.put(OPER_ERROR, Utils.safeStr(res.getString("resDesc")));
        }else
        {
            res = new JSONObject();
            res.put(OPER_STATUS, false);
            res.put(OPER_ERROR, Utils.safeStr(msg));
        }
        res.put(PAY_TYPE, String.valueOf(payType));
        return res;
    }

    @Override
    public void pay(final AbsActivity activity, String curNo, String price, int payType)
    {
        this.payType = payType;
        this.curNo = curNo;
        this.respTransType = RESP_PAY;
        //
        org.json.JSONObject cmd = new org.json.JSONObject();
        String appName = "",transId = "";
        try {
            cmd.put("appId",APP_ID);
            cmd.put("extOrderNo",curNo);
            cmd.put("amt",price);
            switch (payType)
            {
                case PAY_TYPE_CARD:
                    appName = "全民惠";
                    transId = "消费";
                    break;
                case PAY_TYPE_WX:
                case PAY_TYPE_ALIPAY:
                    appName = "POS 通";
                    transId = "扫一扫";
                    break;
            }
        } catch (JSONException e)
        {
            return;
        }

        AppHelper.callTrans(activity, appName, transId, cmd, new IOnTransEndListener()
        {
            @Override
            public void onEnd(String reslutmsg)
            {
                super.onEnd(reslutmsg);
                Intent data = new Intent();
                data.putExtra("data",reslutmsg);
                activity.callActivityResult(AppHelper.TRANS_REQUEST_CODE, Activity.RESULT_OK,data);
            }
        });
    }

    public boolean isYSCancel(String date, String time, String[] ymd)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        boolean isLThan11 = false;//小于23点
        try {
            isLThan11 = new Date().before(sdf.parse(String.format("%s/%s/%s 23:00:00",
                    c.get(Calendar.YEAR),c.get(Calendar.MONTH) + 1,c.get(Calendar.DAY_OF_MONTH))));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        if (isLThan11)
        {
            try {
                c.add(c.DATE,-1);
                Date begin = sdf.parse(String.format("%s/%s/%s 23:00:00",
                        c.get(Calendar.YEAR),c.get(Calendar.MONTH) + 1,c.get(Calendar.DAY_OF_MONTH)));
                c.setTime(new Date());
                Date end = sdf.parse(String.format("%s/%s/%s 23:00:00",
                        c.get(Calendar.YEAR),c.get(Calendar.MONTH) + 1,c.get(Calendar.DAY_OF_MONTH)));

                Date orderDdate = sdf.parse(date + " " + time);
                return orderDdate.after(begin) && orderDdate.before(end);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }else
        {
            try {
                c.setTime(new Date());
                Date begin = sdf.parse(String.format("%s/%s/%s 23:00:00",
                        c.get(Calendar.YEAR),c.get(Calendar.MONTH) + 1,c.get(Calendar.DAY_OF_MONTH)));
                c.setTime(new Date());
                c.add(c.DATE,1);
                Date end = sdf.parse(String.format("%s/%s/%s 23:00:00",
                        c.get(Calendar.YEAR),c.get(Calendar.MONTH) + 1,c.get(Calendar.DAY_OF_MONTH)));
                Date orderDdate = sdf.parse(date + " " + time);
                return orderDdate.after(begin) && orderDdate.before(end);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void refund(final AbsActivity activity, boolean isCancel, String curNo, String price, String param)
    {
        this.curNo = curNo;
        this.respTransType = RESP_REFUND;
        JSONObject json = JSON.parseObject(param);
        String traceNo = json.getString("traceNo");
        String refNo = json.getString("refNo");
        String date = Utils.safeStr(json.getString("date"));
        String time = Utils.safeStr(json.getString("time"));
        String[] ymd = date.split("/");
        if (ymd != null && ymd.length == 3 && !TextUtils.isEmpty(time))
        {
            int payType = Integer.parseInt(json.getString("payType"));
            if (payType == PAY_TYPE_CARD)
            {
                isCancel = isYSCancel(json.getString("date"), time, ymd);
            }else
            {
                isCancel = getCurDateStr().equals(String.format("%s%s%s",ymd[0],ymd[1],ymd[2]));
            }
        }
        //isCancel = false;
        String transId = "";
        transId = isCancel ? "撤销" : "退货";
        org.json.JSONObject cmd = new org.json.JSONObject();
        try {
            cmd.put("appId",APP_ID);
            cmd.put("extOrderNo",curNo);
            if (isCancel)
            {
                cmd.put("orgTraceNo", traceNo);
            }else
            {
                cmd.put("amt", price);
                cmd.put("refNo", refNo);
                cmd.put("date", ymd[1] + ymd[2]);
                cmd.put("tradeYear", ymd[0]);

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        AppHelper.callTrans(activity, "公共资源", transId, cmd, new IOnTransEndListener()
        {
            @Override
            public void onEnd(String reslutmsg)
            {
                super.onEnd(reslutmsg);
                Intent data = new Intent();
                data.putExtra("data",reslutmsg);
                activity.callActivityResult(AppHelper.TRANS_REQUEST_CODE, Activity.RESULT_OK,data);
            }
        });
    }
}
