package com.hx.pay.activity.newland;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.newland.cashin.swing.xdl.ByteUtils;
import com.hx.newland.cashin.swing.xdl.DeviceController;
import com.hx.newland.cashin.swing.xdl.DeviceControllerImpl;
import com.hx.newland.cashin.swing.xdl.NewLandUtil;
import com.hx.newland.cashin.swing.xdl.TransferListener;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.CardBalanceConfirmActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.utils.T;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.ModuleType;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.external.me11.ME11SwipResult;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.audioport.AudioPortV100ConnParams;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.td.app.pay.hx.R;

public class SwingXDLCardBalanceActivity extends BaseActivity implements OnClickListener{
	@SuppressWarnings("unused")
	private static final String ME3X_DRIVER_NAME = "com.newland.me.ME3xDriver";
	private static final String ME11_DRIVER_NAME = "com.newland.me.ME11Driver";
	@SuppressWarnings("unused")
	private static final String K21_DRIVER_NAME = "com.newland.me.K21Driver";
	@SuppressWarnings("unused")
	private static final int FETCH_DEVICE_INFO_ME11 = 0;
	private static final int SWIPCARD_ME11 = 1;// 密文刷卡
	@SuppressWarnings("unused")
	private static final int SWIPCARD_PLAIN_ME11 = 2;
	@SuppressWarnings("unused")
	private static final int PININPUT_ME11 = 3;
	@SuppressWarnings("unused")
	private static final int CANCEL = 4;
	private static final int FETCH_DEVICE_INFO = 5;
	@SuppressWarnings("unused")
	private static final int SWIPCARD = 6;
	@SuppressWarnings("unused")
	private static final int SWIPCARD_PLAIN = 7;
	@SuppressWarnings("unused")
	private static final int FETCH_POWER_LEVEL = 8;
	@SuppressWarnings("unused")
	private static final int MAC = 9;
	@SuppressWarnings("unused")
	private static final int LOAD_WORKKEY = 10;
	@SuppressLint("SimpleDateFormat")
	private DeviceController controller = DeviceControllerImpl.getInstance();
	private Boolean processing = false;
	private String csn;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private long mTime = 0;
	private JSONArray jsonTermListArray = null;
	private boolean sign_flag = false;// 签到标识
	private String termTdk;//密文密钥
	private String termTdkCv;
	@SuppressWarnings("unused")
	private static DeviceManager deviceManager = ConnUtils.getDeviceManager();
	private Button btn_back;
	private TextView cashin_show_msg_text;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_balance);
		initUI();
		initMe11Controller();
		//onGetTermList();
		getDeviceInfo(FETCH_DEVICE_INFO);// 初始化设备

	}

	private void initUI() {


		btn_back=(Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setText("银行卡查询");

		btn_back.setOnClickListener(this);
		cashin_show_msg_text=(TextView) findViewById(R.id.cashin_show_msg_text);

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
		controller.init(SwingXDLCardBalanceActivity.this, ME11_DRIVER_NAME,
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

					/*for (int i = 0; i < jsonTermListArray.length(); i++) {

						try {
							if (jsonTermListArray.getJSONObject(i).get("termNo").equals(csn)) {

								termTdk=(String) jsonTermListArray.getJSONObject(i).get("termTdk");
								termTdkCv=(String) jsonTermListArray.getJSONObject(i).get("termTdkCv");
								sign_flag = true;
								break;

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}*/
					onOperate();
					// btnStateInitFinished();
					/*if (sign_flag) {

						//onCancelOperate();// 第二个任务开始之前先先取消第一个任务的操作。
						onOperate();

					} else {

						DisplayDialog();

					}*/

				} catch (Exception e) {
					// appendInteractiveInfoAndShow("获取设备信息失败!原因:" +
					// e.getMessage());s
					appendInteractiveInfoAndShow("获取设备信息失败，请退出页面再重新刷卡！");

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

				Toast.makeText(SwingXDLCardBalanceActivity.this,"此设备没有进行绑定，请先到绑定界面进行绑定！", Toast.LENGTH_SHORT).show();
				//restBtn.setEnabled(false);
			}
		});

	}

	/**
	 * 刷卡
	 * 
	 * @param whereType
	 */
	public void onSwipCard(final int whereType) {

		if (processing) {
			appendInteractiveInfoAndShow("设备当前仅能执行撤消操作...");
			return;
		}
		new Thread(new Runnable() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				btnStateToProcessing();
				try {
					connectDevice();
					appendInteractiveInfoAndShow("请刷卡或插卡...");

					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间,建议从后台获取(用于PBOC交易)
					String strDate = formatter.format(curDate);

					ME11SwipResult swipRslt = null;
					if (whereType == SWIPCARD_ME11) {
						swipRslt = controller.swipCard_me11(ByteUtils
								.hexString2ByteArray(strDate.substring(2)),
								30000L, TimeUnit.MILLISECONDS);
					} else {
						swipRslt = controller.swipCardForPlain_me11(ByteUtils
								.hexString2ByteArray(strDate.substring(2)),
								30000L, TimeUnit.MILLISECONDS);
					}
					if (swipRslt == null) {
						appendInteractiveInfoAndShow("刷卡撤销");
						btnStateInitFinished();
						return;
					}
					ModuleType[] moduleType = swipRslt.getReadModels();
					if (moduleType[0] == ModuleType.COMMON_SWIPER) {//刷卡
						byte[] firstTrack = swipRslt.getFirstTrackData();
						byte[] secondTrack = swipRslt.getSecondTrackData();
						byte[] thirdTrack = swipRslt.getThirdTrackData();
//						appendInteractiveInfoAndShow("刷卡成功\n一磁道:"+ (firstTrack == null ? "null" : Dump.getHexDump(firstTrack))
//													 + "\n二磁道:"+ (secondTrack == null ? "null" : new String(secondTrack, "UTF-8"))
//													 + "\n三磁道:"+ (thirdTrack == null ? "null" : new String(thirdTrack, "UTF-8")));
						
						
						String firstTrackStr= (firstTrack == null ? "" : Dump.getHexDump(firstTrack));//一磁道
						String secondTrackStr=(secondTrack == null ? "" : new String(secondTrack, "UTF-8"));//二磁道
						String thirdTrackStr=(thirdTrack == null ? "" : new String(thirdTrack, "UTF-8"));//三磁道
						//String cardNo=Utils.getInterceptString(secondTrackStr, 0, secondTrackStr.indexOf("="));//银行卡号
						String cardNo=String.valueOf(swipRslt.getAccount().getAcctNo());//银行卡号
						
						
//						/****************************刷卡***************************/
//						PosData.getPosData().setPayAmt(amount);
//						PosData.getPosData().setCardNo(cardNo);
//						PosData.getPosData().setRate("1");
//						PosData.getPosData().setTermNo(csn);
//						PosData.getPosData().setTermType("02");
//						PosData.getPosData().setPayAmt(amount);
//						PosData.getPosData().setTrack(secondTrackStr + "|"+thirdTrackStr);
//						PosData.getPosData().setRandom("000000");
//						PosData.getPosData().setMediaType("01");
//						//PosData.getPosData().setPeriod(period);
//						//PosData.getPosData().setCrdnum(icNumber);
//						entryOtherActivity();
//						/************************************************************/
						
						//////////////////////////刷卡////////////////////////////////
						nextStep(cardNo, secondTrackStr, thirdTrackStr, "", "","","01");
						
						
					} else if (moduleType[0] == ModuleType.COMMON_ICCARD) {//插卡
						appendInteractiveInfoAndShow("检测到IC卡插入,开启PBOC流程...");
						mTime = System.currentTimeMillis();
						controller.startEmv(new BigDecimal("30.00"),new SimpleTransferListener());
					}
				} catch (Exception e) {
					//appendInteractiveInfoAndShow("刷卡失败！" + e.getMessage());
					//appendInteractiveInfoAndShow("刷卡失败！ +设备连接超时，请点击重置按钮再刷卡!");
					appendInteractiveInfoAndShow("交易失败!请退出页面再重新刷卡!");
					btnStateInitFinished();
					return;
				}
				btnStateInitFinished();
			}
		}).start();
		btnStateDestroyed();

	}
	
	/**
	 * 进入余额查询的确定Activity
	 */
	private void entryOtherActivity(){
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				startActivity(new Intent(SwingXDLCardBalanceActivity.this, CardBalanceConfirmActivity.class));
				finish();
				
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

	private class SimpleTransferListener implements TransferListener {
		
		private String icSecondTrackStr;//IC插卡用   磁道二 
		private String icThirdTrackStr;//IC插卡用  磁道三
		private String icCardNo;//IC插卡用   卡号
		private String icNumber;//IC插卡用   序列号
		private String icPeriod;//IC插卡用   有效期
		private String fRegoin;//IC插卡用      55域
		
		@Override
		public void onEmvFinished(boolean arg0, EmvTransInfo context) throws Exception {
//			System.out.println("emv交易结束");
//			appendInteractiveInfoAndShow("emv交易结束:" + ""/*context.externalToString()*/);
//			appendInteractiveInfoAndShow(">>>>交易完成，卡号:" + context.getCardNo() + ",卡序列号:" + context.getCardSequenceNumber());
//			appendInteractiveInfoAndShow(">>>>交易完成，密码:" + ISOUtils.hexString(context.getOnLinePin()));
//			processingFinished();
//			mTime = System.currentTimeMillis() - mTime;
//			appendInteractiveInfoAndShow("交易耗时:" + mTime/1000+"."+mTime%1000 + "s");
			
			
		}

		@Override
		public void onError(EmvTransController arg0, Exception arg1) {
			System.out.println("emv交易失败");
			appendInteractiveInfoAndShow("交易失败!请退出页面再重新刷卡!");
			processingFinished();
		}

		@Override
		public void onFallback(EmvTransInfo arg0) throws Exception {
			System.out.println("交易降级");
			appendInteractiveInfoAndShow("交易失败!请退出页面再重新刷卡!");
			// startSwipTransfer();
			processingFinished();
		}

		@Override
		public void onRequestOnline(EmvTransController transController, EmvTransInfo context) throws Exception {
			long tTime = System.currentTimeMillis() - mTime;

			//System.out.println("开启联机交易");
			//appendInteractiveInfoAndShow("开启联机交易：" + ""/*context.externalToString()*/);
			//appendInteractiveInfoAndShow(">>>>请求在线交易处理");
			//appendInteractiveInfoAndShow("		95：" + (context.getTerminalVerificationResults() == null ? "无返回" : Dump.getHexDump(context.getTerminalVerificationResults())));
			//appendInteractiveInfoAndShow("		9f26:" + (context.getAppCryptogram() == null ? "无返回" : Dump.getHexDump(context.getAppCryptogram())));
			//appendInteractiveInfoAndShow("		9f34:" + (context.getCvmRslt() == null ? "无返回" : Dump.getHexDump(context.getCvmRslt())));
			//appendInteractiveInfoAndShow(">>>>卡号:" + context.getCardNo() + ",卡序列号:" + context.getCardSequenceNumber());
			//appendInteractiveInfoAndShow(">>>>二磁等效信息:" + (context.getTrack_2_eqv_data() == null ? "无返回" : Dump.getHexDump(context.getTrack_2_eqv_data())));
			//appendInteractiveInfoAndShow(">>>>卡片有效期:" + context.getCardExpirationDate());
			
			icCardNo=context.getCardNo();
			icNumber=context.getCardSequenceNumber();
			icPeriod=context.getCardExpirationDate();
			//获取55域数据
			TLVPackage tlvPackage = context.setExternalInfoPackage(NewLandUtil.L_55TAGS);
			//appendInteractiveInfoAndShow(">>>>55域:" + ISOUtils.hexString(tlvPackage.pack()));
			fRegoin=ISOUtils.hexString(tlvPackage.pack());
			
			SwipResult swipResult=controller.readEncryptICResult();
			 byte[] secondTrack = swipResult.getSecondTrackData();
			 byte[] thirdTrack = swipResult.getThirdTrackData();
			 
			 //icSecondTrackStr= secondTrack == null ? "" : Dump.getHexDump(secondTrack);//二磁 
			 //icThirdTrackStr=thirdTrack == null ? "" : Dump.getHexDump(thirdTrack);//三磁 
			 icSecondTrackStr= secondTrack == null ? "" : new String(secondTrack, "UTF-8");//二磁 
			 icThirdTrackStr=thirdTrack == null ? "" : new String(thirdTrack, "UTF-8");//三磁 
			
			//appendInteractiveInfoAndShow("标准流程耗时:" + tTime/1000+"."+tTime%1000 + "s");
			SecondIssuanceRequest request = new SecondIssuanceRequest();
			request.setAuthorisationResponseCode("00");
			transController.secondIssuance(request);
			////////////////////////////////////插卡///////////////////////////////////
			nextStep(icCardNo, icSecondTrackStr, icThirdTrackStr, icPeriod, icNumber,fRegoin,"02");
		}

		@Override
		public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1)
				throws Exception {
			System.out.println("错误的事件返回，不可能要求密码输入");
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求密码输入！");
			arg0.cancelEmv();
		}

		@Override
		public void onRequestSelectApplication(EmvTransController arg0,
				EmvTransInfo arg1) throws Exception {
			System.out.println("错误的事件返回，不可能要求应用选择！");
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求应用选择！");
			arg0.cancelEmv();
		}

		@Override
		public void onRequestTransferConfirm(EmvTransController arg0,
				EmvTransInfo arg1) throws Exception {
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求交易确认！");
			arg0.cancelEmv();
		}

		@Override
		public void onSwipMagneticCard(SwipResult swipRslt) {
			// startSwipTransfer();
		}

		@Override
		public void onOpenCardreaderCanceled() {
			appendInteractiveInfoAndShow("用户撤销刷卡操作！");
			processingFinished();
		}

		@Override
		public void onQpbocFinished(EmvTransInfo emvTransInfo) {
			// TODO Auto-generated method stub
			
		}
	}

	private void processingFinished() {
		synchronized (processing) {
			processing = false;
		}
	}

	/**
	 * 取消操作(获取设备信息、刷卡..)
	 */
	private void onOperate() {

		new Thread(new Runnable() {
			@Override
			public void run() {
//				boolean cancel_flag = false;
//				btnStateToProcessing();
//				if (DeviceConnState.CONNECTED == controller.getDeviceConnState()) {
//					try {
//						cancel_flag = true;// 取消指令成功
//						controller.reset();
//						// appendInteractiveInfoAndShow("撤消当前指令成功!");
//					} catch (Exception e) {
//						// Log.e(TAG, "撤消指令执行失败!", e);
//						appendInteractiveInfoAndShow("撤消指令执行失败!"+ e.getMessage());
//					}
//				} else {
//					appendInteractiveInfoAndShow("设备未连接!");
//				}
//				// btnStateInitFinished();
//				processing = false;
//				if (cancel_flag && !TextUtils.isEmpty(ksn)) {
//
//					onSwipCard(SWIPCARD_ME11);// 插卡、刷卡（密文）
//
//				}
				
				btnStateToProcessing();
				boolean btnStateInit_flag=false;//状态取消标识
				boolean action_cancel_flag = onCancel();//取消获取设备信息的动作
				//boolean pourData_falg_success=false;
				if(action_cancel_flag && !TextUtils.isEmpty(csn)){
					
//					boolean pourData_falg_success=pourIntoData();//灌密钥成功的标识
//					if( pourData_falg_success){
//						
//						action_cancel_flag = onCancel();//取消灌密钥的动作
//						if(action_cancel_flag){
//							
//							btnStateInit_flag=true;
//							processing=false;
//							onSwipCard(SWIPCARD_ME11);// 插卡、刷卡（密文）
//							
//						}
//					}
					
						
					btnStateInit_flag=true;
					processing=false;
					onSwipCard(SWIPCARD_ME11);// 插卡、刷卡（密文）
							
					
				}
				
				if(!btnStateInit_flag){
					
					btnStateInitFinished();
					
				}
			};
		}).start();

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
			appendInteractiveInfoAndShow("工作密钥装载失败!" + ex);
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
	 * 获取终端列表
	 */
	public void onGetTermList() {

		jsonTermListArray = null;
		HashMap<String, String> params = new HashMap<String, String>();
		MyHttpClient.post(SwingXDLCardBalanceActivity.this,
				Urls.BIND_DEVICE_LIST, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json(responseBody);
						try {

							BasicResponse result = new BasicResponse(
									responseBody).getResult();
							if (result.isSuccess()) {

								JSONObject jsonOneBody = result.getJsonBody();
								jsonTermListArray = jsonOneBody.getJSONArray("termList");
								if (jsonTermListArray.length() > 0) {

									getDeviceInfo(FETCH_DEVICE_INFO);// 初始化设备

								}else{
									
									//restBtn.setEnabled(false);
									cashin_show_msg_text.setText("请先去设备列表界面进行设备绑定！");
									
								}
							} else {

								T.ss(result.getRSPMSG());

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {

						Logger.d(error.toString());
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
	
	
	/**
	 * 
	 * @param cardNo 银行卡号
	 * @param secondTrackStr 二磁
	 * @param thirdTrackStr  三磁
	 * @param period  有效期
	 * @param icNumber  卡序列号
	 * @param icdata    第五域
	 * @param mediaType  刷卡方式
	 */
	private void nextStep(String cardNo,String secondTrackStr,String thirdTrackStr,String period,String icNumber,String icdata,String mediaType){
		
		PosData.getPosData().setCardNo(cardNo);
		PosData.getPosData().setRate("1");
		PosData.getPosData().setTermNo(csn);
		PosData.getPosData().setTermType("02");
		//PosData.getPosData().setPayAmt(amount);
		PosData.getPosData().setTrack(secondTrackStr + "|"+thirdTrackStr);
		PosData.getPosData().setRandom("000000");
		PosData.getPosData().setMediaType(mediaType);//磁卡类型01 磁条卡 02 IC卡
		PosData.getPosData().setPeriod(period);
		PosData.getPosData().setCrdnum(icNumber);
		PosData.getPosData().setIcdata(icdata);
		entryOtherActivity();
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
			case R.id.btn_back:
				finish();
				break;
		}
		
	}
	
}

