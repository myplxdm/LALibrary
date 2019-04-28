package com.liu.pospay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;

/**
 * Created by liu on 2018/8/22.
 */

public class HFPosPay extends BasePosPay
{
    private final String SCHEME = "payment";
    private final String HOST = "com.pnr.pospp";
    private final String[] PAY_TYPE_VAL = new String[]{"W","A","acquire","U"};

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != REQ_CODE_HF) return null;
        JSONObject jo = new JSONObject();
        Bundle bundle = null;
        boolean status = false;
        if (data != null && (bundle = data.getExtras()) != null)
        {
            for (String key : bundle.keySet())
            {
                jo.put(key, bundle.getString(key));
            }
            String code = jo.getString("responseCode");
            status = code.equals("00");
            if (!status)
            {
                String err = jo.getString("message");
                jo.put(OPER_ERROR, TextUtils.isEmpty(err) ? getErrString(code) : err);
            }
        }
        jo.put(PAY_TYPE, String.valueOf(payType));
        jo.put(OPER_STATUS, status);//
        return jo;
    }

    @Override
    public void pay(AbsActivity activity, String curNo, String price, int payType)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        this.curNo = curNo;
        respTransType = RESP_PAY;
        //
        this.payType = payType;
        String cid = payType == PAY_TYPE_CARD ? PAY_TYPE_VAL[2] : "scan";
        String req = String.format("%s://%s/payment?" +
                        "channelId=%s&" +
                        "ordAmt=%s&" +
                        "merOrdId=%s&", SCHEME, HOST,
                cid, price, curNo);
        Uri uri = Uri.parse(req);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQ_CODE_HF);
    }

    @Override
    public void refund(AbsActivity activity, boolean isCancel, String curNo, String price, String param)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        this.curNo = curNo;
        respTransType = RESP_REFUND;
        //
        JSONObject json = JSON.parseObject(param);
        String payDate = json.getString("transDate");
        int payType = Integer.parseInt(json.getString("payType"));
        if (!TextUtils.isEmpty(payDate))
        {
            isCancel = getCurDateStr().equals(payDate);
        }
        String[] keys,vals;
        String req;
        if (isCancel)
        {
            if (payType == PAY_TYPE_CARD)
            {
                keys = new String[]{"channelId","oriVoucherNo","merOrdId"};
                vals = new String[]{"acquire",json.getString("voucherNo"),curNo};
            }else
            {
                keys = new String[]{"channelId","mobilePayType","oriVoucherNo","merOrdId"};
                vals = new String[]{"scan",json.getString("mobilePayType"),json.getString("voucherNo"),curNo};
            }
            req = String.format("%s://%s/paymentVoid?",SCHEME, HOST) + catParam(keys,vals);
        }else
        {
            keys = new String[]{"ordAmt","oriSelfOrdId","merOrdId"};
            vals = new String[]{price,json.getString("merOrdId"),curNo};
            req = String.format("%s://%s/refund?",SCHEME, HOST) + catParam(keys,vals);
        }
        Uri uri = Uri.parse(req);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQ_CODE_HF);
    }

    private String catParam(String[] keys, String[] vals)
    {
        if (keys != null && vals != null && keys.length == vals.length)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(keys[0] + "=" + vals[0]);
            for (int i = 1;i < keys.length;i++)
            {
                sb.append("&" + keys[i] + "=" + vals[i]);
            }
            return sb.toString();
        }
        return "";
    }

    private String getErrString(String code)
    {
        if (code.equals("TF")) return "交易失败";
        else if (code.equals("PE")) return "入参错误";
        else if (code.equals("UL")) return "未登录或取消登录";
        else if (code.equals("UF")) return "未查到";
        else if (code.equals("TP")) return "交易处理中";
        return "未知错误";
    }
}
