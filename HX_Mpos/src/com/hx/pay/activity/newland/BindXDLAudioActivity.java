package com.hx.pay.activity.newland;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.newland.cashin.swing.xdl.DeviceController;
import com.hx.newland.cashin.swing.xdl.DeviceControllerImpl;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.account.EquAddConfirmActivity;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.audioport.AudioPortV100ConnParams;
import com.pay.library.tool.Utils;
import com.td.app.pay.hx.R;

public class BindXDLAudioActivity extends BaseActivity implements OnClickListener{
	private TextView cashin_show_msg_text;
	private Button btn_back;
	private static final String ME11_DRIVER_NAME = "com.newland.me.ME11Driver";
	private static final int FETCH_DEVICE_INFO = 5;
	private DeviceController controller = DeviceControllerImpl.getInstance();
	private Boolean processing = false;
	private String csn;
	private String termTdk;//密文密钥
	private String termTdkCv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.equ_add);
		initUI();
		initMe11Controller();
		getDeviceInfo(FETCH_DEVICE_INFO);// 初始化设备

	}
	
	private void initUI() {
		
		cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text);
		btn_back=(Button) findViewById(R.id.btn_back);
		btn_back.setText("设备绑定");
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(this);

	}

	

	@Override
	public void onBackPressed() {

		finish();

	}

	private void initMe11Controller() {

		btnStateToWaitingInitFinished();
		initMe11DeviceController(new AudioPortV100ConnParams());
		btnStateInitFinished();

	}

	/**
	 * 初始化ME11设备
	 * 
	 * @since ver1.0
	 * @param params
	 *            设备连接参数
	 */
	private void initMe11DeviceController(DeviceConnParams params) {
		controller.init(BindXDLAudioActivity.this, ME11_DRIVER_NAME,
				params, new DeviceEventListener<ConnectionCloseEvent>() {
					@Override
					public void onEvent(ConnectionCloseEvent event,
							Handler handler) {
						if (event.isSuccess()) {
							appendInteractiveInfoAndShow("设备被客户主动断开！");
						}
						if (event.isFailed()) {
							// appendInteractiveInfoAndShow("设备链接异常断开！" +
							// event.getException().getMessage());
							appendInteractiveInfoAndShow("设备链接异常断开，请重新插入刷卡头！");
						}
					}

					@Override
					public Handler getUIHandler() {
						return null;
					}
				});
		// appendInteractiveInfoAndShow("驱动版本号："+controller.getCurrentDriverVersion());
	}
	/**
	 * 设置成等待初始化结束状态
	 * 
	 * @since ver1.0
	 */
	private void btnStateToWaitingInitFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// btnConnect.setEnabled(false);
				// btnDisconnect.setEnabled(false);
				// listView.setOnTouchListener(new OnTouchListener() {
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// return true;
				// }
				// });
				//restBtn.setEnabled(false);
				processing = true;
			}
		});
	}

	/**
	 * 设置成初始化结束状态
	 * 
	 * @since ver1.0
	 */
	private void btnStateInitFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// btnConnect.setEnabled(false);
				// btnDisconnect.setEnabled(true);
				// listView.setOnTouchListener(new OnTouchListener() {
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// return false;
				// }
				// });
				//restBtn.setEnabled(true);
				processing = false;
			}
		});
	}

	private void appendInteractiveInfoAndShow(final String string) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				if(string.contains("刷卡头")){
//					
//					restBtn.setEnabled(true);
//					
//				}
				cashin_show_msg_text.setText(string);
			}
		});
	}

	/**
	 * 设置成处理中状态
	 * 
	 * @since ver1.0
	 */
	private void btnStateToProcessing() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// btnConnect.setEnabled(false);
				// btnDisconnect.setEnabled(false);
				// listView.setOnTouchListener(new OnTouchListener() {
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// return false;
				// }
				// });
				//restBtn.setEnabled(false);
				processing = true;
			}
		});
	}

	/**
	 * 设置成设备销毁状态
	 * 
	 * @since ver1.0
	 */
	private void btnStateDestroyed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// btnConnect.setEnabled(true);
				// btnDisconnect.setEnabled(false);
				// listView.setOnTouchListener(new OnTouchListener() {
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// return true;
				// }
				// });
				//restBtn.setEnabled(true);
				processing = true;
			}
		});
	}

	public void connectDevice() {
		appendInteractiveInfoAndShow("设备连接中...");
		try {
			controller.connect();
			appendInteractiveInfoAndShow("设备连接成功...");
		} catch (Exception e1) {
			e1.printStackTrace();
			appendInteractiveInfoAndShow("设备链接异常断开...");
			throw new RuntimeException(e1.getMessage(), e1);
		}
	}

	/**
	 * 获取设备信息
	 */
	private void getDeviceInfo(final int key) {

		if (processing) {
			appendInteractiveInfoAndShow("设备当前仅能执行撤消操作...");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				btnStateToProcessing();
				try {
					connectDevice();
					DeviceInfo deviceInfo = null;
					if (key == FETCH_DEVICE_INFO) {
						deviceInfo = controller.getDeviceInfo();
					} else {
						deviceInfo = controller.getDeviceInfo_me11();
					}
					// appendInteractiveInfoAndShow("设备相关信息:" + deviceInfo);
					//ksn = deviceInfo.getKSN();
					csn = Utils.getInterceptString(deviceInfo.getCSN(), 0, 14);
					//onSignTDK();
					onOperate();

				} catch (Exception e) {
					// appendInteractiveInfoAndShow("获取设备信息失败!原因:" +
					// e.getMessage());
					appendInteractiveInfoAndShow("获取设备信息失败，确保刷卡头已插入！");

				}
			}
		}).start();
		btnStateDestroyed();
	}

	/**
	 * 
	 */
	public void DisplayDialog() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Toast.makeText(BindXDLAudioActivity.this,"此设备没有进行绑定，请先到绑定界面进行绑定！", Toast.LENGTH_SHORT).show();
				//restBtn.setEnabled(false);
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyDeviceController();
	}

	/**
	 * 销毁设备控制器
	 * 
	 * @since ver1.0
	 */
	private void destroyDeviceController() {
		controller.destroy(); // 终端后的相关状态处理会通过事件完成,此处不需要处理
		btnStateDestroyed();
	}


	/**
	 * 取消操作(获取设备信息、刷卡..)
	 */
	private void onOperate() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				btnStateToProcessing();
				boolean action_cancel_flag = onCancel();//取消获取设备信息的动作
				if(action_cancel_flag && !TextUtils.isEmpty(csn)){
					goNext();
					/*boolean pourData_falg_success=pourIntoData();//灌密钥成功的标识
					if( pourData_falg_success){
						
						goNext();
						
					}*/
					
				}
			    //btnStateInitFinished();
					
			};
		}).start();

	}
	
	private void goNext(){
		
		Intent it = new Intent(BindXDLAudioActivity.this, EquAddConfirmActivity.class);
		it.putExtra("ksn", csn);
		it.putExtra("mac", "");
		startActivity(it);
		finish();
		
	}
	
	/**
	 * 灌注密钥
	 * @return
	 */
	private boolean pourIntoData(){
		
		boolean pour_flag = false;// 灌注指令
		try {
			pour_flag=true;
			controller.updateWorkingKey(WorkingKeyType.DATAENCRYPT, ISOUtils.hex2byte(termTdk), ISOUtils.hex2byte(termTdkCv));
			//appendInteractiveInfoAndShow("工作密钥装载成功!");
		} catch (Exception ex) {
			//appendInteractiveInfoAndShow("工作密钥装载失败!" + ex);
			appendInteractiveInfoAndShow("工作密钥装载失败!");
		}
		
	    return pour_flag;
		
	}
	
	/**
	 * 取消当前的操作
	 */
	private boolean onCancel(){
		
		boolean cancel_flag = false;// 取消指令
		if (DeviceConnState.CONNECTED == controller.getDeviceConnState()) {
			try {
				controller.reset();
				cancel_flag=true;
				// appendInteractiveInfoAndShow("撤消当前指令成功!");
			} catch (Exception e) {
				// Log.e(TAG, "撤消指令执行失败!", e);
				//appendInteractiveInfoAndShow("撤消指令执行失败!"+ e.getMessage());
				appendInteractiveInfoAndShow("撤消指令执行失败!请点击重置按钮.");
			}
		} else {
			appendInteractiveInfoAndShow("设备未连接!");
		}
		
		return cancel_flag;
	}
	
	/**
	 * 终端签到
	 *//*
	public  void onSignTDK(){
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("termNo", csn);
				params.put("termType", "02");
				MyHttpClient.post(BindXDLAudioActivity.this,Urls.SIGN_TDK, params, new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode, Header[] headers,byte[] responseBody) {
								Logger.json(responseBody);
								try {

									BasicResponse result = new BasicResponse(responseBody).getResult();
									if (result.isSuccess()) {
										
										termTdk=result.getJsonBody().getString("tkey");
										termTdkCv=result.getJsonBody().getString("tcv");
										onOperate();
										
									} else {

										//T.ss(result.getRSPMSG());
										T.ss("下载密钥失败，请重新进入该页面进行设备绑定!");

									}

								} catch (JSONException e) {
									e.printStackTrace();
									T.ss("下载密钥失败，请重新进入该页面进行设备绑定!");
								}

							}

							@Override
							public void onFailure(int statusCode, Header[] headers,
									byte[] responseBody, Throwable error) {

								Logger.d(error.toString());
								if (statusCode == 0) {

									T.ss("网络连接超时,请重新进入该页面进行设备绑定!");

								} else {

									//T.ss("网络错误 : " + error.getMessage());
									T.ss("网络错误,请确保网络可用再进行设备绑定！");

								}

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
		});
		
	}*/

	@Override
	public void onClick(View v) {
		
		finish();
		
	}

}

