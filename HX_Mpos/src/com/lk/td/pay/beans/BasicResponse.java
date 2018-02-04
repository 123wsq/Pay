package com.lk.td.pay.beans;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.golbal.MApplication;
import com.pay.library.config.Actions;
import com.pay.library.request.IntentParams;
import com.pay.library.request.ParamsUtils;

public class BasicResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7314798688932428397L;
	protected String RSPCOD;
	protected String RSPMSG;
	protected String payUrl;
	private boolean isSuccess = false;
	private String msg;
	private byte[] response;
	private JSONObject jsonBody;
	public static final String LOGIN_EXPIRE = "888888";
	CustomDialog mDialog = null;
	// private JSONArray jsonArray;
	public BasicResponse(byte[] response) {
		this.response = response;
	}

	public BasicResponse getResult() throws JSONException {
		if (response != null) {
			JSONObject obj = new JSONObject(new String(response)).optJSONObject(ParamsUtils.RESULT_REP_BODY);
			this.jsonBody = obj;
			if (obj==null) {
				return this;
			}
			this.msg = obj.optString(ParamsUtils.RESULT_RSPMSG);
			this.payUrl = obj.optString(ParamsUtils.RESULT_PAY_URL);
			
			Intent intent = new Intent();
			intent.setAction(Actions.ACTION_DIALOG);
        	intent.putExtra(IntentParams.DIALOG_TITLE, "账户安全提示");
        	
        	final Context mContext = MApplication.getInstance().getMainHomeContext();
        	if (mContext == null) {
        		Log.d("===========", "=======Context 为空了=========");
				return this;
			}
        	if (obj.optString(ParamsUtils.RESULT_RSPCOD).equals("000000")) {

				isSuccess = true;
				
			} else if (obj.optString(ParamsUtils.RESULT_RSPCOD).equals("888889")) {
				isSuccess = false;
				// 长时间未操作超时

				intent.putExtra(IntentParams.DIALOG_MSG, "该账户已在其它设备登录！");
            	mContext.sendBroadcast(intent);
            	
			}else if (obj.optString("RSPCOD").equals("888888")){
				isSuccess = false;
				// 长时间未操作超时
				
				intent.putExtra(IntentParams.DIALOG_MSG, "账户长时间未操作,请重新登录.");
            	mContext.sendBroadcast(intent);
            	
            	
			} else if (obj.optString(ParamsUtils.RESULT_RSPCOD).equals("900001")){
				isSuccess = false;
				
				intent.putExtra(IntentParams.DIALOG_MSG, "服务器未响应,请重新登录.");
            	mContext.sendBroadcast(intent);
			}
			return this;
		}
		return this;
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

	public JSONObject getJsonBody() {
		return jsonBody;
	}

	public void setJsonBody(JSONObject jsonBody) {
		this.jsonBody = jsonBody;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	// public JSONArray getJsonArray() {
	// return jsonArray;
	// }
	//
	// public void setJsonArray(JSONArray jsonArray) {
	// this.jsonArray = jsonArray;
	// }
	
	
	

}
