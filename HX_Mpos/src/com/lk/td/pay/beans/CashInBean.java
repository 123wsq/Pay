package com.lk.td.pay.beans;

public class CashInBean {

    private String prdordtype;
    private String fee;
    private String fjname;
    private String fjpath;
    private String amt;
    private String payType;
    private String payCardNo;
    private String rate;
    private String rateType;
    private String termNo;
    private String txamt;
    private String custId;

    private String custName;
    private String prdordno;
    private String ordamt;
    /**
     * 00:未交易 01:成功 02:失败 03:可疑 04:处理中 05:已取消 06:未支付 07:已退货 08:退货中 09:部分退货
     */
    private String ordstatus;
    private String ordtime;
    private String price;

    public String getPrdordtype() {
        return prdordtype;
    }

    public void setPrdordtype(String prdordtype) {
        this.prdordtype = prdordtype;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFjname() {
        return fjname;
    }

    public void setFjname(String fjname) {
        this.fjname = fjname;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayCardNo() {
        return payCardNo;
    }

    public void setPayCardNo(String payCardNo) {
        this.payCardNo = payCardNo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getTxamt() {
        return txamt;
    }

    public void setTxamt(String txamt) {
        this.txamt = txamt;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getPrdordno() {
        return prdordno;
    }

    public void setPrdordno(String prdordno) {
        this.prdordno = prdordno;
    }

    public String getOrdamt() {
        return ordamt;
    }

    public void setOrdamt(String ordamt) {
        this.ordamt = ordamt;
    }

    public String getOrdstatus() {
        return ordstatus;
    }

    public void setOrdstatus(String ordstatus) {
        this.ordstatus = ordstatus;
    }

    public String getOrdtime() {
        return ordtime;
    }

    public void setOrdtime(String ordtime) {
        this.ordtime = ordtime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getFjpath() {
        return fjpath;
    }

    public void setFjpath(String fjpath) {
        this.fjpath = fjpath;
    }
}
