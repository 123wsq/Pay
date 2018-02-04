package com.lk.td.pay.activity.account;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;
import com.lk.td.pay.adapter.BankCardListViewAdapter;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BankCardItem;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.store.SharedPrefConstant;

public class BankCardListActivity extends BaseActivity implements
		View.OnClickListener {

	private static final int GET_BANK_CARD_LIST_SUCCESS = 1;
	private static final int GET_UNBUILD_CARD_SUCCESS = 2;
	Context mContext;
	private ImageView addCardBtn;
	private BankCardListViewAdapter adapter;
	private ListView listView;
	private List<BankCardItem> bankList = new ArrayList<BankCardItem>();
	private String userAccount;
	private String pwdPwd;
	private int selected = 0;
	private String[] ops = new String[] {};
	private int currentClickPosition;
	private Button btn_back,btn_reset;
	private TextView tv_title;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_BANK_CARD_LIST_SUCCESS:
				adapter.notifyDataSetChanged();
				break;
			case GET_UNBUILD_CARD_SUCCESS:
				// accessCashierDeskSuccedActivity();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_card_list);
		mContext = this;
		userAccount = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USER_ACCOUNT);
		init();

	}

	private void init() {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("我的银行卡");

		btn_back.setVisibility( View.VISIBLE);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		btn_reset.setVisibility(View.VISIBLE);
		btn_reset.setText("绑定");
		btn_reset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(BankCardListActivity.this,
						BindBankCardActivity.class));
				finish();
			}
		});
		listView = (ListView) this.findViewById(R.id.bank_listview);
		bankList = new ArrayList<BankCardItem>();
		adapter = new BankCardListViewAdapter(mContext, bankList, false);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onitemclick);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				System.out.println("position=" + arg2);
				currentClickPosition = arg2;
				if (null == pop) {
					initPopwindow(mContext);
				}

				pop.showAsDropDown(arg1);
				pop.setFocusable(true);
				// showPopWindow(mContext).showAsDropDown(arg0);
				return true;
			}
		});
		// 银行卡绑定浏览
		getBankCardStatus();
	}

	OnItemClickListener onitemclick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// BankCardItem item=bankList.get(position);
			// Intent intent=new
			// Intent(BankCardListActivity.this,BankCardDetailsActivity.class);
			// intent.putExtra("data", item);
			// startActivityForResult(intent, 9);
		}
	};

	/**
	 * @Title: getBankCardStatus
	 * @Description: 查询用户绑定银行卡浏览
	 */
	private void getBankCardStatus() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		MyHttpClient.post(mContext, Urls.GET_BANKCARD_LIST, requestMap,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[BankCardList]", responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								JSONArray array = r.getJsonBody().optJSONArray(
										"bankCardList");
								if(bankList.size()>0){
									bankList.clear();
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject temp = array.getJSONObject(i);
									BankCardItem item = new BankCardItem();
									item.setCardName(temp.optString("issnam"));
									item.setCardNo(temp.optString("cardNo"));
									bankList.add(item);
								}
								if(null==adapter){
								adapter = new BankCardListViewAdapter(mContext,
										bankList, true);
								listView.setAdapter(adapter);
								}else{
									adapter.refresh(bankList);
									adapter.notifyDataSetChanged();
								}
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
						// TODO 自动生成的方法存根
						super.onFinish();
						dismissLoadingDialog();
					}
				});

	}

	private void unBuildCard(int positon, String pwd) {

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == 101) {
			if (null != arg2) {
				String pwd = arg2.getStringExtra("pwd");
				unBuildCard(currentClickPosition, pwd);
			}
		} else if (arg1 == 10) {
			finish();
		}
	}

	PopupWindow pop;
	TextView tvdefault;
	TextView tvunbind;

	public void initPopwindow(Context ctx) {

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.pop_banlcard, null);
		pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		tvdefault = (TextView) view.findViewById(R.id.tv_bc_default);
		tvdefault.setOnClickListener(this);
		tvunbind = (TextView) view.findViewById(R.id.tv_bc_unbind);
		tvunbind.setOnClickListener(this);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);

	}

	private void setDefault() {
		String temp = bankList.get(currentClickPosition).getCardNo();
		if (!TextUtils.isEmpty(temp)) {
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("operType", "3");
		params.put("cardNo", temp);
		MyHttpClient.post(mContext, Urls.BANKCARD_EDIT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {

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

	private void bcOperate(int type) {
		String temp = bankList.get(currentClickPosition).getCardNo();
		if (TextUtils.isEmpty(temp)) {
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		if (type == 4)
			params.put("operType", "4");
		else
			params.put("operType", "3");
		params.put("cardNo", temp);
		MyHttpClient.post(mContext, Urls.BANKCARD_EDIT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								T.showCustomeOk(mContext, "操作成功");
								getBankCardStatus();
							} else {
								T.ss(r.getMsg());
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
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_bc_default:
			bcOperate(3);
			pop.dismiss();
			break;
		case R.id.tv_bc_unbind:
			bcOperate(4);
			pop.dismiss();
			break;
		}

	}
	
	

}
