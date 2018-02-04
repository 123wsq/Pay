package com.lk.td.pay.activity.account;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.hx.pay.activity.newland.BindXDLAudioActivity;
import com.hx.pay.activity.ty.CheckTYActivity;
import com.hx.view.bean.CardBean;
import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.CheckActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.pax.yumei.api.YuMeiPaxMpos;
import com.pax.yumei.listener.ConnectListener;
import com.pax.yumei.listener.GetDeviceInfoListener;
import com.pax.yumei.mis.MposDeviceInfo;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BindDeviceInfo;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

public class EquListActivity extends BaseActivity implements OnClickListener {
	public final static int DEVICE_SELECT_BAIFU = 2;
	private ListView listview;
	private Context mContext;
	private String macAddress="";
	private String termNo="";
	String termTypeName; //终端厂商名
	private YuMeiPaxMpos manager;
	private ProgressDialog progressDialog;
	private List<CardBean> listType = null;
	private ArrayList<BindDeviceInfo> deviceList;
	private String bangditype = "0";
	private CustomDialog dialog;
	private Button btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.equ_list);
		mContext = this;
		init();

		listType = MApplication.getInstance().getCardType();
	}

	
	private void init() {
		manager = YuMeiPaxMpos.getInstance(EquListActivity.this);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(this);
		findViewById(R.id.tv_title).setVisibility(View.GONE);
		findViewById(R.id.equ_list_add_btn).setOnClickListener(this);
		listview = (ListView) findViewById(R.id.equ_list_lv);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		deviceList = new ArrayList<BindDeviceInfo>();
		getEqueList();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.equ_list_add_btn:
			bindDevice();
			break;
		
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		  * 搜索蓝牙设备,链接蓝牙 获取设备信息   获取数卡/插卡信息
		  * 
		  */
		if(resultCode!=RESULT_OK) {
			return;
		}
		    switch (requestCode) {
		    case DEVICE_SELECT_BAIFU:
		    	 termNo=data.getStringExtra("ksn");
		    	 macAddress=data.getStringExtra("macAddress");
		    	 connectDevice(true); 
		    }
	}

	private void bindDevice() {
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle(getString(R.string.select_card_type));
		builder.setListView(listType, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
				 CardBean card = listType.get(postion);
			// 根据选择的刷卡器类型决定跳转到哪个页面
				switch (card.getId()) {
					case 1://新大陆音频刷卡器
						startActivity(new Intent(EquListActivity.this, BindXDLAudioActivity.class));
						break;
					case 2://新大陆蓝牙刷卡器
					case 4:
						Intent it = new Intent(EquListActivity.this,CheckActivity.class);
						startActivity(it);
						break;

					case 3://天喻蓝牙刷卡器
						startActivity(new Intent(EquListActivity.this, CheckTYActivity.class));
						break;
					default:
						T.s(mContext, getString(R.string.not_support_card), Toast.LENGTH_SHORT);
						break;
				}
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
		
	}


	
	/**
	 * 连接设备
	 */
	private void connectDevice(boolean rest_flag){
		
		MyOpendeviceListener openListener = new MyOpendeviceListener(rest_flag);
		manager.connect(macAddress, openListener);
		
	}
	
	class MyOpendeviceListener implements ConnectListener {

		boolean rest_flag=false;//目前只要设备连接成功再点击搜素蓝牙按钮就回找不到先前连接的设备，要是想找到之前的设备，必须先进行复位。
		
		public MyOpendeviceListener(boolean rest_flag){
			
			this.rest_flag=rest_flag;
			
		}
		
		@Override
		public void onError(int errcode, String errDesc) {
			
			if(progressDialog!=null){
				
				progressDialog.dismiss();
				
			}
			if(!rest_flag){
				
				Toast.makeText(EquListActivity.this, getString(R.string.connect_failed) + errDesc, Toast.LENGTH_SHORT).show();
				
			}
		}

		@Override
		public void onSucc() {
//			manager.closeDevice();   //关闭设备
			getDeviceInfo();
		}
	
	}
	
	/**
	 * 获取设备信息
	 */
	private void getDeviceInfo(){
		
		myGetDeviceInfoListener listener = new myGetDeviceInfoListener();
		manager.getDeviceInfo(listener);
		
	}
	
	class myGetDeviceInfoListener implements GetDeviceInfoListener {

		@Override
		public void onError(int errCode, String errDesc) {
			// TODO Auto-generated method stub
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			//updateResult("get device infor error:" + errDesc);
			updateResult(getString(R.string.get_device_info_failed)+errDesc);
		}

		@Override
		public void onSucc(MposDeviceInfo devInfo) {
			// TODO Auto-generated method stub
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
//			updateResult(  "appVersion=" +    devInfo.getAppVersion() + 
//					"\n" + "customerSN=" +    devInfo.getCustomerSN() + 
//					"\n" + "model=" +         devInfo.getModel() + 
//					"\n" + "productSN=" +     devInfo.getProductSN() + 
//					"\n" + "vendor=" + devInfo.getVendor()
//					);
//			 String productSN=devInfo.getProductSN();
			 String Csn=devInfo.getCustomerSN();
			 onToEquAddConfirmActivity(Csn);
			}
	}
	
     private void updateResult(final String message){
		
		Toast.makeText(EquListActivity.this, message, Toast.LENGTH_SHORT).show();
		
	}
     
     private void onToEquAddConfirmActivity(String productSN){
			
 		Intent it = new Intent(EquListActivity.this, EquAddConfirmActivity.class);
 		it.putExtra("ksn", productSN);
 		it.putExtra("macAddress", macAddress);
 		startActivity(it);
 		finish();
 		
 	}

	class Hold {
		TextView tv = null;
		TextView btn = null;
		TextView type;
	}

	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
	class EquListAdapter extends BaseAdapter {
		private ArrayList<BindDeviceInfo> list;
		private Context mContext;

		public EquListAdapter(Context mContext, ArrayList<BindDeviceInfo> list) {
			this.mContext = mContext;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			TextView tvtyep = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.equ_list_item, null);
				tv = (TextView) convertView
						.findViewById(R.id.equ_list_item_ksn);
				tvtyep = (TextView) convertView.findViewById(R.id.equ_list_item_type);
				convertView.setTag(tv);
				convertView.setTag(tvtyep);
			} else {
				tv = (TextView) convertView.getTag();
				tvtyep= (TextView) convertView.getTag();
			}
			tv.setText(list.get(position).getTermNo());
			tvtyep.setText(list.get(position).getTerminalType());
			return convertView;
		}

	}

	private void getEqueList() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("custMobile", User.uAccount);
		MyHttpClient.post(this, Urls.BIND_DEVICE_LIST, map,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[终端列表]", responseBody);
						try {
							BasicResponse res = new BasicResponse(responseBody)
									.getResult();
							if (res.isSuccess()) {
								JSONArray array = res.getJsonBody()
										.getJSONArray("termList");
								for (int i = 0; i < array.length(); i++) {
									BindDeviceInfo d = new BindDeviceInfo();
									d.setAgentId(array.getJSONObject(i)
											.optString("agentId"));
									d.setTermNo(array.getJSONObject(i)
											.optString("termNo"));
									d.setTerminalNo(array.getJSONObject(i)
											.optString("terminalNo"));
									d.setTerminalType(array.getJSONObject(i)
											.optString("termTypeName"));

									deviceList.add(d);
								}
								for (int j = 0; j < deviceList.size(); j++) {
									bangditype = deviceList.get(j)
											.getTerminalType();
								}
								listview.setAdapter(new EquListAdapter(
										mContext, deviceList));
							} else {
								T.ss(res.getMsg());
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
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						dismissLoadingDialog();
					}
				});
	}
}

