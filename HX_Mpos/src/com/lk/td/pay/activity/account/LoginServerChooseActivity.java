package com.lk.td.pay.activity.account;

import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.golbal.MApplication;
import com.td.app.pay.hx.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginServerChooseActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "LoginServerChooseActivity------>";
	private EditText text_ip;
	private Button btn_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_server_choose_activity_layout);
		initView();
	}

	/**
	 * @Title: initView
	 * @Description: 初始化UI控件
	 */
	private void initView() {
		text_ip = (EditText) findViewById(R.id.ed_ip);
		btn_url = (Button) findViewById(R.id.btn_url);
		btn_url.setOnClickListener(this);;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_url:
			String textIP = text_ip.getText().toString().trim();
			MApplication.mSharedPref.setSERVER_TYPE(textIP);
			System.out.println("===============================>>>>"+textIP);
			Intent in = new Intent(this,LoginActivity.class);
			startActivity(in);
			finish();
			break;

		default:
			break;
		}
	}

}
