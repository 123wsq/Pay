package com.lk.td.pay.beans;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class CashInResult extends Response implements Serializable{

	private static final long serialVersionUID = 1L;
	private String mercNam;
	private String actNo;   //付款方卡号
	private String ctxnAt;
	private String cpsCod;
	private String cTxnTm;
	
	public static CashInResult parserEntity(String paramString) {
		CashInResult result = new CashInResult();
		if ((paramString == null) || (paramString.equals(""))) {
			result = null;
			return result;
		}
		try {
			JSONObject localJSONObject = new JSONObject(paramString);
			result.setStatus_code(localJSONObject.getString(RSPCOD));
			result.setStatus_msg(localJSONObject.getString(RSPMSG));
			result.setActNo(localJSONObject.getString("AC_NO"));
			result.setCtxnAt(localJSONObject.getString("CTXNAT"));
			result.setCpsCod(localJSONObject.getString("CPSCOD"));
			result.setcTxnTm(localJSONObject.getString("CTXNTM"));
			result.setMercNam(localJSONObject.getString("MERCNAM"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getActNo() {
		return actNo;
	}

	public void setActNo(String actNo) {
		this.actNo = actNo;
	}

	public String getCtxnAt() {
		return ctxnAt;
	}

	public void setCtxnAt(String ctxnAt) {
		this.ctxnAt = ctxnAt;
	}

	public String getCpsCod() {
		return cpsCod;
	}

	public void setCpsCod(String cpsCod) {
		this.cpsCod = cpsCod;
	}

	public String getMercNam() {
		return mercNam;
	}

	public void setMercNam(String mercNam) {
		this.mercNam = mercNam;
	}

	public String getcTxnTm() {
		return cTxnTm;
	}

	public void setcTxnTm(String cTxnTm) {
		this.cTxnTm = cTxnTm;
	}
	
}
