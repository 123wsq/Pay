package com.lk.td.pay.beans;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;


public class CardBalance implements Serializable {

	private static final long serialVersionUID = 7314798688932428397L;
	protected String RSPCOD;
	protected String RSPMSG;
	private boolean isSuccess = false;
	private String msg;
	private byte[] response;
	private String balance;
	public CardBalance(byte[] response){
		this.response=response;
	}

	public CardBalance getResult() throws JSONException {
		if (response != null) {
			JSONObject obj = new JSONObject(new String(response))
					.getJSONObject("REP_BODY");
			this.msg = obj.optString("RSPMSG");
			if (obj.optString("RSPCOD").equals("000000")) {
				balance = obj.optString("balance");
				//balance=AmountUtils.changeFen2Yuan(balance);
				isSuccess = true;
			}else if(obj.optString("RSPCOD").equals("888888")){
				//长时间未操作超时
				
			} else {
				isSuccess = false;
			}
			return this;
		}
		return null;
	}

	public String getRSPCOD() {
		return RSPCOD;
	}

	public void setRSPCOD(String rSPCOD) {
		RSPCOD = rSPCOD;
	}

	public String getRSPMSG() {
		return RSPMSG;
	}

	public void setRSPMSG(String rSPMSG) {
		RSPMSG = rSPMSG;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public String getMsg() {
		return msg;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	
}
