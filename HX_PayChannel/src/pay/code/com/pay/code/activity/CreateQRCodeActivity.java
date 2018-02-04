package com.pay.code.activity;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.example.paychannel.R;
import com.hx.view.widget.CustomDialog;
import com.pay.library.config.Actions;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.request.BasicRequest;
import com.pay.library.request.IntentParams;
import com.pay.library.request.ParamsUtils;
import com.pay.library.tool.store.SharedPrefConstant;
import com.pay.library.uils.AmountUtils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class CreateQRCodeActivity extends Activity implements OnClickListener{


	ImageView qr_code;
	TextView tv_title, tv_code_title, tv_code_share;
	Button btn_back;
	private Button btn_native_pay;
	private String codeData;
	private Activity mContext;
	private boolean isStartThread = false;
	private float width = 0 ;
	private String amount;
	private Timer timer;
	private TimerTask task;
	private String prdordNo;
	private String term_No;
	
	private final int SWICH_FLAG = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.layout_create_qrcode);
		
		init();
	}
	
	
	public void init(){
		
		mContext = this;
		
		codeData = getIntent().getStringExtra(IntentParams.DATA);
		String title = getIntent().getStringExtra(IntentParams.TITLE);
		String code_title = getIntent().getStringExtra(IntentParams.CODE_TITLE);
		String msg_share = getIntent().getStringExtra(IntentParams.CODE_MSG_SHARE);
		isStartThread = getIntent().getBooleanExtra(IntentParams.SCAN_THREAD, false);
		prdordNo = getIntent().getStringExtra(IntentParams.PRD_ORD_NO);
		amount = getIntent().getStringExtra(IntentParams.AMOUNT);
		term_No = getIntent().getStringExtra(IntentParams.TERM_NO);
		boolean isVisitor = false;
		if (getIntent().hasExtra(IntentParams.SHOW_BTN)) {
			isVisitor  = getIntent().getBooleanExtra(IntentParams.SHOW_BTN, false);
		}
		
		qr_code = (ImageView) this.findViewById(R.id.qr_code);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_code_title = (TextView) this.findViewById(R.id.tv_code_title);
		tv_code_share = (TextView) this.findViewById(R.id.tv_code_share);
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_native_pay = (Button) this.findViewById(R.id.btn_native_pay);
		if (isVisitor) {
			btn_native_pay.setVisibility(View.VISIBLE);
			btn_native_pay.setOnClickListener(this);
		}
		btn_back.setVisibility(View.VISIBLE);
		tv_title.setText(title);
		tv_code_title.setText(code_title);
		tv_code_share.setText(msg_share);
		btn_back.setOnClickListener(this);


		width = this.getResources().getDisplayMetrics().widthPixels;
		
		
		if (isStartThread) {			
			getQR();
			
		}else{
			qr_code.setImageBitmap(CreateCode.getQRCode(codeData, (int)(width * 0.8), (int)(width * 0.8)));
		}
	}
	
	/**
	 * 获取生成二维码的内容
	 */
	public void getQR(){
		SharedPreferences shared = getSharedPreferences(SharedPrefConstant.PREF_NAME, Context.MODE_PRIVATE);
		 HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamsUtils.PAY_TYPE, AppConfig.PAYTYPE.QR_CODE);
        params.put(ParamsUtils.PAY_AMT, amount);
        params.put(ParamsUtils.PRDORD_NO, prdordNo);
        params.put("rateType", "");
        params.put("termNo", term_No);
        params.put("termType", "");
        params.put("track", "");
        params.put("pinblk", "");
        params.put("random", "");
        params.put("mediaType", "");
        params.put("period", "");
        params.put("icdata", "");
        params.put("crdnum", "");
        params.put("mac", "");
        params.put("scancardnum", "");
        params.put("scanornot", "");
        params.put("address", shared.getString(SharedPrefConstant.CITY_NAME, ""));
        params.put("ctype", "00");
        BasicRequest.sendRequest(mContext, Urls.TRADE_PAY, params, handler, true);
	}
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v.getId() == R.id.btn_back) {
			if (isStartThread) {
				closeTimer();
			}
			finish();
		}else if (v.getId() == R.id.btn_native_pay){
			
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isStartThread) {
				closeTimer();
				finish();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	public void showDialog(String msg, String ok){
		
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		
		builder.setTitle("提示")
		.setMessage(msg);
		builder.setCancelBtn(ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mContext.finish();
				dialog.dismiss();
			}
		});
		
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
		
	
	
	Handler handler  = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BasicRequest.FLAG_REQUEST_SUCCESS:
				
				try{
					if (msg.getData().getString(BasicRequest.KEY_URL).equals(Urls.QR_QUERY)) {
						String data = (String) msg.getData().getString(BasicRequest.KEY_DATA);
						
						JSONObject json =  new JSONObject(data);
						if("S".equals(json.optString("payResult"))){
							
							closeTimer();
							
							Intent intent = new Intent();
							intent.setAction(Actions.ACTION_GET_CODE);
							intent.putExtra(IntentParams.MSG, "交易成功,交易金额: "+AmountUtils.changeFen2Yuan(amount)+" 元");
							intent.putExtra(IntentParams.CODE, true);
							sendBroadcast(intent);
							finish();
						}else if("F".equals(json.optString("payResult"))){
							closeTimer();
							Intent intent = new Intent();
							intent.setAction(Actions.ACTION_GET_CODE);
							intent.putExtra(IntentParams.MSG, "交易失败");
							intent.putExtra(IntentParams.CODE, false);
							sendBroadcast(intent);
							finish();
						}else if("U".equals(json.optString("payResult"))){
						
							Log.d("======", "正在支付中 ，请稍后。。。");
						}
						
						
					}else{
						String data = (String) msg.getData().getString(BasicRequest.KEY_DATA);
						if (TextUtils.isEmpty(data)) {
							throw new Exception("二维码返回结果为空了");
						}
						JSONObject json =  new JSONObject(data);
						String codeData =  json.optString(IntentParams.PAY_URL);
						if (TextUtils.isEmpty(codeData)) {
							throw new Exception("生成二维码的路径为空了");
						}
						getOrdState();
						qr_code.setImageBitmap(CreateCode.getQRCode(codeData, (int)(width * 0.8), (int)(width * 0.8)));
					}
				}catch (Exception e) {
					// TODO: handle exception
					Log.e("======", "二维码生成失败:         " +e.getMessage());
					
					closeTimer();
					Intent intent = new Intent();
					intent.setAction(Actions.ACTION_GET_CODE);
					intent.putExtra(IntentParams.MSG, "二维码获取失败");
					intent.putExtra(IntentParams.CODE, false);
					sendBroadcast(intent);
					finish();
					
				}
				break;
			case BasicRequest.FLAG_REQUEST_FAIL:
				try {
					if (!msg.getData().getString(BasicRequest.KEY_URL).equals(Urls.QR_QUERY)) {
						
						closeTimer();
						
						Log.d("", "获取失败了");
						Intent intent = new Intent();
						intent.setAction(Actions.ACTION_GET_CODE);
						intent.putExtra(IntentParams.MSG, "二维码获取失败");
						intent.putExtra(IntentParams.CODE, false);
						sendBroadcast(intent);
						finish();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("=====", "====扫码失败异常====="+e.getMessage());
					closeTimer();
					finish();
				}
				
				break;
			case SWICH_FLAG:
				try {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put(IntentParams.PRD_ORD_NO, prdordNo);
					BasicRequest.sendRequest(CreateQRCodeActivity.this, Urls.QR_QUERY, param, handler,false);
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("========", "=====轮询获取交易状态失败====="+e.getMessage());
				}
				
				break;
				
			default:
				break;
			}
		};
	};
	
	public void getOrdState(){
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = SWICH_FLAG;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(task, 8*1000, 5*1000);
	
	}
	
	
	public void closeTimer(){
		if (timer!=null) {
			timer.cancel();
		}
		if (task !=null) {
			task.cancel();
		}
	}
}
