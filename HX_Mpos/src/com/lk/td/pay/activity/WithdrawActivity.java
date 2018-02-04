package com.lk.td.pay.activity;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;

public class WithdrawActivity extends BaseActivity implements
		View.OnClickListener {

	private TextView t0, t1;
	private EditText etcard, etamt;
	private String type = "02";
	private String cardNo;
	private String amount;
	private TextView tv_title;
	private Button btn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdraw);
		initView();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_title.setText("账户提现");
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		t0 = fv(R.id.tv_wd_t0);
		t0.setOnClickListener(this);
		t1 = fv(R.id.tv_wd_t1);
		t1.setOnClickListener(this);
		fv(R.id.btn_wd).setOnClickListener(this);
		etcard = fv(R.id.et_wd_bankcard);
		etamt = fv(R.id.et_wd_amount);

	}

	public <T extends View> T fv(int id) {
		return (T) findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_wd:
			withdraw();
			break;
		case R.id.tv_wd_t0:
			type = "01";
			t0.setBackgroundColor(Color.GREEN);
			t1.setBackgroundColor(Color.WHITE);
			break;
		case R.id.tv_wd_t1:
			type = "02";
			t1.setBackgroundColor(Color.GREEN);
			t0.setBackgroundColor(Color.WHITE);
			break;
		}
	}

	private void withdraw() {
		amount = etamt.getText().toString();
		try {
			double temp = Double.parseDouble(amount);
			if (temp < 1) {
				com.lk.td.pay.utils.T.ss("");
				return;
			}
		} catch (NumberFormatException e) {
			com.lk.td.pay.utils.T.ss("金额有误");
			e.printStackTrace();
			return;
		}
		cardNo = etcard.getText().toString().trim();
		if (cardNo.length() < 1) {
			T.ss("请输入卡号");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("txamt", amount);
		params.put("casType", type);
		params.put("cardNo", cardNo);
		MyHttpClient.post(this, Urls.WITHFRAW, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[Withdraw]", responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								T.showCustomeOk(WithdrawActivity.this, "");
							} else {
								T.ss(r.getMsg());
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
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
