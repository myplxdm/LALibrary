package com.liu.pospay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.utils.Utils;

import java.util.ArrayList;

/**
 * Created by liu on 2018/8/21.
 */
//联迪中行总行pos
public class BocPosPay extends BasePosPay
{
    private final String[] CANCEL_TT_TYPE = { "202003", "201003", "106" };
    private final String[] PAY_TYPE_VAL = { "wechatPay", "alipay", "cardPay" };
    private final String[] REFUND_TT_TYPE = { "202004", "201004", "105" };
    private final String[] TRANS_TYPE = { "202001", "201001", "101" };

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != REQ_CODE_BOC) return null;
        boolean status = resultCode != AbsActivity.RESULT_CANCELED;
        JSONObject jo = new JSONObject();
        Bundle bundle = null;
        if (data != null && (bundle = data.getExtras()) != null)
        {
            for (String key : bundle.keySet())
            {
                jo.put(key, bundle.getString(key));
            }
        }
        jo.put(PAY_TYPE, String.valueOf(payType));
        jo.put(OPER_STATUS, status);
        if (!status)
        {
            jo.put(OPER_ERROR, data.getStringExtra("msg"));
        }
        return jo;
    }

    @Override
    public void pay(AbsActivity activity, String curNo, String price, int payType)
    {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.yada.spos.cashierplatfrom", "com.yada.spos.cashierplatfrom.InvokeActivity"));
        this.curNo = curNo;
        this.respTransType = RESP_PAY;
        this.payType = payType;
        String[] keys = { "channelName", "tranType", "offerFlag", "appOrderNo", "tranAmt" };
        String[] vals = new String[5];
        vals[0] = this.PAY_TYPE_VAL[payType];
        vals[1] = this.TRANS_TYPE[payType];
        vals[2] = "0";
        vals[3] = this.curNo;
        vals[4] = String.format("%012d", Integer.valueOf(Integer.parseInt(price)));
        insertKV(i, keys, vals);
        activity.startActivityForResult(i, REQ_CODE_BOC);
    }

    @Override
    public void refund(AbsActivity activity, boolean isCancel, String curNo, String price, String param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.yada.spos.cashierplatfrom", "com.yada.spos.cashierplatfrom.InvokeActivity"));
        this.curNo = curNo;
        this.respTransType = RESP_REFUND;
        JSONObject json = JSON.parseObject(param);
        ArrayList keys = new ArrayList();
        ArrayList vals = new ArrayList();
        int payType = Integer.parseInt(json.getString("payType"));
        keys.add("channelName");
        vals.add(this.PAY_TYPE_VAL[payType]);

        String payDate = json.getString("tranDateTime");
        if (payType != PAY_TYPE_CARD)//不是银行卡
        {
            keys.add("orderNo");
            vals.add(json.getString("orderNo"));
        }else if (!TextUtils.isEmpty(payDate))
        {
            keys.add("cardNo");
            vals.add(json.getString("cardNo"));

            keys.add("authNo");
            vals.add(Utils.safeStr(json.getString("authNo")));

            keys.add("tranDate");
            vals.add(payDate.substring(0,4));

            keys.add("tranTime");
            vals.add(payDate.substring(4));

            keys.add("refundAmt");
            vals.add(price);

            payDate = payDate.substring(0,4);
            isCancel = getCurMDDateStr().equals(payDate);
        }
        keys.add("tranType");
        vals.add(isCancel ? CANCEL_TT_TYPE[payType] : REFUND_TT_TYPE[payType]);

        keys.add("appOrderNo");
        vals.add(json.getString("appOrderNo"));

        keys.add("traceNo");
        vals.add(json.getString("traceNo"));


        insertKV(intent, (String[])keys.toArray(new String[0]), (String[])vals.toArray(new String[0]));
        activity.startActivityForResult(intent, 288);
    }

    /*
    @Override
    public void pay(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.yada.spos.cashierplatfrom",
                "com.yada.spos.cashierplatfrom.InvokeActivity"));
        //
        curNo = param.getString(ORDER);
        respTransType = RESP_PAY;

        String[] ks = new String[]{"channelName", "tranType", "offerFlag","appOrderNo", "tranAmt"};
        String[] vs = new String[]{PAY_TYPE_VAL[param.getIntValue(PAY_TYPE)],
                TRANS_TYPE[param.getIntValue(PAY_TYPE)],"0",curNo,
                String.format("%012d",Integer.parseInt(param.getString(PRICE)))};
        insertKV(intent, ks, vs);
        activity.startActivityForResult(intent, REQ_CODE_BOC);
    }

    @Override
    public void refund(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.yada.spos.cashierplatfrom",
                "com.yada.spos.cashierplatfrom.InvokeActivity"));
        //
        curNo = param.getString(ORDER);
        respTransType = RESP_REFUND;
        //
        JSONObject jo = JSON.parseObject(param.getString(REFUND_PARAM));

        ArrayList<String> ks = new ArrayList<>();
        ArrayList<String> vs = new ArrayList<>();
        int payType = Integer.parseInt(jo.getString("payType"));
        ks.add("channelName");
        vs.add(PAY_TYPE_VAL[payType]);

        ks.add("tranType");
        vs.add(REFUND_TT_TYPE[payType]);

        ks.add("appOrderNo");
        vs.add(jo.getString("appOrderNo"));

        ks.add("traceNo");
        vs.add(jo.getString("traceNo"));

        if (payType != PAY_TYPE_CARD)
        {
            ks.add("orderNo");
            vs.add(jo.getString("orderNo"));
        }
        insertKV(intent, ks.toArray(new String[]{}), vs.toArray(new String[]{}));
        activity.startActivityForResult(intent, REQ_CODE_BOC);
    }

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, int payType, Intent data)
    {
        if (requestCode != REQ_CODE_BOC) return null;
        boolean status = resultCode != AbsActivity.RESULT_CANCELED;

        JSONObject jo = new JSONObject();
        for (String key : data.getExtras().keySet())
        {
            jo.put(key, data.getExtras().getString(key));
        }
        jo.put(PAY_TYPE, String.valueOf(payType));
        jo.put(OPER_STATUS, status);
        if (!status)
        {
            jo.put(OPER_ERROR, data.getStringExtra("msg"));
        }
        return jo;
    }*/
}
