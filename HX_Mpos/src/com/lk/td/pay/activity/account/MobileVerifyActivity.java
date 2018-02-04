package com.lk.td.pay.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.td.pay.activity.more.ProtocolActivity;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.Entity;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.android.http.JsonHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.request.ParamsUtils;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.ExpresssoinValidateUtil;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 获取手机验证码
 *
 * @author Ding
 */
public class MobileVerifyActivity extends BaseActivity implements
        OnClickListener {

    private Button btnNextStep, btnGetVerify;
    private EditText etPhone, etphoneVerify;
    private String userName, title;
    private boolean hasSend;

    /**
     * 注册获取验证码
     */
    public static final String ACTION_REGISTER = "0";
    /**
     * 找回登录密码获取验证码
     */
    public static final String ACTION_FORGET_LOGIN_PWD = "1";
    /**
     * 找回支付密码获取验证码
     */
    public static final String ACTION_FORGET_PAY_PWD = "2";

    private String action = "-1";
    private CheckBox checkbox;
    Button  btn_back;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_verify);
        action = getIntent().getAction();
        if (null == action) {
            throw new NullPointerException("获取手机验证码，请求类型不能为空");
        }
        initView();
    }

    private void initView() {
        if (action.equals(ACTION_REGISTER)) {
            title = "注册";
        } else if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
            title = "找回登录密码";
        } else if (action.equals(ACTION_FORGET_PAY_PWD)) {
            title = "找回支付密码";
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        btn_back = ((Button)findViewById(R.id.btn_back));
        btn_back.setText("返回");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        btnNextStep = (Button) findViewById(R.id.btn_mobile_verify_next_step);
        btnNextStep.setOnClickListener(this);
        btnGetVerify = (Button) findViewById(R.id.btn_mobile_verify_getverify);
        btnGetVerify.setOnClickListener(this);

        etPhone = (EditText) findViewById(R.id.et_mobile_verify_phone);
        etphoneVerify = (EditText) findViewById(R.id.et_mobile_verify_phoneverify);
        findViewById(R.id.tv_mobile_verify_agree).setOnClickListener(this);
        checkbox = (CheckBox) findViewById(R.id.cb_my_bank_card_add_agree);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_mobile_verify_getverify:
                getVerify();
                break;
            case R.id.btn_mobile_verify_next_step:
                nextStep(); // 下一步
                break;
            case R.id.tv_mobile_verify_agree:
                goProtocol();
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }

    }

    private void goProtocol() {
        Intent it = new Intent(MobileVerifyActivity.this, ProtocolActivity.class);
        startActivity(it);
    }

    private void getVerify() {
        mobile = etPhone.getText().toString();

        userName = etPhone.getText().toString();
        if (userName == null || (userName != null && userName.equals(""))) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        } else if (!ExpresssoinValidateUtil.isMobilePhone(userName)) {
            T.ss("手机号码有误");
            return;
        }

        getVerifyCode();

    }

    String mobileVerify;

    private void nextStep() {
        if (!hasSend) {
            T.ss("请获取验证码后操作");
        }
        mobileVerify = etphoneVerify.getText().toString();
        if (mobileVerify == null
                || (mobileVerify != null && mobileVerify.equals(""))) {
            Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show();
            return;
        } else if (mobileVerify.length() < 6) {
            T.ss("验证码最少为6位");
            return;
        }
        if (checkbox.isChecked()) {
            checkVerifyCode();
        } else {
            Toast.makeText(this, "请先同意服务协议", Toast.LENGTH_SHORT).show();
        }


    }

    private void gotoRegister() {
        Intent it = new Intent(MobileVerifyActivity.this,
                RegisterActivity.class);
        it.putExtra("userName", userName);
        startActivity(it);

    }

    private String mobile;

    private void getVerifyCode() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ParamsUtils.CUST_MOBILE, etPhone.getText().toString().trim());
        if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
            map.put(ParamsUtils.CODE_TYPE, "02");
            map.put(ParamsUtils.CUST_ID, User.uId);
        } else if (action.equals(ACTION_FORGET_PAY_PWD)) {
            map.put(ParamsUtils.CODE_TYPE, "03");
            map.put(ParamsUtils.CUST_ID, User.uId);
        } else {

            map.put(ParamsUtils.CODE_TYPE, "01");
        }
        MyHttpClient.postWithoutDefault(this, Urls.GET_VERIFY, map,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        hasSend = true;
                        Logger.json(new String(responseBody));
                        try {
                            BasicResponse response = new BasicResponse(
                                    responseBody).getResult();
                            if (response.isSuccess()) {
                                btnGetVerify.setText("已发送");
                                ;
                                sms = new SmsCodeCount(60000, 1000);
                                sms.start();
                                T.ss("已发送");

                            } else {
                                T.ss(response.getMsg());
                                btnGetVerify.setText("重新发送");
                                btnGetVerify.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                        btnGetVerify.setText("重新发送");
                        btnGetVerify.setEnabled(true);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        btnGetVerify.setText("发送中");
                        ;
                        btnGetVerify.setEnabled(false);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void checkVerifyCode() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("custMobile", etPhone.getText().toString().trim());
        map.put("msgCode", etphoneVerify.getText().toString());
        if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
            map.put("codeType", "02");
            map.put("custId", User.uId);
        } else if (action.equals(ACTION_FORGET_PAY_PWD)) {
            map.put("codeType", "03");
            map.put("custId", User.uId);
        } else {
            map.put("codeType", "01");
        }
        MyHttpClient.postWithoutDefault(this, Urls.CHECK_VERIFY, map,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Logger.json(response.toString());
                        try {
                            JSONObject obj = response.getJSONObject("REP_BODY");
                            if (obj.getString(
                                    Entity.RSP_COD).equals(
                                    Entity.RSP_SUCC)) {
                                gotoNext();
                                finish();
                            } else {
                                T.sl("" + obj.optString("RSPMSG"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        networkError(statusCode, throwable);
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

    private void gotoNext() {
        if (action.equals(ACTION_REGISTER)) {
            startActivity(new Intent(this, RegisterActivity.class).putExtra( "mobile", mobile));
        } else if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
            startActivity(new Intent(this, FindPwdActivity.class)
                    .setAction(ACTION_FORGET_LOGIN_PWD)
                    .putExtra("code", mobileVerify).putExtra("mobile", mobile));
        } else if (action.equals(ACTION_FORGET_PAY_PWD)) {
            startActivity(new Intent(this, FindPwdActivity.class)
                    .setAction(ACTION_FORGET_PAY_PWD)
                    .putExtra("code", mobileVerify).putExtra("mobile", mobile));
        }
    }

    private SmsCodeCount sms;

    /**
     * @ClassName: SmsCodeCount
     * @Description: 定义一个倒计时的内部类
     */
    class SmsCodeCount extends CountDownTimer {

        /**
         * Title:SmsCodeCount Description: 倒计时
         *
         * @param millisInFuture
         * @param countDownInterval
         */
        public SmsCodeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnGetVerify.setText(getString(R.string.get_again));
            btnGetVerify.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnGetVerify.setText(millisUntilFinished / 1000
                    + getString(R.string.resume));
            // btnGetVerify.setBackgroundColor(Color
            // .parseColor(getString(R.color.btn_bg_grey)));
            btnGetVerify.setEnabled(false);
        }
    }
}
