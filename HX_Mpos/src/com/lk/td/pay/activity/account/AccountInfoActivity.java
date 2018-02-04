package com.lk.td.pay.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;

import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BankCardItem;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.request.ParamsUtils;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.AmountUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AccountInfoActivity extends BaseActivity {

    private Button mBtn_back;
    private TextView mTv_info_account;
    private TextView mTv_info_name;
    private TextView bankNameText;
    private TextView mTv_info_bcno;
    private TextView tv_balance_1,tv_balance_2,tv_balance_3, tv_agent_Id, tv_scan_code, tv_quick;
    private List<BankCardItem> bankList = new ArrayList<BankCardItem>();
    private Context mContext;
    private TextView tv_account_amount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);
        bindViews();
    }

    private void bindViews() {
        mContext = this;
        tv_balance_1 = (TextView) findViewById(R.id.tv_balance_total_1);
        tv_balance_2 = (TextView) findViewById(R.id.tv_balance_total_2);
        tv_balance_3 = (TextView) findViewById(R.id.tv_balance_total_3);
        tv_agent_Id = (TextView) findViewById(R.id.tv_agent_Id);
        tv_scan_code = (TextView) findViewById(R.id.tv_scan_code);
        tv_quick = (TextView) findViewById(R.id.tv_quick);
        tv_account_amount = (TextView) findViewById(R.id.tv_account_amount);
        mBtn_back = (Button) findViewById(R.id.btn_back);
        mBtn_back.setText("我的账户");
        mBtn_back.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_title).setVisibility(View.GONE);
        mTv_info_account = (TextView) findViewById(R.id.tv_info_account);
        mTv_info_name = (TextView) findViewById(R.id.tv_info_name);
        bankNameText = (TextView) findViewById(R.id.tv_info_bank_name);
        mTv_info_bcno = (TextView) findViewById(R.id.tv_info_bcno);
        mTv_info_account.setText(Utils.hiddenMobile(User.uAccount));

        mTv_info_name.setText(toS(User.uName));
        tv_agent_Id.setText(User.uId);
        mBtn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.accnount_info_confirm_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        getBankCardStatus();
        queryBalance();
    }

    private void getBankCardStatus() {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        MyHttpClient.post(AccountInfoActivity.this, Urls.GET_BANKCARD_LIST, requestMap,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[BankCardList]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                JSONArray array = r.getJsonBody().optJSONArray(
                                        "bankCardList");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject temp = array.getJSONObject(i);
                                    BankCardItem item = new BankCardItem();
                                    item.setCardName(temp.optString("issnam"));
                                    item.setCardNo(temp.optString("cardNo"));
                                    bankList.add(item);
                                }
                                if (bankList.size() > 0) {
                                    bankNameText.setText(bankList.get(0).getCardName());
                                    mTv_info_bcno.setText(Utils.hiddenCardNo(bankList.get(0).getCardNo()));
                                } else {
                                    bankNameText.setText("--");
                                    mTv_info_bcno.setText("--");
                                }

                            } else {
                                T.ss(r.getMsg());
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
//						dismissLoadingDialog();
                    }
                });

    }

    private void queryBalance() {

        MyHttpClient.post(this, Urls.QUERY_BALANCE,
                new HashMap<String, String>(), new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[余额查询]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                JSONObject obj = r.getJsonBody();
                                User.amtT0 = AmountUtils.changeFen2Yuan(obj
                                        .optString("acT0"));
                                User.amtT1 = AmountUtils.changeFen2Yuan(obj
                                        .optString("acT1"));
                                User.amtT1y = AmountUtils.changeFen2Yuan(obj
                                        .optString("acT1Y"));
                                User.totalAmt = AmountUtils.changeFen2Yuan(obj
                                        .optString("acBal"));
                                User.acT1UNA = AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1_UNA)));
                                User.acT1AUNP = AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1_AUNP)));
                                User.acT1AP = AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1_AP)));
                                User.acT1AP_ACT03 =AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT03)));
                                User.acT1AP_ACT04 =AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT04)));
                                User.acT1Y_ACT03 =AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1Y_ACT03)));
                                User.acT1Y_ACT04 =AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1Y_ACT04)));
                                tv_balance_1.setText(User.acT1UNA + "元" );
                                tv_balance_2.setText(User.acT1AP + "元");
                                tv_balance_3.setText(User.acT1AUNP + "元");
                                tv_quick.setText(User.acT1AP_ACT03 +"元");
                                tv_scan_code.setText(User.acT1AP_ACT04+"元");
                                tv_account_amount.setText((Double.parseDouble(User.acT1AP)
                                		+Double.parseDouble(User.acT1AP_ACT03)
                                		+Double.parseDouble(User.acT1AP_ACT04))+"元");
//                                tv_balance_3.setText();
                            } else {
                                T.ss(r.getMsg());
                            }
                        } catch (JSONException e) {
                            // TODO 自动生成的 catch 块
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
                        //showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }

                });

    }

    private String sub(String s) {
        if (null == s)
            return "";
        if (s.length() > 4) {
            return s.substring(s.length() - 4, s.length());
        } else {
            return "";
        }
    }

    private String toS(String s) {
        if (null == s)
            return "";
        return s;
    }
}
