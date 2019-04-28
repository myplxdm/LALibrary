package com.liu.pospay;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;

/**
 * Created by liu on 2018/8/21.
 */
//联迪中行分行pos
public class BocBranchPosPay extends BasePosPay
{
    private final String[] PAY_TYPE_VAL = { "01", "02", "03" };

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != REQ_CODE_BOC_BRANCH) return null;
        boolean status = resultCode != AbsActivity.RESULT_CANCELED;
        JSONObject jo;
        String str = null;
        if (data != null && !TextUtils.isEmpty (str = data.getExtras().getString("transData")))
        {
            jo = JSON.parseObject(str);
            if (!status)
            {
                jo.put(OPER_ERROR, data.getStringExtra("reason"));
            }
        }else jo = new JSONObject();

        jo.put("orderNo", curNo);
        jo.put(PAY_TYPE, String.valueOf(payType));
        jo.put(OPER_STATUS, status);

        return jo;
    }

    @Override
    public void pay(AbsActivity activity, String curNo, String price, int payType)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        this.payType = payType;
        this.curNo = curNo;
        this.respTransType = RESP_PAY;
        String[] ks = { "trans_name", "trans_type", "out_trade_no", "amount" };
        String[] vs = new String[4];
        vs[0] = "消费";
        vs[1] = this.PAY_TYPE_VAL[payType];
        vs[2] = this.curNo;
        vs[3] = price;
        insertKV(intent, ks, vs);
        activity.startActivityForResult(intent, REQ_CODE_BOC_BRANCH);
    }

    @Override
    public void refund(AbsActivity activity, boolean isCancel, String curNo, String price, String param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        this.curNo = curNo;
        this.respTransType = RESP_REFUND;
        String[] ks = { "trans_name", "amount", "orig_out_trade_no", "out_refund_no", "trans_type" };
        String[] vs = new String[5];
        JSONObject json = JSON.parseObject(param);

        int payType = Integer.parseInt(json.getString("payType"));
        String payDate = json.getString("date");
        if (payType == PAY_TYPE_CARD && !TextUtils.isEmpty(payDate))
        {
            isCancel = getCurDateStr().equals(payDate);
        }
        vs[0] = payType == PAY_TYPE_CARD ? (isCancel ? "手动退款" : "退货") : "手动退款";
        vs[1] = price;
        vs[2] = payType == PAY_TYPE_CARD ? json.getString("traceNo") : json.getString("orderNo");
        vs[3] = curNo;
        vs[4] = this.PAY_TYPE_VAL[payType];
        insertKV(intent, ks, vs);
        activity.startActivityForResult(intent, REQ_CODE_BOC_BRANCH);
    }
    /*
    private final String[] PAY_TYPE_VAL = new String[]{"01","02","03"};
    public final int PAY_TYPE_CARD = 2;//银行卡

    @Override
    public void pay(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        //
        curNo = param.getString(ORDER);
        respTransType = RESP_PAY;
        String[] ks = new String[]{"trans_name","trans_type","out_trade_no","amount"};
        String[] vs = new String[]{"消费", PAY_TYPE_VAL[param.getIntValue(PAY_TYPE)],
                curNo, param.getString(PRICE)};
        insertKV(intent, ks, vs);
        activity.startActivityForResult(intent, REQ_CODE_BOC_BRANCH);
    }

    @Override
    public void refund(AbsActivity activity, JSONObject param)
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        //
        curNo = param.getString(ORDER);
        respTransType = RESP_REFUND;

        String[] ks = new String[]{"trans_name","amount","orig_out_trade_no","out_refund_no","trans_type"};
        JSONObject jo = JSON.parseObject(param.getString(REFUND_PARAM));
        String ootn;
        int payType = Integer.parseInt(jo.getString("payType"));
        if (payType == PAY_TYPE_CARD)
        {
            ootn = jo.getString("traceNo");
        }else
        {
            ootn = jo.getString("orderNo");
        }
        String[] vs = new String[]{"手动退款",param.getString(PRICE),
                ootn,param.getString(REFUND),PAY_TYPE_VAL[payType]};
        insertKV(intent, ks, vs);
        activity.startActivityForResult(intent, REQ_CODE_BOC_BRANCH);
    }

    @Override
    public JSONObject onResponse(int requestCode, int resultCode, int payType, Intent data)
    {
        if (requestCode != REQ_CODE_BOC_BRANCH) return null;
        boolean status = resultCode != AbsActivity.RESULT_CANCELED;

        JSONObject jo = new JSONObject();
        jo.put("orderNo", curNo);
        jo.put(PAY_TYPE, String.valueOf(payType));
        jo.put(OPER_STATUS, status);
        if (!status)
        {
            jo.put(OPER_ERROR, data.getStringExtra("reason"));
        }
        return jo;
    }
    */

}
