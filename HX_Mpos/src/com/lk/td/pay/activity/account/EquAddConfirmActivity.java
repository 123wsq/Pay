package com.lk.td.pay.activity.account;

import java.util.HashMap;
import org.apache.http.Header;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

public class EquAddConfirmActivity extends BaseActivity {
	
	private TextView ksnText;
	private String ksn;
	private Context mContext;
	private String macAddress;
	private boolean bind_flag=false;//绑定标识
	public static final String Tag="Tag";
	private Button btn_back;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.equ_add_confirm);
		ksn = getIntent().getStringExtra("ksn");
		macAddress=getIntent().getStringExtra("macAddress");
		ksnText = (TextView) findViewById(R.id.equ_add_ksn_text);
		ksnText.setText(ksn);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setText("设备绑定");
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.equ_add_confirm_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addEqu();
			}
		});
	}
	
	private void addEqu(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("custMobile", User.uAccount);
		map.put("termNo", ksn);
		map.put("macAddress", macAddress);
		Log.i(Tag, map.get("custMobile")+"_"+map.get("termNo")+"_"+map.get("macAddress"));
		MyHttpClient.post(this, Urls.POS_BING, map,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						BasicResponse res;
						try {
							res = new BasicResponse(responseBody).getResult();
							//绑定设备成功
							if (res.isSuccess()) {
								bind_flag=true;
								T.showCustomeOk(mContext,res.getMsg());
								User.macAddress = macAddress;
								finish();
							} else {
								showDialog(res.getMsg());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						networkError(statusCode, error);
					}
					@Override
					public void onStart() {
						showLoadingDialog();
					}
					@Override
					public void onFinish() {
						dismissLoadingDialog();
						if(bind_flag){    
							refreshUserInfo();
//							startActivity(new Intent(EquAddConfirmActivity.this, EquListActivity.class));
//							finish();
						}
					}
				});
	}
	
	public void refreshUserInfo() {
		MyHttpClient.post(EquAddConfirmActivity.this, Urls.GET_USER_INFO,
				new HashMap<String, String>(), new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						System.out.println("刷新用户信息---");
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								User.uStatus = r.getJsonBody().optInt(
										"custStatus");
								User.termNum = r.getJsonBody()
										.optInt("termNum");
								User.cardNum = r.getJsonBody()
										.optInt("cardNum");
								User.cardBundingStatus=r.getJsonBody().optInt("cardBundingStatus");
							} else {

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						networkError(statusCode, error);
					}
					@Override
					public void onStart() {
						super.onStart();
					}
				});
	}

}
