package com.lk.td.pay.activity.main.cashin;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hx.newland.cashin.swing.xdl.bluetootch.Const;
import com.hx.newland.cashin.swing.xdl.bluetootch.pinInput.PinInputInterfaceImpl;
import com.hx.pay.activity.newland.BuletootchController;
import com.hx.pay.activity.newland.BuletootchControllerImpl;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.CardBalance;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.AmountUtils;
import com.pay.library.uils.PinDes;
import com.td.app.pay.hx.R;
import com.whty.tymposapi.DeviceApi;


public class CardBalanceConfirmActivity extends BaseActivity implements OnClickListener {

    private TextView cardNoText;
    private EditText cardPwdEdit;
    private String cardPwd;
    private Context mContext;
    private DeviceApi deviceApi;
    private Button btn_back;
    private SwipResult swipRslt;
    private BuletootchController buletootchController = BuletootchControllerImpl.getInstance();
    private PinInputInterfaceImpl pinInputInterfaceImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_confirm);
        mContext = this;
        init();
    }

    private void init() {

        pinInputInterfaceImpl = new PinInputInterfaceImpl();
        swipRslt = PosData.getPosData().getSwipResult();

        deviceApi = new DeviceApi(getApplicationContext());
        cardNoText = (TextView) findViewById(R.id.cashin_card_no_text);
        cardPwdEdit = (EditText) findViewById(R.id.cash_bank_pwd_edit);
        cardNoText.setText(Utils.hiddenCardNo(PosData.getPosData().getCardNo()));

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setText("银行卡查询");
        btn_back.setOnClickListener(this);
        findViewById(R.id.balance_confirm_btn).setOnClickListener(this);

        if (MApplication.getInstance().isKeymap()){
            try {
                buletootchController.isConnected();
                Logger.d("设备连接。。。。");

            }catch (Exception e){
                Logger.d("设备没有连接。。。");
                T.sl("设备连接断开，交易中断");
                finish();
            }
        }
        if (MApplication.getInstance().isKeymap()){
            cardPwdEdit.setText("123456");
            cardPwdEdit.setEnabled(false);
            cardPwdEdit.clearFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_confirm_btn:

                cardPwd = cardPwdEdit.getText().toString().trim();
                if (TextUtils.isEmpty(cardPwd)) {
                    showDialog(getResources().getString(R.string.inputbankCardPwd));
                } else if (cardPwd.length() != 6) {
                    showDialog("银行卡密码长度应为6位数");
                } else {
//					if (deviceApi.isConnected()) {
//						goPay("");
//					}else {
                    downloadPineky();
//					}
                }


                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }

    }

    private void downloadPineky() {
        //终端签到
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("termNo", PosData.getPosData().getBluetoothTermNo());
        params.put("termType", PosData.getPosData().getTermType());
        MyHttpClient.post(this, Urls.BLUETOOTH_SIGN, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        try {
                            BasicResponse re = new BasicResponse(responseBody).getResult();
                            if (re.isSuccess()) {
                                String pinkey = re.getJsonBody().optString("zpinkey");
                                String zpincv = re.getJsonBody().optString("zpincv");
                                if (MApplication.getInstance().isKeymap()){
                                    loadWkey(pinkey, zpincv);
                                }else{
                                    goPay(pinkey);
                                }
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
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }
    public void loadWkey(String pinKey, String zpincv){
        if (null != buletootchController){
            try {
                buletootchController.isConnected();
            }catch (Exception e){
                T.sl("设备连接断开，交易中断");
                finish();
            }
        }
        byte[] buff = pinInputInterfaceImpl.loadWorkingKey(WorkingKeyType.PININPUT,
                Const.MKIndexConst.DEFAULT_MK_INDEX, Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX,
                ISOUtils.hex2byte(pinKey));
        String checkValue = Dump.getHexDump(buff).replace(" ", "");
        Log.d("==checkValue =", "" + checkValue + "========" + zpincv.substring(0, 4));
        if (!Dump.getHexDump(buff).replace(" ", "").startsWith(zpincv.substring(0, 4))) {
            T.sl("PinKey 校验失败！");

            finish();
        } else {
            getInputPassword(pinKey);
        }

    }
    /**
     * 获取键盘输入的密码
     */
    public void getInputPassword(String pinKey){

        new GetInputKeyPassword(pinKey).execute();
    }
    public class GetInputKeyPassword extends AsyncTask<Void, Void, PinInputEvent> {

        private String pinKey;
        public GetInputKeyPassword(String pinKey){
            this.pinKey = pinKey;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog("请在终端输入密码");
        }

        @Override
        protected PinInputEvent doInBackground(Void... voids) {
            try {
                PinInputEvent event = pinInputInterfaceImpl.startStandardPinInput(new WorkingKey(Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX),
                        PinManageType.MKSK, AccountInputType.USE_ACCT_HASH, swipRslt
                                .getAccount().getAcctHashId(), 6, new byte[]{'F', 'F', 'F',
                                'F', 'F', 'F', 'F', 'F'}, true, "请输入密码", 30,
                        TimeUnit.SECONDS);
                return event;
            }catch (Exception e){

                return null;
            }

        }

        @Override
        protected void onPostExecute(PinInputEvent event) {
            super.onPostExecute(event);
            dismissLoadingDialog();

            if (null == event) {
                Log.d("密码错误", "===========================");
            } else if (event.isSuccess()) {
                Log.d("KSN ", "" + Dump.getHexDump(event.getKsn()));
                Log.d("密文密码", "=====" + Dump.getHexDump(event.getEncrypPin()));
                cardPwd = Dump.getHexDump(event.getEncrypPin()).replaceAll(" ", "");
                goPay(pinKey);
            } else if (event.isUserCanceled()) {
                Log.d("密码异常", "====用户取消输入");
            }
        }
    }
    private void goPay(String pinkey) {
        try {

//			if(deviceApi.isConnected()) {
//				cardPwd = deviceApi.getEncPinblock(cardPwd);
//			}else {
            cardPwd = PinDes.pinResultMak(PinDes.ZMK, pinkey, PosData.getPosData().getCardNo(), cardPwd);
//			}
        } catch (Exception e) {

        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("termNo", PosData.getPosData().getBluetoothTermNo());
        System.out.println(PosData.getPosData().getBluetoothTermNo() + "_________________________");
        params.put("termType", PosData.getPosData().getTermType());
        params.put("track", PosData.getPosData().getTrack());
        params.put("pinblk", cardPwd);
        params.put("random", PosData.getPosData().getRandom());
        params.put("mediaType", PosData.getPosData().getMediaType());
        params.put("period", PosData.getPosData().getPeriod());
        params.put("icdata", PosData.getPosData().getIcdata());
        params.put("crdnum", PosData.getPosData().getCrdnum());
        params.put("mac", "");
        MyHttpClient.post(mContext, Urls.CARD_QUERY,
                params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(responseBody);
                        try {
                            PosData.getPosData().clearPosData();
                            CardBalance r = new CardBalance(responseBody).getResult();
                            Intent it = new Intent(mContext, ShowMsgActivity.class);
                            it.setAction("ACTION_CARD_QUERY");
                            if (r.isSuccess()) {
                                String tempBalance = r.getBalance();
                                tempBalance = AmountUtils.changeFen2Yuan(tempBalance);
                                it.putExtra("code", true);
                                it.putExtra("msg", tempBalance + "元");
                            } else {
                                it.putExtra("code", false);
                                it.putExtra("msg", r.getMsg());
                            }
                            startActivity(it);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showDialog("数据解析失败");
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
                        if (MApplication.getInstance().isKeymap()){
                            if (null != buletootchController){
                                buletootchController.destroy();
                            }
                        }
                    }
                });
    }

    private void yuMeiBluetoothgoPay() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("termNo", PosData.getPosData().getBluetoothTermNo());
        params.put("termType", PosData.getPosData().getTermType());
        params.put("track", PosData.getPosData().getTrack());
        params.put("pinblk", PosData.getPosData().getPinblk());
        params.put("random", PosData.getPosData().getRandom());
        params.put("mediaType", PosData.getPosData().getMediaType());
        params.put("period", PosData.getPosData().getPeriod());
        params.put("icdata", PosData.getPosData().getIcdata());
        params.put("crdnum", PosData.getPosData().getCrdnum());    //卡片序列号
        params.put("mac", "");
        MyHttpClient.post(mContext, Urls.CARD_QUERY,
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(responseBody);
                        try {
                            PosData.getPosData().clearPosData();
                            CardBalance r = new CardBalance(responseBody).getResult();
                            Intent it = new Intent(mContext, ShowMsgActivity.class);
                            it.setAction("ACTION_CARD_QUERY");
                            if (r.isSuccess()) {
                                String tempBalance = r.getBalance();
                                it.putExtra("code", true);
                                it.putExtra("msg", tempBalance + "元");
                            } else {
                                it.putExtra("code", false);
                                it.putExtra("msg", r.getMsg());
                            }

                            startActivity(it);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showDialog("数据解析失败");
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        //if(responseBody.length<1)
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

