package com.pay.library.config;

import com.pay.library.config.AppConfig;


public class Urls {

    public static final String BACKGROUND_KEY = "123456";
    public static final String SUFFIX = ".json";
    public static String LOGIN = "SY0003" + SUFFIX;
    public static String REGISTER = "SY0002" + SUFFIX;
    public static String GET_VERIFY = "SY0001" + SUFFIX;
    public static String CHECK_VERIFY = "SY0013" + SUFFIX;
    // 4.1.5 商户信息查询
    public static String GET_USER_INFO = "SY0004" + SUFFIX;
    public static String UPDATE_PWD = "SY0005" + SUFFIX;
    public static String POS_BING = "TE0001" + SUFFIX;
    public static String BLUETOOTH_SIGN = "SG0002" + SUFFIX;
    public static String SIGN_TDK = "SG0003" + SUFFIX;
    //银行名称查询
    public static String BANKNAME = "BU0001" + SUFFIX;
    //支行名称查询
    public static String BANKLIST = "BU0002" + SUFFIX;
    // 公告
    public static String SYSTEM_MESSAGE = "SY0011" + SUFFIX;
    //记录已阅公告
    public static String SET_MESSAGE = "SY0020" + SUFFIX;
    // 检查更新
    public static String CHECK_UPDATE = "SY0009" + SUFFIX;

    // 首页轮播图
    public static String MAIN_AD_IMG = "SY0010" + SUFFIX;
    // 获取商户绑定终端
    public static String BIND_DEVICE_LIST = "TE0002" + SUFFIX;
    // 获取终端费率列表
    public static String TERM_DEVICE_LIST = "TE0003" + SUFFIX;
    // 银行卡相关操作
    public static String BANKCARD_EDIT = "SY0008" + SUFFIX;
    // 银行卡列表查询
    public static String GET_BANKCARD_LIST = "SY0015" + SUFFIX;
    // 4.2.4 商品订单(下单)
    public static String CREATE_ORDER = "OD0001" + SUFFIX;
    // 查询交易记录
    public static String TRADE_RECORDS = "TR0001" + SUFFIX;
    // 支付
    public static String TRADE_PAY = "PY0001" + SUFFIX;
    // 银行卡查询
    public static String CARD_QUERY = "PY0003" + SUFFIX;
    // 城市列表
    public static String PROVINCE = "SY0012" + SUFFIX;
    //提现
    public static String WITHFRAW = "PY0004" + SUFFIX;

    public static String IDENTITY_CHECH = "SY0007" + SUFFIX;
    //4.2.9	上传电子签名照片
    public static String UPLOAD_SIGNTURE = "UP0001" + SUFFIX;
    //上传银行卡扫描结果
    public static String UPLOAD_BANK_CARD_IMAGE = "UP0002" + SUFFIX;
    //4.2.10	查询余额
    public static String QUERY_BALANCE = "GB0001" + SUFFIX;
    //4.2.7	试计算手续费
    public static String CALC_FEE = "GB0002" + SUFFIX;
    public static String PROFIT_QUERY = "PF0001" + SUFFIX;
    
    /**
     * 扫码收款
     */
    public static String  QR_QUERY= "SM0001"+ SUFFIX;
    

    /**
     * @param serverType
     * @Title: initServer
     * @Description: 接口环境选择、控件环境选择、日志开关选择
     */
    public static void initServer(String serverType) {
        if ("请输入地址".equals(serverType)) {
            serverType = "";
        }
        if (!"".equals(serverType) && serverType != null) { // 测试环境
            AppConfig.HOST = "http://" + serverType + "/mpcctp/";
        }
        System.out.println("====================urls===========>" + AppConfig.HOST);
    }
}
