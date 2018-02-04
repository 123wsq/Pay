package com.lk.td.pay.activity.account;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hx.view.widget.CustomDialog;
import com.landicorp.android.mpos.reader.LandiMPos;
import com.landicorp.mpos.reader.BasicReaderListeners.AddAidListener;
import com.landicorp.mpos.reader.BasicReaderListeners.AddPubKeyListener;
import com.landicorp.mpos.reader.BasicReaderListeners.GetDeviceInfoListener;
import com.landicorp.mpos.reader.BasicReaderListeners.LoadMacKeyListener;
import com.landicorp.mpos.reader.BasicReaderListeners.LoadPinKeyListener;
import com.landicorp.mpos.reader.BasicReaderListeners.OpenDeviceListener;
import com.landicorp.mpos.reader.model.MPosDeviceInfo;
import com.landicorp.robert.comm.api.CommunicationManagerBase.DeviceSearchListener;
import com.landicorp.robert.comm.api.DeviceInfo;
import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.lk.td.pay.adapter.BluetoothAdapter;
import com.lk.td.pay.golbal.MApplication;
import com.pay.library.tool.store.SharedPrefConstant;
import com.pay.library.uils.ByteArrayUtils;
import com.td.app.pay.hx.R;

public class EquipmentManagementActivity extends BaseFragmentActivity implements OnItemClickListener, DeviceSearchListener{
	
	private static final int LOAD_PINKEY = 0;
	private static final int LOAD_MACKEY = 1;
	private static final int LOAD_PUBLICKEY = 2;
	private static final int LOAD_AID = 3;
	private static final int LOAD_SUCCESS = 4;
	
	private TextView currentText;
	private ListView listview;
	private BluetoothAdapter adapter;
	private LandiMPos reader;
	private ProgressBar progressBar;
	private byte[] pinKey = null;
	private byte[] macKey = null;
	private String[] publicKey = null;
	private String[] aid = null;
	private int adiIndex = 0;
	private int publickeyIndex = 0;
	byte masterKeyIndex = 0x00;
	private DeviceInfo deviceInfo;
	private String usrId;
	private String dowFlg;
	//private String termphyNo;
	private TextView tv_title;
	private Button btn_back;
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_PINKEY:
				loadPinKey();
				break;
			case LOAD_MACKEY:
				loadMacKey();
				break;
			case LOAD_PUBLICKEY:
				loadPublicKey();
				break;
			case LOAD_AID:
				loadAID();
				break;
			case LOAD_SUCCESS:
				dismissLoadingDialog();
				MApplication.mSharedPref.putDeviceInfo(deviceInfo);
				currentText.setText(deviceInfo.getName());
				showMsg("成功绑定蓝牙刷卡器");
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.equipment_management);
		usrId = MApplication.mSharedPref.getSharePrefString(SharedPrefConstant.USER_ACCOUNT);
		dowFlg = MApplication.mSharedPref.getSharePrefString(SharedPrefConstant.DOW_FLG);
		//pinKey = ByteArrayUtils.hexString2ByteArray("2D7357E369DD8C11000000000000000076CB24F2");        
		//macKey = ByteArrayUtils.hexString2ByteArray("2D7357E369DD8C11000000000000000076CB24F2");
		
		//pinKey = ByteArrayUtils.hexString2ByteArray("5B829A3377E78E9FBC885091BCF829D02F187B8C");        
		//macKey = ByteArrayUtils.hexString2ByteArray("D0895998892F0BC40000000000000000C7EB4CEB");
		//publicKey = getResources().getStringArray(R.array.publickeys);
		//aid = getResources().getStringArray(R.array.aids);
		initView();
	}

	private void initView() {
		currentText = (TextView) findViewById(R.id.equ_man_current_text);
		DeviceInfo deviceInfo = MApplication.mSharedPref.getDeviceInfo();
		if(null == deviceInfo){
			currentText.setText("无");
		} else {
			currentText.setText(deviceInfo.getName());
		}
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("设备连接管理器");
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		listview = (ListView) findViewById(R.id.equ_man_listview);
		progressBar = (ProgressBar) findViewById(R.id.equ_man_progressbar);
		adapter = new BluetoothAdapter(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		progressBar.setVisibility(View.VISIBLE);
		reader = LandiMPos.getInstance(getApplicationContext());
		reader.startSearchDev(this, true, true, 60000);
		findViewById(R.id.equ_man_stop_search_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reader.stopSearchDev();
				
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		reader.stopSearchDev();
		deviceInfo = adapter.getDeviceInfo(position);
		//if("1".equals(dowFlg)){
			bindEqu(position);
		//} else {
		//	showToast(deviceInfo.getName() + "已经绑定。");
		//}
	}

	private void bindEqu(final int position){
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_name));
		builder.setMessage("您确定要绑定此设备?");
		builder.setOkBtn("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(deviceInfo.getName() != null){
					//writeDevice(deviceInfo);
					openDevice();
					
				} else {
					showMsg("未知设备，请重新选择");
				}
			}
		});
		builder.setCancelBtn("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}
	
	private void writeDevice() {
		//showLoadingDialog("正在添加设备。。。");		
		reader.openDevice(deviceInfo, new OpenDeviceListener() {
			@Override
			public void openSucc() {
				mHandler.sendEmptyMessage(LOAD_PINKEY);
			}

			@Override
			public void openFail() {				
				dismissLoadingDialog();
				showMsg("打开蓝牙设备失败!");
				
			}

		});
	}
	
	private void loadPinKey(){
		reader.loadPinKey(masterKeyIndex, pinKey, new LoadPinKeyListener() {
			@Override
			public void onError(int errCode, String errDesc) {
				dismissLoadingDialog();
				showMsg("失败"+errDesc);
			}
			@Override
			public void onLoadPinKeySucc() {
				mHandler.sendEmptyMessage(LOAD_MACKEY);
			}
		});
	}
	
	private void loadMacKey(){
		reader.loadMacKey(masterKeyIndex, macKey, new LoadMacKeyListener() {
			@Override
			public void onError(int errCode, String errDesc) {
				dismissLoadingDialog();
				showMsg("失败"+errDesc);
			}
			
			@Override
			public void onLoadMacKeySucc() {
				mHandler.sendEmptyMessage(LOAD_PUBLICKEY);
			}
		});	
	}
	
	private void loadPublicKey(){
		byte[] data =  ByteArrayUtils.toByteArray(publicKey[publickeyIndex++]);
		System.out.println("publickeyIndex------------------------------:" + publickeyIndex);
		System.out.println("data--->" + data);
		reader.addPubKey(data, new AddPubKeyListener() {
			@Override
			public void onError(int errCode, String errDesc) {
				dismissLoadingDialog();
				showMsg("添加公钥失败"+errDesc);
			}
			@Override
			public void onAddPubKeySucc() {
				if(publickeyIndex < publicKey.length){
					mHandler.sendEmptyMessage(LOAD_PUBLICKEY);
				} else {
					mHandler.sendEmptyMessage(LOAD_AID);
				}
			}
		});
	}	
	
	private void loadAID(){
		byte[] data =  ByteArrayUtils.toByteArray(aid[adiIndex++]);
		System.out.println("adiIndex------------------------------:" + adiIndex);
		/*if(data.length != 224 && adiIndex < aid.length){
			mHandler.sendEmptyMessage(LOAD_AID);
		}*/
		try {
			reader.AddAid(data, new AddAidListener() {
				@Override
				public void onError(int errCode, String errDesc) {
					dismissLoadingDialog();
					showMsg("添加aid失败"+errDesc);
				}
				@Override
				public void onAddAidSucc() {
					if(adiIndex < aid.length){
						mHandler.sendEmptyMessage(LOAD_AID);
						System.out.println("aid写入成功------------------------------:" + adiIndex);
					} else {
						mHandler.sendEmptyMessage(LOAD_SUCCESS);
					}
					
				}
			});
		} catch (Exception e) {
			if(adiIndex < aid.length){
				mHandler.sendEmptyMessage(LOAD_AID);
			} else {
				mHandler.sendEmptyMessage(LOAD_SUCCESS);
			}
		}
		
	}

	private void showMsg(String mag){
		new CustomDialog.Builder(this)
		.setTitle(getString(R.string.app_name))
		.setMessage(mag)
		.setOkBtn(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
						finish();
					}
				}).create().show();
	}
	
	@Override
	public void discoverComplete() {
		progressBar.setVisibility(View.INVISIBLE);
		
	}

	@Override
	public void discoverOneDevice(DeviceInfo arg0) {
		if (arg0.getName() != null) {
			adapter.addDevice(arg0);
		}
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			reader.stopSearchDev();
    		return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
	
	
	private void openDevice() {
		showLoadingDialog();
		reader.openDevice(deviceInfo, new OpenDeviceListener() {
			@Override
			public void openSucc() {

				reader.getDeviceInfo(new GetDeviceInfoListener() {
					
					@Override
					public void onError(int arg0, String arg1) {
						dismissLoadingDialog();
						showMsg("获取设备信息失败"+arg1);
						
					}
					
					@Override
					public void onGetDeviceInfoSucc(MPosDeviceInfo arg0) {
						//downKey(arg0.deviceSN);
						mHandler.sendEmptyMessage(LOAD_SUCCESS);
					}
				});
			}

			@Override
			public void openFail() {				
				dismissLoadingDialog();
				showMsg("打开刷卡器设备失败");
				
			}

		});
		
	}
	
	private void downKey(String termphyNo){
		
	}
}
