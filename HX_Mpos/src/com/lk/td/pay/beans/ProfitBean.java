package com.lk.td.pay.beans;

import java.io.Serializable;

public class ProfitBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7980997323325545379L;
	private String refrence;
	private String custId;
	private String mngAmt;
	private String custClass;
	private String profitId;
	private String profitDate;
	private String txnAmt;
	public String getRefrence() {
		return refrence;
	}
	public void setRefrence(String refrence) {
		this.refrence = refrence;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getMngAmt() {
		return mngAmt;
	}
	public void setMngAmt(String mngAmt) {
		this.mngAmt = mngAmt;
	}
	public String getCustClass() {
		return custClass;
	}
	public void setCustClass(String custClass) {
		this.custClass = custClass;
	}
	public String getProfitId() {
		return profitId;
	}
	public void setProfitId(String profitId) {
		this.profitId = profitId;
	}
	public String getProfitDate() {
		return profitDate;
	}
	public void setProfitDate(String profitDate) {
		this.profitDate = profitDate;
	}
	public String getTxnAmt() {
		return txnAmt;
	}
	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}
	
	

}
