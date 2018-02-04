package com.lk.td.pay.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.BankScanCamera;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.CashInBean;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.beans.TradeBean;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.AppConfig;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;

import java.util.HashMap;

public class SalesSlipActivity extends BaseActivity {

    private ImageView signImage,iv_path;
    private int height, width;
    private TradeBean detail;
    private TextView merchantNameText, merchantNoText, ordIdText, termIdText,
            cardNoText, ordDateText, ordAmtText,slip_pay_type;
    private String userAccount, userName;

    private RelativeLayout rl_trade_details_title;
    private LinearLayout rl_trade_details;
    private String recordType;
    private TextView tv_title;
    private Button btn_back;
    private Button btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tradesales_slip);
        detail = (TradeBean) getIntent().getSerializableExtra("data");
        recordType = getIntent().getStringExtra("recordType");
        init();
        loadData();
    }

    private void loadData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("busType", detail.getBusType());
        params.put("ordno", detail.getPrdNo());
        MyHttpClient.post(this, "TR0002.json", params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[CashinDetails]", responseBody);
                        if (responseBody != null) {
                            try {
                                BasicResponse r = new BasicResponse(
                                        responseBody).getResult();
                                if (r.isSuccess()) {
                                    CashInBean bean = new Gson().fromJson(r
                                                    .getJsonBody().toString(),
                                            CashInBean.class);
                                    //1:成功
                                    if (bean.getOrdstatus().equals("01")){
                                        rl_trade_details.setVisibility(View.GONE);
                                        rl_trade_details_title.setVisibility(View.GONE);
                                        iv_path.setVisibility(View.VISIBLE);
                                    } else {
                                        rl_trade_details.setVisibility(View.VISIBLE);
                                        rl_trade_details_title.setVisibility(View.VISIBLE);
                                        iv_path.setVisibility(View.GONE);
                                    }
                                    ordIdText.setText(detail.getPrdNo() + "");
                                    merchantNoText.setText(bean.getCustId() + "");
                                    termIdText.setText(bean.getTermNo() + "");
                                    cardNoText.setText(Utils.hiddenCardNo(bean.getPayCardNo()));
                                    merchantNameText.setText(bean.getCustName() + "");
                                    slip_pay_type.setText(AppConfig.PAYTYPE.getValue(bean.getPayType()));
                                    if (detail.getBusType().equals(AppConfig.BUSTYPE.BUS_TYPE_01)
                                    		&& detail.getPayWay().equals(AppConfig.PAYTYPE.QR_CODE)) {
                                    	
                                    	  rl_trade_details.setVisibility(View.VISIBLE);
                                          rl_trade_details_title.setVisibility(View.VISIBLE);
                                          iv_path.setVisibility(View.GONE);
									}else{
										ImageLoader.getInstance().displayImage(AppConfig.HOST + bean.getFjpath(), iv_path, options);
									}
                                } else {
                                    showDialog(r.getMsg());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                        showLoadingDialog("查询中...");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
//                        btn_reset.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void init() {

        merchantNameText = (TextView) findViewById(R.id.slip_merchant_name);
        merchantNoText = (TextView) findViewById(R.id.slip_merchant_no);
        ordIdText = (TextView) findViewById(R.id.slip_ord_id);
        termIdText = (TextView) findViewById(R.id.slip_term_no);
        cardNoText = (TextView) findViewById(R.id.slip_card_no);
        ordDateText = (TextView) findViewById(R.id.slip_ord_date);
        ordAmtText = (TextView) findViewById(R.id.slip_ord_amt);
        slip_pay_type = (TextView) findViewById(R.id.slip_pay_type);
//		merchantNameText.setText(User.uName);
//		merchantNoText.setText(User.uAccount);
        ordIdText.setText(tos(detail.getId()));
        termIdText.setText(tos(detail.getTerNo()));
        cardNoText.setText(Utils.hiddenCardNo(detail.getBankCardNo()));
        ordDateText.setText(detail.getTarnTime());
        ordAmtText.setText(tos(detail.getTranAmt()) + "元");
        slip_pay_type.setText(AppConfig.PAYTYPE.getValue(detail.getPayWay()));
        signImage = (ImageView) findViewById(R.id.sales_slip_image);
        iv_path = (ImageView) findViewById(R.id.iv_path);
        rl_trade_details_title = (RelativeLayout) findViewById(R.id.rl_trade_details_title);
        rl_trade_details= (LinearLayout) findViewById(R.id.rl_trade_details);

        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        
        btn_reset.setText("");
        tv_title.setText("订单详情");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (!recordType.isEmpty()) {
            if (recordType.length() != 0) {
                if (recordType.equals("2")) {
                    btn_reset.setText("拍照");

                    btn_reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PosData.getPosData().setPrdordNo(detail.getPrdNo());
                            startActivity(new Intent(SalesSlipActivity.this, BankScanCamera.class).putExtra("TYPE", "2"));
                        }
                    });
                }
            }
        }
    }

    private String tos(String str) {
        if (null == str)
            return "";
        return str;
    }
}
