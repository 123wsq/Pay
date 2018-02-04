package com.lk.td.pay.utils;

import java.util.Map;

import android.content.Context;

public interface OnStateListener {

	/**
	 * 交易成功 PY0001
	 * @param context
	 * @param map
	 * @param action
	 */
	void onPaySuccess(Context context, Map<String, Object> map);
	
	/**
	 * 交易成功 PY0001
	 * @param context
	 * @param map
	 * @param action
	 */
	void onPayFail(Context context, Map<String, Object> map);
	
	
	
	
	void showAlertDialog(Context context, String title, String msg);
}
