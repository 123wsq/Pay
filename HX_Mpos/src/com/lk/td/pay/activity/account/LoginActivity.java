package com.lk.td.pay.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lk.td.pay.activity.MainTabActivity;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.AppManager;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.LogLevel;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.store.SharedPrefConstant;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private EditText usernameEdit, userPwdEdit;
    private String username, userPwd;
    private TextView forgetPwdText;
    private CheckBox rememberCb;
    private String action;
    private Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.activity_login);
        mContext = this;

        init();
//		checkUpdate();
    }

    private void init() {
        Logger.init().hideThreadInfo().setLogLevel(LogLevel.FULL);
        action = getIntent().getAction();
        usernameEdit = (EditText) this
                .findViewById(R.id.et_login_username);
        usernameEdit.setText(MApplication.mSharedPref.getSharePrefString(
                SharedPrefConstant.USER_ACCOUNT, null));
        userPwdEdit = (EditText) this.findViewById(R.id.et_login_pwd);
        rememberCb = (CheckBox) findViewById(R.id.remember_account_cb);

        ((TextView)findViewById(R.id.tv_title)).setText(getString(R.string.app_name));
        
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.login_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                login();
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(LoginActivity.this,
                        MobileVerifyActivity.class)
                        .setAction(MobileVerifyActivity.ACTION_FORGET_LOGIN_PWD));
                break;
            case R.id.login_register:
                startActivity(new Intent(LoginActivity.this,
                        MobileVerifyActivity.class)
                        .setAction(MobileVerifyActivity.ACTION_REGISTER));
                break;
            default:
                break;
        }

    }

    private void login() {
        username = usernameEdit.getText().toString().trim();
        userPwd = userPwdEdit.getText().toString().trim();

        if (TextUtils.isEmpty(username)
                && userPwd.equals(Urls.BACKGROUND_KEY)) {
            Intent intent = new Intent(LoginActivity.this,
                    LoginServerChooseActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            T.ss(R.string.username_null);
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            T.ss(R.string.password_null);
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("custMobile", username);
        params.put("custPwd", userPwd);

        MyHttpClient.postWithoutDefault(this, Urls.LOGIN, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        String content = new String(responseBody);
                        Logger.json(content);
                        try {
                            JSONObject obj = new JSONObject(content)
                                    .getJSONObject("REP_BODY");
                            
                            if (obj.getString("RSPCOD").equals("000000")) {
                                try {
                                    User.uName = obj.optString("custName");
                                    User.uAccount = obj.optString("custLogin");
                                    User.uId = obj.optString("custId");
                                    User.uStatus = Integer.valueOf(obj.optString("authStatus"));
                                    String temp = new JSONObject(content)
                                            .getJSONObject("REP_HEAD").getString(
                                                    "SIGN");
                                    MApplication.mSharedPref.putSharePrefString(
                                            "key", temp);
                                    MApplication.mSharedPref.putSharePrefString(
                                            "custid", User.uId);
                                    MApplication.mSharedPref.putSharePrefString(
                                            "custmobile", User.uAccount);
                                    User.sign = temp;
                                    User.login = true;
                                    if (rememberCb.isChecked()) {
                                        MApplication.mSharedPref
                                                .putSharePrefString(
                                                        SharedPrefConstant.USER_ACCOUNT,
                                                        User.uAccount);

                                    } else {
                                        MApplication.mSharedPref
                                                .putSharePrefString(
                                                        SharedPrefConstant.USER_ACCOUNT,
                                                        null);

                                    }
                                } catch (JSONException e) {
                                    showDialog(getString(R.string.data_parse_exception));
                                    return;
                                }

                                startActivity(new Intent(LoginActivity.this,
                                        MainTabActivity.class));
                                finish();
                            } else {
                                showDialog(obj.optString("RSPMSG"));

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getAppManager().AppExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyHttpClient.cancleRequest(this);
    }

}
