package com.hx.pay.activity.ty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MessageTag;
import com.hx.pay.activity.newland.AbstractDevice;
import com.hx.pay.activity.newland.BuletootchController;
import com.hx.pay.activity.newland.BuletootchControllerImpl;
import com.hx.ty.cashin.DeviceDelegate;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.CardBalanceConfirmActivity;
import com.lk.td.pay.activity.main.cashin.SignaturePadActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.utils.T;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.AmountUtils;
import com.td.app.pay.hx.R;
import com.whty.bluetoothsdk.util.Utils;
import com.whty.comm.inter.ICommunication;
import com.whty.tymposapi.DeviceApi;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SwingCardByTYBluetootchActivity extends BaseActivity {
    private static final String ME3X_DRIVER_NAME = "com.newland.me.ME3xDriver";
    private static final String ME11_DRIVER_NAME = "com.newland.me.ME11Driver";
    private static final String K21_DRIVER_NAME = "com.newland.me.K21Driver";
    private SwingCardByTYBluetootchActivity swingcardbyxdlbluetootchactivity;
    private String deviceInteraction = "", newstring;
    private static final int FETCH_DEVICE_INFO_ME11 = 0;
    private AbstractDevice connected_device;
    private static final int SWIPCARD_PLAIN_ME11 = 2;
    private static final int PININPUT_ME11 = 3;
    private static final int CANCEL = 4;
    private static final int FETCH_DEVICE_INFO = 5;
    private static final int SWIPCARD = 6;
    private static final int SWIPCARD_PLAIN = 7;
    private static final int FETCH_POWER_LEVEL = 8;
    private static final int MAC = 9;
    private static final int LOAD_WORKKEY = 10;
    private BuletootchController controller = BuletootchControllerImpl.getInstance();
    private TextView cashin_account_text;// 充值钱数
    private TextView cashin_show_msg_text;// 刷卡状态
    private ListView appListView;
    private String amount = "";// 充值数量
    private TextView restBtn, titl;// 重置
    private LinearLayout backBtn, cashin_step_two_layout;// 返回
    private Boolean processing = false;
    //private String ksn;
    private String csn;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    private long mTime = 0;
    // private boolean service_flag=false;//服务请求标识
    // private boolean termList_flag=false;//终端列表标识
    private JSONArray jsonTermListArray = null;
    private boolean sign_flag = false;// 签到标识
    private String termTdk;//密文密钥
    private String termTdkCv;
    private static DeviceManager deviceManager = ConnUtils.getDeviceManager();
    private Boolean isFrist = false;
    private String SDCardPath;
    private DeviceApi deviceApi;
    private String blueTootchAddress;
    private byte tradeType = (byte) 0x00;
    private MyHandler mHandler;
    private DeviceDelegate delegate;
    private String cdmy = "465F9AEA61A12A13A87110221B70BFFBC54FAF3A";
    private Button btn_back;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swing_card_audio);
        swingcardbyxdlbluetootchactivity = SwingCardByTYBluetootchActivity.this;

        PosData.getPosData().getActtext();
        initUI();
        //blue8C:DE:52:4A:F4:27
        blueTootchAddress = User.macAddress;
        btnStateToWaitingInitFinished();
        mHandler = new MyHandler();
        delegate = new DeviceDelegate(mHandler);
        deviceApi = new DeviceApi(getApplicationContext());
        deviceApi.setDelegate(delegate);
        if (!deviceApi.isConnected()) {
            deviceApi.initDevice(ICommunication.BLUETOOTH_DEVICE);
        }
//        initMe3xDeviceController(ME3X_DRIVER_NAME, new BlueToothV100ConnParams(blueTootchAddress));
        System.out.println("控制器已初始化!");
        getDeviceInfo();
    }


    private void initUI() {
        cashin_account_text = (TextView) findViewById(R.id.cashin_account_text);
        cashin_step_two_layout = (LinearLayout) findViewById(R.id.cashin_step_two_layout);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        tv_title.setText("刷卡支付");
        if (PosData.getPosData().getActtext().equals("ACTION_QUERY_BALANCE")) {
            amount = "1";
            cashin_step_two_layout.setVisibility(View.GONE);
            tv_title.setText("余额查询");
            tradeType = (byte) 0x31;
        } else {
            amount = PosData.getPosData().getPayAmt();

        }

        cashin_account_text.setText(AmountUtils.changeFen2Yuan(amount) + "元");
        cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text);
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {

        finish();

    }

    public BuletootchController initMe3xDeviceController(String driverPath, DeviceConnParams params) {
        controller.init(SwingCardByTYBluetootchActivity.this, driverPath, params, new DeviceEventListener<ConnectionCloseEvent>() {
            @Override
            public void onEvent(ConnectionCloseEvent event, Handler handler) {
                if (event.isSuccess()) {
                    appendInteractiveInfoAndShow("设备被客户主动断开！");
                }
                if (event.isFailed()) {
                    appendInteractiveInfoAndShow("设备链接异常断开！" + event.getException().getMessage());
                }
            }

            @Override
            public Handler getUIHandler() {
                return null;
            }
        });
        System.out.println("驱动版本号：" + controller.getCurrentDriverVersion());
        return controller;

    }

    private void appendInteractiveInfoAndShow(final String string) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

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
                processing = true;
            }
        });
    }

    public void connectDevice() {
        appendInteractiveInfoAndShow("设备连接中...");
        try {
//            controller.connect();
            deviceApi.connectDevice(blueTootchAddress);
            isFrist = true;
            appendInteractiveInfoAndShow("设备连接成功...");
        } catch (Exception e1) {
            e1.printStackTrace();
            appendInteractiveInfoAndShow("设备链接异常断开...");
            //throw new RuntimeException(e1.getMessage(), e1);
        }
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!deviceApi.isConnected())
                    connectDevice();
                try {
                    processingLock();
//                    DeviceInfo deviceInfo = controller.getDeviceInfo();
//                    csn = deviceInfo.getCSN();
                    csn = deviceApi.getDeviceCSN().substring(2);
                    PosData.getPosData().setTermNo(csn);
                    PosData.getPosData().setTermType("01");
                } catch (Exception e) {
                    //appendInteractiveInfoAndShow(PosData.getPosData().getErr());
                    appendInteractiveInfoAndShow("获取设备信息失败，确保刷卡头已连接！");
                    return;
                } finally {
                    processingUnLock();

                }
//                boolean[] isSuccess = deviceApi.updateWorkingKey(cdmy, null, null);
//                if (isSuccess[0]) {
                onSwipCard();// 插卡、刷卡（密文）
//                } else {
//                    appendInteractiveInfoAndShow("更新磁道密钥失败，请重试！");
//                }
            }
        }).start();
    }

    private void downloadPineky() {
        // 终端签到
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("termNo", PosData.getPosData().getTermNo());
        params.put("termType", PosData.getPosData().getTermType());
        MyHttpClient.post(this, Urls.BLUETOOTH_SIGN, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        try {
                            BasicResponse re = new BasicResponse(responseBody)
                                    .getResult();
                            if (re.isSuccess()) {
                                String pinkey = re.getJsonBody().optString(
                                        "zpinkey");
//                                goPay(pinkey);
                            } else {
                                showDialog(re.getMsg());
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
     *
     */
    public void DisplayDialog() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Toast.makeText(SwingCardByTYBluetootchActivity.this, "此设备没有进行绑定，请先到绑定界面进行绑定！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 刷卡
     * <p/>
     * //     * @param whereType
     */
    public void onSwipCard() {

        processingisLocked();
        appendInteractiveInfoAndShow("请刷卡或插入IC卡...");
        System.err.println("========s=================================================");
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            final BigDecimal amt;
            amt = new BigDecimal(amount);
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyyMMddHHmmss", Locale.getDefault());
            String terminalTime = format.format(new Date());
            Log.e("swipeCardThread===", "terminalTime:" + terminalTime);
            // 例如2014-12-03 16:20:55 则terminalTime传入"";
            // 传入金额的时候注意不要传小数点，如果想要传1.50则写入"150";
            // 传入交易类型 (byte)0x00代表消费，(byte)0x31代表查询余额
            String termNo = deviceApi
                    .getMacWithMKIndex(0,
                            Utils.hexString2Bytes("0200702404c030c098111962122632020070732980000000000000000001000002230902100012376212263202007073298d23092205739991617f0094996212263202007073298d1561560000000000001003573999010000023090d000000000000d00000000d00000000f31313034303332363830363030313030303031303534383135367d109d8d4ad0a72a2600000000000000001322000006000000"));
            PosData.getPosData().setTermNo(termNo);
            PosData.getPosData().setBluetoothTermNo(deviceApi.getDeviceSN().substring(2));
//            deviceApi.getMacWithMKIndex()
            HashMap mMap = deviceApi.readCard(String.valueOf(amt), terminalTime.substring(2), tradeType,
                    (byte) 0x64, (byte) 0x00);
//            delegate.onReadCard(mMap);
            dealWithData(mMap);
//            PosData.getPosData().setPinKey(mMap.get("pin").toString());
//            controller.startTransfer(swingcardbyxdlbluetootchactivity,
//                    new OpenCardType[]{OpenCardType.SWIPER, OpenCardType.ICCARD, OpenCardType.NCCARD},
//                    "交易金额为:" + df.format(amt) + "\n请刷卡或者插入IC卡", amt, 60,
//                    TimeUnit.SECONDS, CardRule.ALLOW_LOWER,
//                    new SimpleTransferListener(connected_device, swingcardbyxdlbluetootchactivity));
            PosData.getPosData().setPayAmt(amount);
            PosData.getPosData().setTermNo(csn);
            entryOtherActivity();
        } catch (Exception e1) {
            appendInteractiveInfoAndShow(PosData.getPosData().getErr());
            //appendInteractiveInfoAndShow("刷卡失败！ 设备连接超时，请点击重置按钮再刷卡!");
            btnStateInitFinished();
            return;
        }
            /*new Thread(new Runnable() {

				@Override
				public void run() {

						try {
							System.err.println("========d==================================================");
							connected_device.getController().startTransfer(swingcardbyxdlbluetootchactivity,  new OpenCardType[]{OpenCardType.SWIPER,OpenCardType.ICCARD,OpenCardType.NCCARD}, "交易金额为:" + amount + "\n请刷卡或者插入IC卡", amount, 60, TimeUnit.SECONDS, CardRule.ALLOW_LOWER,  new SimpleTransferListener(connected_device, swingcardbyxdlbluetootchactivity));
						} catch (Exception e) {
							if (e instanceof ProcessTimeoutException) {
								appendInteractiveInfoAndShow("swipe failed:超时!" + e.getMessage());
								processingUnLock();
								return;
							} else if (e instanceof DeviceRTException) {
								appendInteractiveInfoAndShow("请重新刷卡或插卡" + e.getMessage());
								// mainActivity.appendInteractiveInfoAndShow("请重新刷卡或插卡",
								// MessageTag.TIP);
							}

						}
					}



			});*/
    }

    private void dealWithData(HashMap mMap) {
        if (!mMap.get("errorCode").toString().equals("9000")) {
            appendInteractiveInfoAndShow("刷卡失败！ 请返回重试！");
            return;
        }
        if (mMap.containsKey("cardType")) {
            if (!mMap.get("cardType").toString().isEmpty()) {
                if (mMap.get("cardType").toString().equals("00")) {//磁条卡
                    PosData.getPosData().setMediaType("01");
                    PosData.getPosData().setPeriod(mMap.get("expiryDate").toString());
                    PosData.getPosData().setCardNo(mMap.get("cardNumber").toString());
                    PosData.getPosData().setServiceCode(mMap.get("serviceCode").toString());
                    PosData.getPosData().setTrack(mMap.get("encTrack2Ex").toString() + "|" + mMap.get("encTrack3Ex").toString());
                } else if (mMap.get("cardType").toString().equals("01")) {//芯片卡
                    PosData.getPosData().setMediaType("02");
                    PosData.getPosData().setTrack(mMap.get("encTrack2Ex").toString() + "|");
                    PosData.getPosData().setIcdata(mMap.get("icData").toString());
                    PosData.getPosData().setPeriod(mMap.get("expiryDate").toString());
                    PosData.getPosData().setCrdnum(mMap.get("cardSeqNum").toString());
                    PosData.getPosData().setCardNo(mMap.get("cardNumber").toString());
                }
            }
            if (mMap.get("randomNum").toString().isEmpty()) {
                PosData.getPosData().setRandom("CDB9C9D14724091B");
            } else {
                PosData.getPosData().setRandom(mMap.get("randomNum").toString());
            }
        }
    }


    /**
     * 进入签名的Activity
     */
    private void entryOtherActivity() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (PosData.getPosData().getCardNo().equals("")) {
                    appendInteractiveInfoAndShow("获取磁道信息失败！");
                    return;
                } else {
                    if (PosData.getPosData().getActtext().equals("ACTION_QUERY_BALANCE")) {
                        startActivity(new Intent(SwingCardByTYBluetootchActivity.this, CardBalanceConfirmActivity.class));
                        finish();
                    } else if (PosData.getPosData().getActtext().equals("ACTION_QUERY")) {
                        startActivity(new Intent(SwingCardByTYBluetootchActivity.this, SignaturePadActivity.class));
                        finish();
                    }
                }

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
     * 取消当前的操作
     */
    private boolean onCancel() {

        boolean cancel_flag = false;// 取消指令
        if (DeviceConnState.CONNECTED == controller.getDeviceConnState()) {
            try {
                controller.reset();
                cancel_flag = true;
            } catch (Exception e) {
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
        MyHttpClient.post(SwingCardByTYBluetootchActivity.this,
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


    /**
     * @param cardNo         银行卡号
     * @param secondTrackStr 二磁
     * @param thirdTrackStr  三磁
     * @param period         有效期
     * @param icNumber       卡序列号
     * @param icdata         第五域
     * @param mediaType      刷卡方式
     */
    private void nextStep(String cardNo, String secondTrackStr, String thirdTrackStr, String period, String icNumber, String icdata, String mediaType) {

        PosData.getPosData().setPayAmt(amount);
        PosData.getPosData().setTermNo(csn);
        entryOtherActivity();
        PosData.getPosData().setCardNo(cardNo);
        PosData.getPosData().setRate("1");
        PosData.getPosData().setTermType("02");
        PosData.getPosData().setTrack(secondTrackStr + "|" + thirdTrackStr);
        PosData.getPosData().setRandom("000000");
        PosData.getPosData().setMediaType(mediaType);//磁卡类型01 磁条卡 02 IC卡
        PosData.getPosData().setPeriod(period);
        PosData.getPosData().setCrdnum(icNumber);
        PosData.getPosData().setIcdata(icdata);


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

    /**
     * 设置成初始化结束状态
     *
     * @since ver1.0
     */
    public void btnStateInitFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processing = false;
            }
        });

    }

    public void doPinInputShower(final boolean isNext) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isNext) {
                    deviceInteraction = "*" + deviceInteraction;
                    System.err.println(Html.fromHtml(deviceInteraction));
                } else {
                    deviceInteraction = deviceInteraction.substring(1, deviceInteraction.length());
                    System.err.println(Html.fromHtml(deviceInteraction));
                }

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
                processing = true;
            }
        });
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            String show_msg = "";
            switch (msg.what) {

                case 31:

                    show_msg = (String) msg.obj;
                    Toast.makeText(SwingCardByTYBluetootchActivity.this, show_msg, Toast.LENGTH_SHORT);
//                    if (show_msg.equals("连接设备成功！"))
//                        deviceConnected = true;

                    break;

                case 32:

                    show_msg = (String) msg.obj;
                    Toast.makeText(SwingCardByTYBluetootchActivity.this, show_msg, Toast.LENGTH_SHORT);
                    break;

                default:
                    break;
            }
        }
    }

}
