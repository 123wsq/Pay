package com.lk.td.pay.beans;

import com.newland.mtype.module.common.swiper.SwipResult;

import java.io.Serializable;

public class PosData implements Serializable {

    private static final long serialVersionUID = -8535989799281050288L;
    private static PosData posData;

    private PosData() {

    }

    public static PosData getPosData() {
        if (posData == null) {
            posData = new PosData();
        }
        return posData;
    }

    public void clearPosData() {
        posData = null;
    }

    private String prdordNo;   //订单号
    private String payType;    //支付方式：01支付账户02终端03快捷
    private String rate;       //费率类型1 民生类2 一般类3 餐娱类4 批发类5 房产类
    private String termNo;     //终端号

    private String termType;   //01蓝牙02音频
    private String payAmt;     //交易金额
    private String track;      //磁道信息
    private String random;     //随机数
    private String mediaType;  //磁卡类型01 磁条卡 02 IC卡
    private String period;     //有效期
    private String icdata;     //55域
    private String serviceCode;     //服务代码
    private String err; //错误
    private SwipResult swipResult;  //刷卡返回信息

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public static void setPosData(PosData posData) {
        PosData.posData = posData;
    }

    private String crdnum;     //卡序列号
    private String cardNo = "";  //银行卡号
    private String pinKey;     //密码密钥
    private String mac;
    private String ctype;
    private String pinblk;     //硬加密
    private String acttext;


    public String getActtext() {
        return acttext;
    }

    public void setActtext(String acttext) {
        this.acttext = acttext;
    }

    public String getPinblk() {
        return pinblk;
    }

    public void setPinblk(String pinblk) {
        this.pinblk = pinblk;
    }


    /**
     * 蓝牙设备
     *
     * @param termNo
     */


    public void setBluetoothTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getBluetoothTermNo() {
        return termNo;
    }

    public String getPrdordNo() {
        return prdordNo;
    }

    public void setPrdordNo(String prdordNo) {
        this.prdordNo = prdordNo;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }


    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(String payAmt) {
        this.payAmt = payAmt;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getIcdata() {
        return icdata;
    }

    public void setIcdata(String icdata) {
        this.icdata = icdata;
    }

    public String getCrdnum() {
        return crdnum;
    }

    public void setCrdnum(String crdnum) {
        this.crdnum = crdnum;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getPinKey() {
        return pinKey;
    }

    public void setPinKey(String pinKey) {
        this.pinKey = pinKey;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }


    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public SwipResult getSwipResult() {
        return swipResult;
    }

    public void setSwipResult(SwipResult swipResult) {
        this.swipResult = swipResult;
    }
}
