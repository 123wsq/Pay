package com.hx.pay.activity.newland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MessageTag;
import com.hx.newland.cashin.swing.xdl.bluetootch.pinInput.ME3xDeviceDriver;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.CardBalanceConfirmActivity;
import com.lk.td.pay.activity.main.cashin.SignaturePadActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.CardRule;
import com.newland.mtype.module.common.cardreader.OpenCardType;
import com.newland.mtypex.bluetooth.BlueToothV100ConnParams;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.AmountUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SwingCardByXDLBluetootchActivity extends BaseActivity {
    private static final String ME3X_DRIVER_NAME = "com.newland.me.ME3xDriver";
    private SwingCardByXDLBluetootchActivity swingcardbyxdlbluetootchactivity;
    private String deviceInteraction = "", newstring;

    private BuletootchController controller = BuletootchControllerImpl.getInstance();
    private TextView cashin_account_text;// 充值钱数
    private TextView cashin_show_msg_text;// 刷卡状态
    private String amount = "";// 充值数量
    private LinearLayout  cashin_step_two_layout;// 返回
    //private String ksn;
    private String csn;
    private JSONArray jsonTermListArray = null;
    private Boolean isFrist = false;
    private Button btn_back, btn_reset;
    private TextView tv_title;
    public static  boolean isScanSuccess = false;
    
    public static final int MSG_RESULT = 0; 
    public static final int BIND_S = 1;
    
    public int state = 0;
    
    public boolean isResult  = false;
    private MyAsyncTask asyncTask;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swing_card_audio);
        swingcardbyxdlbluetootchactivity = SwingCardByXDLBluetootchActivity.this;
        PosData.getPosData().getActtext();
        initUI();
        String blueTootchAddress = User.macAddress;
        initMe3xDeviceController(ME3X_DRIVER_NAME, new BlueToothV100ConnParams(blueTootchAddress));
       
        getDeviceInfo();

    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    }

    private void initUI() {
        cashin_account_text = (TextView) findViewById(R.id.cashin_account_text);
        cashin_step_two_layout = (LinearLayout) findViewById(R.id.cashin_step_two_layout);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_back.setVisibility(View.VISIBLE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("刷卡支付");
        if (PosData.getPosData().getActtext().equals("ACTION_QUERY_BALANCE")) {
            amount = "1";
            cashin_step_two_layout.setVisibility(View.GONE);
            tv_title.setText("余额查询");
        } else {
            amount = PosData.getPosData().getPayAmt();

        }
        cashin_account_text.setText(AmountUtils.changeFen2Yuan(amount) + "元");
        cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text);
        btn_reset.setVisibility(View.VISIBLE);
        btn_reset.setText("重置");
        btn_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!isFrist){
					String blueTootchAddress = User.macAddress;
					initMe3xDeviceController(ME3X_DRIVER_NAME,new BlueToothV100ConnParams(blueTootchAddress));
					System.out.println("控制器已初始化!");
					getDeviceInfo();
					
				}else{
					onSwipCard();
				}
			}
		});
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null!=controller){
                    controller.destroy();
                    
                }
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public BuletootchController initMe3xDeviceController(String driverPath, DeviceConnParams params) {
        ME3xDeviceDriver me3xDeviceController = new ME3xDeviceDriver(SwingCardByXDLBluetootchActivity.this);
        me3xDeviceController.initMe3xDeviceController(driverPath, params);
        
        controller.init(SwingCardByXDLBluetootchActivity.this, driverPath, params, new DeviceEventListener<ConnectionCloseEvent>() {
            @Override
            public void onEvent(ConnectionCloseEvent event, Handler handler) {
            	Message message = new Message();
                message.what = MSG_RESULT;
                if (event.isSuccess()) {
                	message.obj =  "设备被客户主动断开！";
                }
                if (event.isFailed()) {
                	message.obj="设备被客户主动断开！"+ event.getException().getMessage();
                }
                mHandler.sendMessage(message);
            }

            @Override
            public Handler getUIHandler() {
                return null;
            }
        });
        
        Log.d("驱动版本号：", ""+controller.getCurrentDriverVersion());
        return controller;
    }



    public void connectDevice() {
    	Message message = new Message();
    	message.what = MSG_RESULT;
    	message.obj =  "设备连接中...";
        try {
            controller.connect();
            isFrist = true;
            message.obj = "设备连接成功...";
        } catch (Exception e1) {
            e1.printStackTrace();
            message.obj = "设备链接异常断开...！";
        }
        mHandler.sendMessage(message);
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
    			
    	
        connectDevice();
        try {
        	Message message = new Message();
        	message.what =  MSG_RESULT;
        	message.obj = "连接设备中！";
        	mHandler.sendMessage(message);
            processingLock();
            DeviceInfo deviceInfo = controller.getDeviceInfo();
            csn = deviceInfo.getCSN();
        } catch (Exception e) {
        	Message message = new Message();
        	message.what = MSG_RESULT;
            message.obj = "获取设备信息失败，确保刷卡头已连接！";
            mHandler.sendMessage(message);
            return;
        } finally {
            processingUnLock();

        }
        onSwipCard();// 插卡、刷卡（密文）
    }

    public void processingUnLock() {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("PBOC_LOCK", false);
        editor.commit();
        return;
    }

    public void processingLock() {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("PBOC_LOCK", true);
        editor.commit();
    }

    /**
     * 刷卡
     *
     *
     */
    public void onSwipCard() {

        processingisLocked();

        asyncTask = new MyAsyncTask();
        asyncTask.execute();
    }

    public class MyAsyncTask  extends AsyncTask<Void, Void, Integer>{
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		state =0;
    		super.onPreExecute();
    		Message message = new Message();
    		message.what = MSG_RESULT;
    		message.obj = "请刷卡或插入IC卡...";
    		mHandler.sendMessage(message);
    		isResult = false;
    		
    	}
    	@Override
    	protected Integer doInBackground(Void... params) {
    		// TODO Auto-generated method stub
    		
    	        try {
    	        		
    	        	Log.d("======", "====正在刷卡=======");
    	            String amtNum = AmountUtils.changeFen2Yuan(amount);

    	            DecimalFormat df = new DecimalFormat("#.00");
    	            BigDecimal amt = new BigDecimal(amtNum);

    	            controller.startTransfer(swingcardbyxdlbluetootchactivity,
    	                    new OpenCardType[]{OpenCardType.SWIPER, OpenCardType.ICCARD, OpenCardType.NCCARD},
    	                    "交易金额为:" + df.format(amt)+ " \n请刷卡或者插入IC卡", amt, 60,
    	                    TimeUnit.SECONDS, CardRule.ALLOW_LOWER,
    	                    simpleTransferListener);
    	            
    	            Thread.sleep(1000);
    	        } catch (Exception e1) {
    	        	Log.e("交易拋出异常", ""+e1.getMessage());
//    	        	PosData.getPosData().setErr(e1.getMessage());
    	        	state = -2;
    	            return state;
    	        }
    		return state;
    	}
    	
    	@Override
    	protected void onPostExecute(Integer result) {
    		// TODO Auto-generated method stub
    		
    		Log.d("=========", "交易状态===" +result);
    		switch (result) {
    		case 0:
    				
    			Log.d("======", "交易。。。"+isResult);
    			if (!isResult) {
					Message msg = new Message();
					msg.what = MSG_RESULT;
					msg.obj = "交易失败";
	    			mHandler.sendMessage(msg);
				}
    			break;
    		case -2:
    			isResult = true;
    			isScanSuccess = false;
    			Message message = new Message();
        		message.what = MSG_RESULT;
    			message.obj =  PosData.getPosData().getErr();
    			mHandler.sendMessage(message);
    			break;

			default:
				
				break;
			}
    		
    	}
    }
    
 
    
    SimpleTransferListener simpleTransferListener =  new SimpleTransferListener(SwingCardByXDLBluetootchActivity.this) {
    	
    	@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
    		isResult = true;
			Log.d("====onSuccess===", "刷卡成功");
			Log.d("=========","csn : " + csn);
            Message message = new Message();
            message.what = MSG_RESULT;
            message.obj =  "交易成功";
            isScanSuccess = true;
            mHandler.sendMessage(message);
    		PosData.getPosData().setPayAmt(amount);
            PosData.getPosData().setTermNo(csn);
    		entryOtherActivity();
		}
		

		@Override
		public void onFailed(String arg0) {
			// TODO Auto-generated method stub
			Log.d("===onFailed===", "交易失败");
			isResult = true;
			isScanSuccess = false;
			Message message = new Message();
			message.what = MSG_RESULT;
			message.obj =  arg0;
			mHandler.sendMessage(message);
		}


		@Override
		public void onStatus() {
			// TODO Auto-generated method stub
			isResult = true;
		}
		
	};

    /**
     * 进入签名的Activity
     */
    private void entryOtherActivity() {

        if (TextUtils.isEmpty(PosData.getPosData().getCardNo())) {
        	Message message = new Message();
        	message.what = MSG_RESULT;
        	message.obj = "获取磁道信息失败！";
        	mHandler.sendMessage(message);
            return;
        } else {
        	
        	if (isScanSuccess) {
	            if (PosData.getPosData().getActtext().equals("ACTION_QUERY_BALANCE")) {//余额查询
	            	
	                startActivity(new Intent(SwingCardByXDLBluetootchActivity.this, CardBalanceConfirmActivity.class));
	                finish();
	            } else if (PosData.getPosData().getActtext().equals("ACTION_QUERY")) {  //交易签名
	            	Intent intent = new Intent(SwingCardByXDLBluetootchActivity.this, SignaturePadActivity.class);
	            	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	                startActivity(intent);
	                finish();
	            }
        	
			}
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDeviceController();
        asyncTask.cancel(true);
        Log.d("====", "======执行销毁方法======");
    }

    /**
     * 销毁设备控制器
     *
     * @since ver1.0
     */
    private void destroyDeviceController() {
        if (!MApplication.getInstance().isKeymap()){
            controller.destroy(); // 终端后的相关状态处理会通过事件完成,此处不需要处理
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode== KeyEvent.KEYCODE_BACK){
            if (null != controller){
            	
            	controller.destroy();
                
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 获取终端列表
     */
    public void onGetTermList() {

        jsonTermListArray = null;
        HashMap<String, String> params = new HashMap<String, String>();
        MyHttpClient.post(SwingCardByXDLBluetootchActivity.this,
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

                                    getDeviceInfo();// 初始化设备

                                } else {
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


    public void appendInteractiveInfoAndShow(final String string, final int messageTag) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (messageTag) {
                    case MessageTag.NORMAL:
                        newstring = "<font color='black'>" + string + "</font>";
                        break;
                    case MessageTag.ERROR:
                        newstring = "<font color='red'>" + string + "</font>";
                        break;
                    case MessageTag.TIP:
                        newstring = "<font color='blue'>" + string + "</font>";
                        break;
                    case MessageTag.DATA:
                        String arr[] = string.split(":");
                        newstring = "<font color='green'>" + arr[0] + "</font>" + ":" + arr[1];
                        break;
                    default:
                        break;
                }
                deviceInteraction = newstring + "<br>" + deviceInteraction;
                System.err.println(Html.fromHtml(deviceInteraction));
            }
        });
    }


    public boolean processingisLocked() {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        if (setting.getBoolean("PBOC_LOCK", true)) {
            return true;
        } else {
            return false;
        }
    }


    Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		
    		switch (msg.what) {
			case MSG_RESULT:
				if (msg.obj == null) {
					msg.obj = "交易失败";
				}
				cashin_show_msg_text.setText(msg.obj.toString());
				break;
			default:
				break;
			}
    	};
    	
    };

}
