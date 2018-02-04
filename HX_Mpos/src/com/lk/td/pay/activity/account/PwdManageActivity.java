package com.lk.td.pay.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;
import com.lk.td.pay.utils.T;
import com.pay.library.bean.User;

public class PwdManageActivity extends BaseActivity implements OnClickListener {

	private Context ctx;

	private Button btn_back;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_manage);
		ctx = this;

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("密码管理");
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setText("返回");
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(this);
		// findViewById(R.id.btn_pwd_manage_revise_login_pwd).setOnClickListener(
		// this);
		// findViewById(R.id.btn_pwd_manage_revise_pay_pwd).setOnClickListener(
		// this);
		// findViewById(R.id.btn_pwd_manage_find_pay_pwd).setOnClickListener(this);
		findViewById(R.id.pwd_findpay).setOnClickListener(this);
		findViewById(R.id.pwd_reviselogin).setOnClickListener(this);
		findViewById(R.id.pwd_revisepay).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {


		case R.id.pwd_reviselogin:
			// getVerify(VerifyType.REVISE_LOGIN_PWD);
			intent.setClass(ctx, PwdReviseActivity.class);
			intent.setAction(PwdReviseActivity.ACTION_REVISE_LOGIN_PWD);
			break;
		case R.id.pwd_revisepay:
			if(User.uStatus!=2){
				T.ss("请实名认证后操作");
				return;
			}
			// getVerify(VerifyType.REVISE_PAY_PWD);
			intent.setClass(ctx, PwdReviseActivity.class);
			intent.setAction(PwdReviseActivity.ACTION_REVISE_PAY_PWD);
			break;
		case R.id.pwd_findpay:
			if(User.uStatus!=2){
				T.ss("请实名认证后操作");
				return;
			}
			// getVerify(VerifyType.FIND_PAY_PWD);
			intent.setClass(ctx, MobileVerifyActivity.class);
			intent.setAction(MobileVerifyActivity.ACTION_FORGET_PAY_PWD);
			// return;
			// intent.setClass(ctx, FindPayPwdActivity.class);
			
			break;
			case R.id.btn_back:
				finish();
				return;
		default:
			break;
		}
		startActivity(intent);
		finish();
	}

	private void getVerify(int verifyType) {
		// Intent it = new Intent(this, PwdVerifyActivity.class);
		// it.putExtra("verifyType", verifyType);
		// it.putExtra("action", PwdVerifyActivity.ACTION_NOEDITABLE);
		// startActivity(it);

	}

}
