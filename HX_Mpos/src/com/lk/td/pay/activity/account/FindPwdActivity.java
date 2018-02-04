package com.lk.td.pay.activity.account;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.MD5Util;


/**
 * 找回密码
 * @author Ding
 *
 */
public class FindPwdActivity extends BaseActivity implements OnClickListener{
	
	private EditText  newPwdEdit, newPwdAEdit;
	
	/**
	 * 找回登录密码
	 */
	public static final String ACTION_FIND_LOGIN_PWD="1";

	/**
	 * 找回支付密码
	 */
	public static final String ACTION_FIND_PAY_PWD="2";
	private String action,newPwd,rePwd,code,mobile;
	private TextView tv_title;
	private Button btn_back;
	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.find_pwd);
		action=getIntent().getAction();
		System.out.println("getIntent().getStringExtra("+getIntent().getStringExtra("code"));
		code = MD5Util.generatePassword(getIntent().getStringExtra("code"));
		System.out.println("--------------------->code"+code);
		mobile=getIntent().getStringExtra("mobile");
		initView();
	}

	private void initView() {
		newPwdEdit = (EditText) findViewById(R.id.et_pw1);
		newPwdAEdit = (EditText) findViewById(R.id.et_pw2);
		this.findViewById(R.id.btn_next).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_back = (Button) findViewById(R.id.btn_back);
		if(action.equals(ACTION_FIND_LOGIN_PWD))
			tv_title.setText("找回登录密码");
		else{
			tv_title.setText("找回支付密码");
		}
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_next:
			revisePwd();
			break;

		default:
			break;
		}
		
	}

	private void revisePwd() {
		newPwd=newPwdEdit.getText().toString().trim();
		rePwd=newPwdAEdit.getText().toString().trim();
		 if(newPwd.length()==0){
			T.ss("请输入新密码");
			return;	
		}else if(rePwd.length()==0){
			T.ss("请输入新密码");
			return;	
		}
		if(newPwd.length()<6||rePwd.length()<6){
			T.ss("新密码长度最少为6位");
			return;
		}
		if(!newPwd.equals(rePwd)){
			T.ss("两次输入的密码不一致");
			return;
		}
		HashMap<String, String> params=new HashMap<String, String>();
		params.put("pwdType", action);
		params.put("updateType", "2");
		params.put("value", code);
		params.put("newPwd", newPwd);
		params.put("custMobile", mobile);
		for(String key:params.keySet()){
			System.out.println("["+key+"]"+params.get(key));
		}
		MyHttpClient.post(this, Urls.UPDATE_PWD, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				if(responseBody!=null){
					Logger.json(new String(responseBody));
					try {
						BasicResponse response=new BasicResponse(responseBody).getResult();
						if(response.isSuccess()){
							T.showCustomeOk(FindPwdActivity.this, "修改密码成功");
							finish();
						}else{
							T.sl(response.getMsg());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
				showLoadingDialog();
			}
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
		});
		
	}
	
}
