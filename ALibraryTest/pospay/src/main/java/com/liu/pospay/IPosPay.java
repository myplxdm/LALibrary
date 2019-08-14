package com.liu.pospay;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;

/**
 * Created by liu on 2018/8/21.
 */

public interface IPosPay
{
    public final static int REQ_CODE_BOC = 0x1020;//中行总行
    public final static int REQ_CODE_BOC_BRANCH = 0x1021;//中行分行
    public final static int REQ_CODE_HF = 0x1022;//汇付

    public final static String PAY_TYPE = "payType";//支付类型
    public final static String ORDER = "order";//订单号
    public final static String PRICE = "price";//价格

    public final int PAY_TYPE_WX = 0;//微信
    public final int PAY_TYPE_ALIPAY = 1;//支付宝
    public final int PAY_TYPE_CARD = 2;//银行卡
    public final int PAY_TYPE_YL = 3;//银联钱包
    //
    public final static String REFUND = "refund";
    public final static String REFUND_PARAM = "refParam";
    public final static String IS_CANCEL = "isCancel";//是否为撤消
    //
    public final static String OPER_STATUS = "operStatus";//操作状态
    public final static String OPER_ERROR = "operError";//出错提示

    public final int REQ_PAY = 0x10;
    public final int REQ_REFUND = 0x11;
    public final int RESP_PAY = 0x20;
    public final int RESP_REFUND = 0x21;
    //
    public JSONObject onResponse(int requestCode, int resultCode, Intent data);
    public void pay(AbsActivity activity, String curNo, String price, int payType);
    public void refund(AbsActivity activity, boolean isCancel, String curNo, String price, String param);
    public void signIn(AbsActivity activity);//签到
    public void query(AbsActivity activity, JSONObject param);
    public boolean isActivityResult();
}
