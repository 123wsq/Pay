package com.lk.td.pay.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private EditText etUserPwd, etUserPwdAgain, etRefer;
    private TextView etprovince, etcity;
    private String mobile;

    // public LocationClient mLocationClient = null;
    // public BDLocationListener myListener = new MyLocationListener();
    private String city;
    private Context ctx;
    private TextView tv_title;
    private Button btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ctx = this;
        mobile = getIntent().getExtras().getString("mobile");
        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        etUserPwd = (EditText) findViewById(R.id.et_register_user_pwd);
        etUserPwdAgain = (EditText) findViewById(R.id.et_register_user_pwd_again);
        findViewById(R.id.btn_register_confirm).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        register();
                    }
                });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("注册");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int arg0, int resultCode, Intent data) {
        super.onActivityResult(arg0, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();

            Cursor c = managedQuery(contactData, null, null, null, null);
            c.moveToFirst();

            String phoneNum = getContactPhone(this, c);
            //etRefer.setText(phoneNum);

        }
    }

    private void register() {
        String userPasswd = etUserPwd.getText().toString().trim();
        if (userPasswd == null || (userPasswd != null && userPasswd.equals(""))) {
            Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String userPasswdAgain = etUserPwdAgain.getText().toString().trim();
        if (!userPasswd.equals(userPasswdAgain)) {
            Toast.makeText(this, "登录密码二次输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("custPwd", userPasswd);
        // params.put("custName", name);
        // params.put("certificateNo", identifyNo);
        params.put("custMobile", mobile);

        //params.put("referrer", refer);
        MyHttpClient.postWithoutDefault(this, Urls.REGISTER, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(new String(responseBody));
                        try {
                            BasicResponse response = new BasicResponse(
                                    responseBody).getResult();
                            if (response.isSuccess()) {
                                showToast(response.getMsg());
                                startActivity(new Intent(ctx,
                                        LoginActivity.class));
                            } else {
                                showDialog(response.getMsg());
//								T.sl(response.getMsg());
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
                        showLoadingDialogCannotCancle(null);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });

    }

    public void sh(Context ctx, String msg) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.network_error_popwindow, null);
        final PopupWindow pop = new PopupWindow(view,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        Button btn = (Button) view.findViewById(R.id.btn_retry_pop);
        if (!TextUtils.isEmpty(msg)) {
            ((TextView) (view.findViewById(R.id.tv_error_pop))).setText(msg);
        }
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                pop.showAsDropDown(v);

            }
        });
    }

    private void gotoLogin() {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }



    @Override
    public void onClick(View v) {


    }

    /**
     * 获取联系人电话号码
     *
     * @param mContext
     * @param cursor
     * @return
     */
    public static String getContactPhone(Context mContext, Cursor cursor) {

        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String phoneResult = "";

        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = mContext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);

            if (phones.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !phones.isAfterLast(); phones.moveToNext()) {
                    int index = phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phones.getInt(typeindex);
                    String phoneNumber = phones.getString(index);

                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;

                    switch (phone_type) {
                        case 2:
                            phoneResult = phoneNumber;
                            break;
                    }

                }
                if (!phones.isClosed()) {
                    phones.close();
                }
            }
        }
        return phoneResult;
    }
}
