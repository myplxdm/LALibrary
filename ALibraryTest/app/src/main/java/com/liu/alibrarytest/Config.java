package com.liu.alibrarytest;

/**
 * Created by liu on 2017/8/18.
 */

public class Config
{
    public static final int LIST_PAGE_SIZE = 20;
    public static final int IMAGE_MAX_PX = 1000;
    public static final String HOST = "http://192.168.3.24:5050";
//    public static final String HOST = "http://water.chalive.cn";
    //api
    public static final String API_LOGIN_URL = HOST + "/api/login?";//登录
    public static final String API_GET_VER_CODE_URL = HOST + "/api/sms?";//获取手机验证码
    public static final String API_BIND_URL = HOST + "/api/RegAuth?";//绑定微信与手机号
    public static final String API_VERSION_URL = HOST + "/api/appversion?type=android";//获取版本号
    public static final String API_UPLOAD_FILE_URL = HOST + "/api/UpdatePortrait";//上传文件
    public static final String API_WATER_INFO_URL = HOST + "/api/indexAPI?";//水掌柜
    public static final String API_CW_URL = HOST + "/api/heart?";//公益数据
    //
    public static final String WEB_AGREEMENT_URL = HOST + "";
    public static final String WEB_ADD_ORDER_URL = HOST + "/shop/orders/up";//添加订单
    public static final String WEB_WAIT_PAY_URL = HOST + "/shop/orders/list?status=wait";//待支付
    public static final String WEB_WAIT_DELIVER_URL = HOST + "/shop/orders/list?status=pay";//待发货
    public static final String WEB_ARREARS_URL = HOST + "/user/credit/list";//挂帐中
    public static final String WEB_MONTH_TURNOVER_URL = HOST + "/user/settle/recordlist";//本月成交
    public static final String WEB_MONTH_SERVICE_URL = HOST + "/user/settle/settlelist";//本月服务费收入
    public static final String WEB_UNSETTLED_SERVICE_URL = HOST + "/user/settle/balancelist";//未结算服务费
    public static final String WEB_CUSTOMER_URL = HOST + "/user/member/list";//客户管理
    public static final String WEB_ORDER_SEARCH = HOST + "/user/orders/list";//订单查询
    public static final String WEB_CW_SELLER_URL = HOST + "/coupon/partnerlist";//联盟商家
    //my
    public static final String WEB_MY_ADDR_URL = HOST + "/user/address/list";//收货地址
    public static final String WEB_MY_FEEDBACK_URL = HOST + "/user/info/feedback";//意见反馈
    public static final String WEB_MY_ABOUT_US_URL = HOST + "/user/info/aboutus";//关于我们
    public static final String WEB_MY_MANUAL_URL = HOST + "/user/info/handbook";//产品手册
    public static final String WEB_MY_SERVICE_URL = HOST + "/user/info/saleservice";//售后服务
    public static final String WEB_EMPL_BUY_URL = HOST + "/goods/wealList";//员工内购
}
