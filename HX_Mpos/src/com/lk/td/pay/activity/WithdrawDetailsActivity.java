package com.lk.td.pay.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.TradeBean;
import com.lk.td.pay.beans.WithDrawBean;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.AppConfig;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.AmountUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WithdrawDetailsActivity extends BaseActivity {

    private TextView tvOrderStatus;
    private String userName;
    TradeBean bean;
    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    private ListView lv;

    private Button btn_back;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal_details);
        String prdOrdNo = getIntent().getStringExtra("prdOrdNo");
        tvOrderStatus = (TextView) findViewById(R.id.tv_withdraw_detail_orderStatus);
        lv = (ListView) findViewById(R.id.lv_withdraw);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提现详情");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bean = (TradeBean) getIntent().getSerializableExtra("data");

        loadData();
    }

    private String keys[] = new String[]{"流水号:", "提现类型:", "订单时间:", "完成时间:",
            "订单状态:", "商户编号:", "商户名称:", "提现金额:", "手续费:", "实际到账金额:", "提现银行卡:"};
    private String vals[] = new String[12];

    private void loadData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("busType", bean.getBusType());
        params.put("ordno", bean.getPrdNo());
        MyHttpClient.post(this, "TR0002.json", params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[WithdrawDetails]", responseBody);
                        if (responseBody != null) {
                            try {
                                BasicResponse r = new BasicResponse(
                                        responseBody).getResult();
                                if (r.isSuccess()) {
                                    WithDrawBean bean = new Gson().fromJson(r
                                                    .getJsonBody().toString(),
                                            WithDrawBean.class);
                                    System.out.println(bean.getCustName());
                                    String temp = "";

//                                    if (bean.getOrdstatus().equals("00")) {
//                                        temp = "未交易";
//                                    } else if (bean.getOrdstatus().equals("01")) {
//                                        temp = "交易中";
//                                    } else if (bean.getOrdstatus().equals("02")) {
//                                        temp = "交易失败";
//                                    } else if (bean.getOrdstatus().equals("03")) {
//                                        temp = "可疑";
//                                    } else if (bean.getOrdstatus().equals("04")) {
//                                        temp = "审核中";
//                                    } else if (bean.getOrdstatus().equals("05")) {
//                                        temp = "审核拒绝";
//                                    } else if (bean.getOrdstatus().equals("06")) {
//                                        temp = "交易中";
//                                    } else if (bean.getOrdstatus().equals("07")) {
//                                        temp = "交易成功";
//                                    } else if (bean.getOrdstatus().equals("08")) {
//                                        temp = "交易中";
//                                    } else {
//                                        temp = "--";
//                                    }
                                    temp =AppConfig.ORDSTATUS.getContent(bean.getOrdstatus());
                                    tvOrderStatus.setText(temp + "");
                                    vals[0] = toS(bean.getCasordno());
                                    if ("1".equals(bean.getCasType())) {
                                        vals[1] = "T0";
                                    } else if ("2".equals(bean.getCasType())) {
                                        vals[1] = "T1";
                                    } else if ("3".equals(bean.getCasType())) {
                                        vals[1] = "T0+T1";
                                    } else {
                                        vals[1] = "--";
                                    }
                                    vals[2] = toD(bean.getCasDate());
                                    vals[3] = toD(bean.getSucDate());
                                    vals[4] = temp;
                                    vals[5] = toS(bean.getCustId());
                                    vals[6] = toS(bean.getCustName());
                                    vals[7] = AmountUtils.changeFen2Yuan(bean.getOrdamt()) + " 元";
                                    vals[8] = AmountUtils.changeFen2Yuan(bean.getFee()) + " 元";
                                    vals[9] = AmountUtils.changeFen2Yuan(bean.getNetrecAmt()) + " 元";
                                    vals[10] = Utils.hiddenCardNo(bean.getCardno());
                                    for (int i = 0; i < 11; i++) {
                                        HashMap<String, String> obj = new HashMap<String, String>();
                                        obj.put("key", keys[i]);
                                        obj.put("val", vals[i]);
                                        data.add(obj);
                                    }

                                    SimpleAdapter adapter = new SimpleAdapter(
                                            WithdrawDetailsActivity.this, data,
                                            R.layout.item_normal_layout,
                                            new String[]{"key", "val"},
                                            new int[]{R.id.item_sinfo_name,
                                                    R.id.item_sinfo_val});
                                    lv.setAdapter(adapter);
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
                    }
                });
    }

    private String toS(Object str) {
        if (null == str)
            return "--";
        if (str.toString().contains("null")) {
            return str.toString().replace("null", "");
        }
        return str.toString();
    }

    private String toD(String str) {
        if (TextUtils.isEmpty(str)) {
            return "--";
        }
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date temp = d.parse(str);
            return new SimpleDateFormat("yy年MM月dd日  HH:mm:ss").format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
            return "--";
        }
    }
}
