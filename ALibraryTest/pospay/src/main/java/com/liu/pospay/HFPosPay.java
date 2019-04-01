package com.liu.pospay;

import android.content.Intent;
import android.net.Uri;

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
        return null;
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
        //String mpt = PAY_TYPE_VAL[payType];

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
        JSONObject jo = JSON.parseObject(param);
        String req;
        if (isCancel)
        {
            req = String.format("%s://%s/paymentVoid?" +
                            "oriVoucherNo=%s&" +
                            "merOrdId=%s&",SCHEME, HOST,
                    jo.getString("voucherNo"), curNo);
        }else
        {
            req = String.format("%s://%s/refund?" +
                            "ordAmt=%s&" +
                            "oriMerOrdId=%s&",SCHEME, HOST,
                    jo.getString("merOrdId"), curNo);
        }
        Uri uri = Uri.parse(req);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQ_CODE_HF);
    }

    /*
    @Override
    public void pay(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        curNo = param.getString(ORDER);
        respTransType = RESP_PAY;
        //
        int payType = param.getIntValue(PAY_TYPE);
        String cid = payType == PAY_TYPE_CARD ? PAY_TYPE_VAL[2] : "scan";
        //String mpt = PAY_TYPE_VAL[payType];

        String req = String.format("%s://%s/payment?" +
                        "channelId=%s&" +
                        "ordAmt=%s&" +
                        "merOrdId=%s&", SCHEME, HOST,
                        cid, param.getString(PRICE), curNo);
        Uri uri = Uri.parse(req);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQ_CODE_HF);
    }

    @Override
    public void refund(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        curNo = param.getString(ORDER);
        respTransType = RESP_REFUND;
        //
        JSONObject jo = JSON.parseObject(param.getString(REFUND_PARAM));
        String req;
        if (param.getBoolean(IS_CANCEL))
        {
            req = String.format("%s://%s/paymentVoid?" +
                            "oriVoucherNo=%s&" +
                            "merOrdId=%s&",SCHEME, HOST,
                            jo.getString("voucherNo"), curNo);
        }else
        {
            req = String.format("%s://%s/refund?" +
                            "ordAmt=%s&" +
                            "oriMerOrdId=%s&",SCHEME, HOST,
                            jo.getString("merOrdId"), curNo);
        }
        Uri uri = Uri.parse(req);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQ_CODE_HF);
    }

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, int payType, Intent data)
    {
        if (requestCode != REQ_CODE_BOC) return null;
        return null;
    }*/
}
