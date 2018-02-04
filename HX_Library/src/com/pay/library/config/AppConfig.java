package com.pay.library.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsq on 2016/5/23.
 */
public class AppConfig {
    
    /**
     * 测试环境地址
     */
//    public static String HOST = "http://103.47.137.53:8098/mpcctp/";

    /**
     * 生产环境地址
     */
    public static String HOST = "http://103.47.137.51:8098/mpcctp/";

    /**
     * 服务协议
     */
    public static String GET_AGREEMENT = "http://103.47.137.51:8899/pay/test/agreement.html";
    
    /**
     * 分享路径
     */
    public static String SHARE_URL = "http://103.47.137.51:8899/pay/scan/register_cust.html?cust_Id=";

    /**
     * 主界面显示的列数
     */
    public static final int COLUMNS = 3;
    //
    public static final boolean DEBUG = true;
    
    /**
     * 设置二维码交易检查的轮询时间
     */
    public static final int QR_CODE_PAY_TIME = 5 * 1000;
    
    /**
     * 设置扫码交易时的延迟时间
     */
    public static final int QR_CODE_DELAY_TIME = 8 * 1000;
    /**
     * 默认终端号
     */
    public static final String DEFAULT_TERMNAL_NO = "999999999";
    
    
    public static class DATA{
    	
    	public static final String POPUP_TRAN_DATA = "data/popup_data";
    	
    	public static final String POPUP_SCAN_TYPE_DATA = "data/scan_type";
    	
    }
    
    
    /***
     * 支付类型
     * @author wsq
     *
     */
    public static class PAYTYPE{
    	
    	static Map<String, String> map  = new HashMap<String, String>();
    	/**
    	 * 支付账户
    	 */
    	public static final String  PAY_ACCOUNT = "01";
    	
    	/**
    	 * 终端
    	 */
    	public static final String TERM = "02";
    	
    	/**
    	 * 快捷支付
    	 */
    	public static final String QUICK = "03";
    	
    	/**
    	 * 扫码支付
    	 */
    	public static final String QR_CODE = "04";
    	
    	
    	static{
    		map.put(PAY_ACCOUNT, "支付账户");
    		map.put(TERM, "终端");
    		map.put(QUICK, "快捷支付");
    		map.put(QR_CODE, "扫码支付");
    	}
    	
    	public static  String getValue(String key){
    		
    		return map.get(key)==null ? "" : map.get(key);
    	}
    	
    }
    
    
    /***
     *提现方式
     * @author wsq
     *
     */
    public static class ACCTTYPE{
    	
    	static Map<String, String> map  = new HashMap<String, String>();
    	/**
    	 * 支付账户
    	 */
    	public static final String  PAY_ACCOUNT = "01";
    	
    	/**
    	 * 终端
    	 */
    	public static final String TERM = "02";
    	
    	/**
    	 * 快捷支付
    	 */
    	public static final String QUICK = "03";
    	
    	/**
    	 * 扫码支付
    	 */
    	public static final String QR_CODE = "04";
    	
    	
    	static{
    		map.put(PAY_ACCOUNT, "所有账户");
    		map.put(TERM, "普通收单账户");
    		map.put(QUICK, "快捷交易账户");
    		map.put(QR_CODE, "扫码支付");
    		
    	}
    	
    	public static String getValue(String key){
    		
    		return map.get(key)==null ? "" : map.get(key);
    	}
    	
    }
    
    /**
     * 提现类型
     * @author wsq
     *
     */
    public static class CASTYPE{
    	
    	
    	/**
    	 * T0提现
    	 */
    	public static final String CAS_TYPE_T0 = "1";
    	
    	/**
    	 * T1提现
    	 */
    	public static final String CAS_TYPE_T2 = "2";
    	
    	/**
    	 * 3:不区分混合式提现
    	 */
    	public static final String CAS_TYPE_UN = "3";
    	
    	
    }
    
    /**
     * 业务类型
     * @author wsq
     *
     */
    public static class BUSTYPE{
    	static Map<String, String> map  = new HashMap<String, String>();
    	/**
    	 * 所有
    	 */
    	public static final String BUS_TYPE_00 = "00";
    	
    	/**
    	 * 收款
    	 */
    	public static final String BUS_TYPE_01 = "01";
    	
    	/**
    	 * 消费
    	 */
    	public static final String BUS_TYPE_02 = "02";
    	/**
    	 * 提现
    	 */
    	public static final String BUS_TYPE_03 = "03";
    	
    	static{
    		map.put(BUS_TYPE_00, "所有");
    		map.put(BUS_TYPE_01, "收款");
    		map.put(BUS_TYPE_02, "消费");
    		map.put(BUS_TYPE_03, "提现");
    	}
    	
    	public static String getValue(String key){
    		return map.get(key)==null ? "" : map.get(key);
    	}
    	
    }
    
    /**
     * 订单状态
     * @author wsq
     *
     */
    
    public static class ORDSTATUS{
    	static Map<String, String> map  = new HashMap<String, String>();
    	
    	/**
    	 * 未交易
    	 */
    	public static final String ORD_STATUS_00 = "00";
    	
    	/**
    	 * 成功
    	 */
    	public static final String ORD_STATUS_01 = "01";
    	
    	/**
    	 * 失败
    	 */
    	public static final String ORD_STATUS_02 = "02";
    	
    	/**
    	 * 可疑
    	 */
    	public static final String ORD_STATUS_03 = "03";
    	
    	/**
    	 * 处理中
    	 */
    	public static final String ORD_STATUS_04 = "04";
    	
    	/**
    	 * 已取消
    	 */
    	public static final String ORD_STATUS_05 = "05";
    	
    	
    	/**
    	 * 未支付
    	 */
    	public static final String ORD_STATUS_06 = "06";
    	
    	/**
    	 * 已退货
    	 */
    	public static final String ORD_STATUS_07 = "07";
    	
    	/**
    	 * 退货中
    	 */
    	public static final String ORD_STATUS_08 = "08";
    	
    	/**
    	 * 部分退货
    	 */
    	public static final String ORD_STATUS_09 = "09";
    	
    	
    	
    	static{
    		
    		map.put(ORD_STATUS_00, "未交易");
    		map.put(ORD_STATUS_01, "成功");
    		map.put(ORD_STATUS_02, "失败");
    		map.put(ORD_STATUS_03, "可疑");
    		map.put(ORD_STATUS_04, "处理中");
    		map.put(ORD_STATUS_05, "已取消");
    		map.put(ORD_STATUS_06, "未支付");
    		map.put(ORD_STATUS_07, "已退货");
    		map.put(ORD_STATUS_08, "退货中");
    		map.put(ORD_STATUS_09, "部分退货");
    		
    	} 
    	
    	public static String getContent(String key){
    		
    		return map.get(key)==null ? "" : map.get(key);
    	}
    	
    }
    
    /**
     * 二维码扫描的类型
     * @author wsq
     *
     */
    public static class SCANTYPE{
    	
    	static Map<String, String> map  = new HashMap<String, String>();
    	
    	/**
    	 * 微信支付
    	 */
    	public static final String SCAN_TYPE_00 = "00";
    	
    	static{
    		map.put(SCAN_TYPE_00, "微信");
    	}
    	
    	public static String getScanType(String key){
    		
    		return map.get(key)==null ? "": map.get(key);
    	}
    	
    	
    }

}
