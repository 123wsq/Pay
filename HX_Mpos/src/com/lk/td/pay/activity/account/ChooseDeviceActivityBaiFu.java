package com.lk.td.pay.activity.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lk.td.pay.activity.base.BaseActivity;
import com.pax.yumei.api.YuMeiPaxMpos;
import com.pax.yumei.listener.ConnectListener;
import com.pax.yumei.listener.GetDeviceInfoListener;
import com.pax.yumei.mis.MposDeviceInfo;
import com.td.app.pay.hx.R;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 绑定蓝牙设备
 *
 */
public class ChooseDeviceActivityBaiFu extends BaseActivity implements OnClickListener,OnItemClickListener {

	private TextView txt_tip;
	private Button checkBtn;
	private ListView m_ListView;
	private ImageView imvAnimScan;
	private AnimationDrawable animScan;
	private BluetoothAdapter blueAdapter = null;
	private List<BluetoothDevice> lstDevScanned;
	private BluetoothAdapter mAdapter;
	private MyListViewAdapter m_Adapter;
	private Drawable bindDrawable;//绑定图片
	private Drawable noBindDrawable;//未绑定
	private View line_view1;
	private View line_view2;
	private YuMeiPaxMpos manager;
	private ProgressDialog progressDialog;
	private String macAddress="";//初始值为空，不能为null
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosebaifubluetooth);
		initUI();
		initYuMeiPaxPosSDK();
		setBluetooth();
			
	}
	
	/***
	 * 初始化POS_SDK
	 */
	private void initYuMeiPaxPosSDK(){
		manager = YuMeiPaxMpos.getInstance(ChooseDeviceActivityBaiFu.this);
	}
	
	
	private void initUI(){
		checkBtn = (Button) findViewById(R.id.btnBT);
		checkBtn.setOnClickListener(this);
		m_ListView = (ListView) findViewById(R.id.lv_indicator_BTPOS);
		m_ListView.setOnItemClickListener(this);
		txt_tip=(TextView) findViewById(R.id.txt_tip);
		imvAnimScan = (ImageView) findViewById(R.id.img_anim_scanbt);
		line_view1=findViewById(R.id.line_view1);
		line_view2=findViewById(R.id.line_view2);
		
	}
	
	/**
	 * 设置蓝牙
	 */
	private void setBluetooth(){
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter localIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		localIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, localIntentFilter);
		
	}

	@Override
	public void onClick(View v) {
		connectDevice(true);// 注：先进行复位,清楚以前连接成功的蓝牙设备，防止再次搜索搜不到之前连接成功的设备。
		doDiscovery();
		
	}
	
	private void doDiscovery() {
		line_view1.setVisibility(View.GONE);
		line_view2.setVisibility(View.GONE);
		checkBtn.setEnabled(false);
		txt_tip.setVisibility(View.VISIBLE);
		imvAnimScan.setVisibility(View.VISIBLE);
		
		if (lstDevScanned == null) {
			
			lstDevScanned = new ArrayList<BluetoothDevice>();
			
		}else{
			
			lstDevScanned.clear();		
			
		}
        if (mAdapter.isDiscovering()) {
        	
        	mAdapter.cancelDiscovery();
        }
        mAdapter.startDiscovery();
	}
	
	
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				setCompareDevice(device);
				
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				
				imvAnimScan.setVisibility(View.GONE);
				txt_tip.setVisibility(View.GONE);
				checkBtn.setEnabled(true);
				
			}
        }
    };
    
    /**
     * 对比设备
     * @param device
     */
    private void setCompareDevice(final BluetoothDevice device){
    	
    	new Thread(){
    		
    		@Override
    		public void run() {
    			
    			boolean isFound = false;
    			for (BluetoothDevice dev : lstDevScanned) {
    				if (dev.getAddress().equals(device.getAddress())) {
    					
    					isFound = true;
    					break;
    					
    				}
    			}
    			if (!isFound){
    				
    				lstDevScanned.add(device);
    				
    			}
    			refreshAdapter(generateAdapterData());
    		}
    		
    	}.start();
    }
    
    private void refreshAdapter(final List<Map<String, ?>> data) {
		
	    runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				if(data.size()>0){
					
					line_view1.setVisibility(View.VISIBLE);
					line_view2.setVisibility(View.VISIBLE);
				}
				if(m_Adapter==null){
					
					m_Adapter = new MyListViewAdapter(ChooseDeviceActivityBaiFu.this, data);
					m_ListView.setAdapter(m_Adapter);
					
				}else{
					
					m_Adapter.notifyDataSetChanged(data);
					
				}
			}
		});
	}
    
    
    
    protected List<Map<String, ?>> generateAdapterData() {
		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		for (BluetoothDevice dev : lstDevScanned) {
			Map<String, Object> itm = new HashMap<String, Object>();
			itm.put("ICON_FLAG",dev.getBondState() == BluetoothDevice.BOND_BONDED ? true: false);//1.true:绑定; 2.false:未绑定
			itm.put("TITLE", dev.getName() + "(" + dev.getAddress() + ")");
			itm.put("ADDRESS", dev.getAddress());
			data.add(itm);
		}
		return data;
	}
    
    private class MyListViewAdapter extends BaseAdapter {
    	
		private List<Map<String, ?>> m_DataMap;
		private LayoutInflater m_Inflater;


		public MyListViewAdapter(Context context, List<Map<String, ?>> map) {
			this.m_DataMap = map;
			this.m_Inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
		
			return m_DataMap.size();
		}

		@Override
		public Object getItem(int position) {
			return m_DataMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
			
			ViewHolder viewHolder=null;
			if (convertView == null) {
				
				viewHolder=new ViewHolder();
				convertView = m_Inflater.inflate(R.layout.bt_qpos_item, null);
				viewHolder.m_TitleName = (TextView) convertView.findViewById(R.id.item_tv_lable);
				convertView.setTag(viewHolder);
				
			}else{
				
				viewHolder=(ViewHolder) convertView.getTag();
				
			}
			
			Map<String, ?> itemdata = (Map<String, ?>) m_DataMap.get(position);
			String sTitleName = (String) itemdata.get("TITLE");
			System.out.println(sTitleName.substring(0, 4)+"________________________________");
		    if(!TextUtils.isEmpty(sTitleName)){
		    	viewHolder.m_TitleName.setText(SetblueToothName(sTitleName));
		    }
			else{
				viewHolder.m_TitleName.setText("");
			}
			boolean icon_flag=(Boolean) itemdata.get("ICON_FLAG");
			
			if(icon_flag){
				
				viewHolder.m_TitleName.setCompoundDrawables(bindDrawable, null, null, null);
				viewHolder.m_TitleName.setHint("已配对");
				
			}else{
				
				viewHolder.m_TitleName.setCompoundDrawables(noBindDrawable, null, null, null);
				viewHolder.m_TitleName.setHint("未配对");
				
			}
			return convertView;
		}
		
		private String SetblueToothName(String sTitleName) {
			
			if(sTitleName.substring(0, 4).equals("D180")){
				return "百富"+sTitleName;
			}
			return sTitleName;
		}

		public void notifyDataSetChanged(List<Map<String, ?>> m_DataMap){
			
			this.m_DataMap=m_DataMap;
			notifyDataSetChanged();
			
		}
		
		class ViewHolder{
			
			TextView m_TitleName; 
			
		}

	}
    
    protected void onDestroy() {
    	
    	 if (mAdapter != null) {
    		 mAdapter.cancelDiscovery();
         }
    	 unregisterReceiver(mReceiver);
    	 super.onDestroy();
    	
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		//mAdapter.cancelDiscovery();
        String info=((TextView)view.findViewById(R.id.item_tv_lable)).getText().toString(); 

        final String hintInfo=((TextView)view.findViewById(R.id.item_tv_lable)).getHint().toString(); 
        if(TextUtils.isEmpty(info)){
        	return;
        }
        macAddress = info.substring(info.indexOf("(")+1,info.indexOf(")"));
		
		if(!TextUtils.isEmpty(hintInfo)){
			progressDialog = ProgressDialog.show(ChooseDeviceActivityBaiFu.this, "", "设备正在连接，请稍等！");
			progressDialog.setCancelable(true);
			connectDevice(false);
		}
	};
	
	
	/**
	 * 连接设备
	 */
	private void connectDevice(boolean rest_flag){
		
		MyOpendeviceListener openListener = new MyOpendeviceListener(rest_flag);
		manager.connect(macAddress, openListener);
		
	}
	
	class MyOpendeviceListener implements ConnectListener {

		boolean rest_flag=false;
		
		public MyOpendeviceListener(boolean rest_flag){
			
			this.rest_flag=rest_flag;
			
		}
		
		@Override
		public void onError(int errcode, String errDesc) {
			
			if(progressDialog!=null){
				
				progressDialog.dismiss();
				
			}
			if(!rest_flag){
				
				updateResult("连接失败原因: " + errDesc);
				
			}
		}

		@Override
		public void onSucc() {
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
			progressDialog.dismiss();
			updateResult("获取设备信息失败 ： "+errDesc);
		}

		@Override
		public void onSucc(MposDeviceInfo devInfo) {
			progressDialog.dismiss();
			 String Csn=devInfo.getCustomerSN();
			 onToEquAddConfirmActivity(Csn);
			}
	}
	
	private void onToEquAddConfirmActivity(String productSN){
				
		Intent it = new Intent();
		it.putExtra("ksn", productSN);
		it.putExtra("macAddress", macAddress);
		ChooseDeviceActivityBaiFu.this.setResult(RESULT_OK, it);
		finish();
		
	}
	
	private void updateResult(final String message){
		
		Toast.makeText(ChooseDeviceActivityBaiFu.this, message, Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
