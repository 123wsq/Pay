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
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.MD5Util;
import com.td.app.pay.hx.R;
/**
 * 修改密码
 * @author Ding
 *
 */
public class PwdReviseActivity extends BaseActivity implements OnClickListener{
	
	private EditText oldPwdEdit, newPwdEdit, newPwdAEdit;
	
	/**
	 * 修改登录密码
	 */
	public static final String ACTION_REVISE_LOGIN_PWD="1";

	/**
	 * 修改支付密码
	 */
	public static final String ACTION_REVISE_PAY_PWD="2";
	private String action,oldPwd,newPwd,rePwd;
	private Button btn_back;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.revise_pwd);
		action=getIntent().getAction();
		if(null==action){
				throw new NullPointerException("修改密码类型为空[PwdReviseActivity.class]");
		}
		initView();
	}

	private void initView() {
		oldPwdEdit = (EditText) findViewById(R.id.old_pwd_edit);
		newPwdEdit = (EditText) findViewById(R.id.new_pwd_edit);
		newPwdAEdit = (EditText) findViewById(R.id.new_pwda_edit);
		this.findViewById(R.id.revise_pwd_btn).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		if(action.equals(ACTION_REVISE_LOGIN_PWD))
		tv_title.setText("修改登录密码");

		else{
			tv_title.setText("修改支付密码");
		}
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.revise_pwd_btn:
			revisePwd();
			break;
		default:
			break;
		}
		
	}

	private void revisePwd() {
		oldPwd=oldPwdEdit.getText().toString().trim();
		newPwd=newPwdEdit.getText().toString().trim();
		rePwd=newPwdAEdit.getText().toString().trim();
		oldPwd=MD5Util.generatePassword(oldPwd);
		if(oldPwd.length()==0){
			T.ss("请输入原密码");
			return;
		}else if(oldPwd.length()<6){
			T.ss("密码最短为6位");
			return;
		}else if(newPwd.length()==0){
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
		params.put("updateType", "1");
		params.put("value", oldPwd);
		params.put("newPwd", newPwd);
		
		MyHttpClient.post(this, Urls.UPDATE_PWD, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				if(responseBody!=null){
					Logger.json(new String(responseBody));
					try {
						BasicResponse response=new BasicResponse(responseBody).getResult();
						if(response.isSuccess()){
							T.showCustomeOk(PwdReviseActivity.this, "修改密码成功");
							finish();
						}else{
							showDialog("修改失败:"+response.getMsg());
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
