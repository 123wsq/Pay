package com.lk.td.pay.utils;

import java.util.Map;

import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.activity.main.cashin.ShowMsgActivity;
import com.lk.td.pay.beans.PosData;
import com.pay.code.activity.CreateQRCodeActivity;
import com.pay.library.config.AppConfig;
import com.pay.library.request.IntentParams;
import com.pay.library.uils.AmountUtils;
import com.pay.library.uils.HStartActivity;
import com.td.app.pay.hx.R;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

public class RequestListener implements OnStateListener{

	@Override
	public void onPaySuccess(Context context, Map<String, Object> map) {
		// TODO Auto-generated method stub
		String payType = PosData.getPosData().getPayType();
		
	
		//判断交易类型
		if (!TextUtils.isEmpty(payType)) {
				
			if (payType.trim().equals(AppConfig.PAYTYPE.PAY_ACCOUNT)) {
				
			}else if (payType.trim().equals(AppConfig.PAYTYPE.TERM)) {
				
				HStartActivity.startActivity(context, ShowMsgActivity.class, map, "ACTION_CASH_IN");
				
			}else if (payType.trim().equals(AppConfig.PAYTYPE.QUICK)) {
				
			}else if (payType.trim().equals(AppConfig.PAYTYPE.QR_CODE)) {
				
//				map.put(IntentParams.TITLE, context.getString(R.string.pay_scan_qr_code_title));
//				map.put(IntentParams.CODE_MSG_SHARE, 
//						String.format(context.getString(R.string.qr_message),
//								AmountUtils.changeFen2Yuan(map.get(IntentParams.AMOUNT).toString())
//								)
//						);
//				map.put(IntentParams.SHOW_BTN, false);
//				map.put(IntentParams.SCAN_THREAD, true);
//				
//				HStartActivity.startActivity(context, CreateQRCodeActivity.class, map);
			}
			
			PosData.getPosData().clearPosData();
			((Activity)context).finish();
		}else{
			showAlertDialog(context, "提示", "交易类型为空！");
		}
		
		
	}

	@Override
	public void onPayFail(Context context, Map<String, Object> map) {
		// TODO Auto-generated method stub
		HStartActivity.startActivity(context, ShowMsgActivity.class, map, "ACTION_CASH_IN");
		
		((Activity)context).finish();
	}

	@Override
	public void showAlertDialog(Context context, String title, String msg) {
		// TODO Auto-generated method stub
		
		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setTitle(title)
		.setMessage(msg);
		
		builder.setCancelBtn("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
			}
		});
		
		builder.create().show();
		
	}
	
	
	
	
	
	
}
