package com.lk.td.pay.beans;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: TCPayResponse
 * @Description: 通用解析类
 * @author lukejun
 * @date 2013-12-3 下午2:19:20
 * 
 */
public class Response implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String RSPCOD = "RSPCOD";
	public static final String RSPMSG = "RSPMSG";
	public static final String STATUS_OK = "000000";
	public static final String LOGIN_EXPIRE = "099990";
	private String status_code = "";
	private String status_msg = "";

	public static Response parseEntity(String paramString) {
		Response response = new Response();
		if ((paramString == null) || (paramString.equals(""))) {
			response = null;
			return response;
		}
		try {
			JSONObject jsonObject = new JSONObject(paramString);
			response.setStatus_code(jsonObject.getString(RSPCOD));
			response.setStatus_msg(jsonObject.getString(RSPMSG));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	public String getStatus_code() {
		return this.status_code;
	}

	public String getStatus_msg() {
		return this.status_msg;
	}

	public void setStatus_code(String paramString) {
		this.status_code = paramString;
	}

	public void setStatus_msg(String paramString) {
		this.status_msg = paramString;
	}

}