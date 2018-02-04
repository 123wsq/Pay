package com.lk.td.pay.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hx.view.bean.PopupItem;
import com.hx.view.widget.CustomListView;
import com.hx.view.widget.CustomListView.OnLoadMoreListener;
import com.hx.view.widget.CustomListView.OnRefreshListener;
import com.hx.view.widget.CustomPopupWindow;
import com.lk.td.pay.activity.WithdrawDetailsActivity;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.adapter.DealRecordAdapter;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.TradeBean;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.request.ParamsUtils;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.AmountUtils;
import com.pay.library.uils.StringUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TradeListActivity extends BaseActivity implements OnClickListener {

    private Context ctx;
    private CustomListView listview;
    private DealRecordAdapter adapter;
    private int currentPage = 0;
    private HashMap<String, String> params = null;
    private ArrayList<TradeBean> adaValues = new ArrayList<TradeBean>();
    private final String ACTION_LOADMORE = "-1";
    private final int PAGE_SIZE = 20;
    private String ACTION_REFRESH = "00";
    private String recordType = "1";

    private static final int SELECT_TIME = 1;
    private String START_TIME = "";
    private String END_TIME = "";

    private Button btn_back;
    private TextView tv_title;
    private TextView tv_filter;
    private List<PopupItem> popupData;

    private PopupWindow pop;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal_record_refresh);
        ctx = this;
        initView();
        getPopupData();
        recordType = getIntent().getStringExtra("RECORD_TYPE");
        type= AppConfig.BUSTYPE.BUS_TYPE_00;
        initData(ACTION_REFRESH, 0, type, recordType, START_TIME, END_TIME);
//        check = getResources().getDrawable(R.drawable.ok32);
//        check.setBounds(0, 0, check.getMinimumWidth(), check.getMinimumHeight());
    }


    private void initData(final String action, int start, String type, String recordType, String startTime, String endTime) {
        params = new HashMap<>();
        // params.put("prdOrdNo", "");
        params.put(ParamsUtils.PAGE_SIZE, "20");
        params.put(ParamsUtils.START, start + "");
        params.put(ParamsUtils.START_TIME, startTime);
        params.put(ParamsUtils.END_TIME, endTime);
        params.put(ParamsUtils.BUS_TYPE, type);
        params.put(ParamsUtils.RECORD_TYPE, recordType);
        params.put(ParamsUtils.PAW_WAY, "");

        MyHttpClient.post(ctx, Urls.TRADE_RECORDS, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[交易记录====]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                JSONArray array = r.getJsonBody().getJSONArray(
                                        "tranList");
                                if (action.equals(ACTION_REFRESH)) {// 如果是刷新操作
                                    if (adaValues.size() > 0) {
                                        adaValues.clear();
                                    }
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    TradeBean bean = new TradeBean();
                                    bean.setBusType(obj.optString(ParamsUtils.RESULT_ORD_TYPE));
                                    bean.setPrdNo(obj.optString(ParamsUtils.RESULT_ORD_NO));
                                    bean.setAgentId(obj.optString(ParamsUtils.CUST_ID));
                                    bean.setTarnTime(datePaser(obj.optString(ParamsUtils.RESULT_ORD_TIME)));
                                    // bean.setTarnMonth(obj.optString("tranMonth"));
                                    bean.setTranState(obj.optString(ParamsUtils.RESULT_ORD_STATUS));
                                    bean.setTranAmt(AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ORD_AMT)));
                                    bean.setBankCardNo(StringUtils.hideString(obj.optString("PAY_CARDNO"), 3, 1));
                                    
                                    // bean.setTerNo(obj.optString("TER_NO"));
                                    // bean.setTranDay(obj.optString("tranDay"));
                                    // bean.setId(obj.optString("tranCode"));
                                   
                                    if (AppConfig.BUSTYPE.BUS_TYPE_01.trim().equals(bean.getBusType())) {
                                    	bean.setPayWay(obj.optString(ParamsUtils.PAW_WAY));
									}else if(AppConfig.BUSTYPE.BUS_TYPE_03.trim().equals(bean.getBusType())){
										bean.setAcctType(obj.optString(ParamsUtils.ACC_TYPE));
									}
                                    
                                    adaValues.add(bean);
                                }

                                if (null == adapter) {
                                    adapter = new DealRecordAdapter(ctx,
                                            adaValues);
                                    listview.setAdapter(adapter);
                                } else {
                                    adapter.refreshValues(adaValues);
                                    adapter.notifyDataSetChanged();
                                }
                                if (array.length() == 0) {
                                    T.ss("暂无交易记录");
                                    listview.setCanLoadMore(false);
                                    //adapter.notifyDataSetChanged();
                                    listview.hideFooterView();
                                }
                                if (action.equals(ACTION_REFRESH)) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    if (array.length() < PAGE_SIZE) {
                                        listview.setCanLoadMore(false);
                                        //adapter.notifyDataSetChanged();
                                        listview.hideFooterView();
                                    }
                                    handler.sendEmptyMessage(2);
                                }
                            } else {
                                T.ss(r.getMsg());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                    }
                });

    }

    private String datePaser(String str) {
        if (null == str)
            return "--";
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = d.parse(str);
            SimpleDateFormat temp = new SimpleDateFormat("MM月dd日 HH:mm:ss");
            return temp.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    OnRefreshListener onrefresh = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            listview.setCanLoadMore(true);
            currentPage = 0;
            initData(ACTION_REFRESH, 0, type, recordType, START_TIME, END_TIME);
            // handler.sendEmptyMessage(1);
        }
    };
    OnLoadMoreListener onloadmore = new OnLoadMoreListener() {

        @Override
        public void onLoadMore() {
            currentPage++;
            initData(ACTION_LOADMORE, currentPage * PAGE_SIZE, type, recordType, START_TIME, END_TIME);
            // handler.sendEmptyMessage(2);

        }
    };

    private void initView() {
        popupData =new ArrayList<PopupItem>();
        listview = (CustomListView) findViewById(R.id.listview_reade_records);
        listview.setCanRefresh(true);
        listview.setCanLoadMore(true);
        listview.setOnRefreshListener(onrefresh);
        listview.setOnLoadListener(onloadmore);
        listview.setOnItemClickListener(onItemClick);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("交易记录");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        tv_filter = (TextView) findViewById(R.id.tv_filter);
        tv_filter.setVisibility(View.VISIBLE);
        tv_filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == pop) {
                    initPopwindow();
                }
                pop.showAtLocation(findViewById(R.id.deal_layout_record), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    listview.onRefreshComplete();
                    break;
                case 2:
                    listview.onLoadMoreComplete();
                    break;
            }
        }

    };

    protected void onDestroy() {
        super.onDestroy();
    }

    ;

    OnItemClickListener onItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position < 0)
                return;
            goDetail(position);
        }
    };

    private void goDetail(int arg2) {

        TradeBean temp = adaValues.get(arg2 - 1);
        if (temp.getBusType().equals("01") || temp.getBusType().equals("02")) {
            if (temp.getTranState().equals("01")) {
                //消费
                startActivity(new Intent(this, SalesSlipActivity.class).putExtra(
                        "data", temp)
                        .putExtra("recordType", recordType)
                );
            } else if (temp.getTranState().equals("00")) {
                showToast("该订单未完成支付!");
            }
        } else {
            //提现
            startActivity(new Intent(this, WithdrawDetailsActivity.class)
                    .putExtra("data", temp));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case  R.id.btn_back:
                finish();
                break;
        }

    }

    public void initPopwindow() {

        pop = new CustomPopupWindow(TradeListActivity.this, popupData,"",  new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               PopupItem p =  popupData.get(position);
                type = "0"+p.getId();

                for (int i = 0, len = popupData.size(); i <len; i++){
                    if (p.getId()==popupData.get(i).getId()){
                        popupData.get(i).setState(true);
                    }else{
                        popupData.get(i).setState(false);
                    }
                }

                if (position !=4){
                    initData(ACTION_REFRESH, 0, type, recordType, START_TIME, END_TIME);
                }else{
                    type = AppConfig.BUSTYPE.BUS_TYPE_00;
                    startActivityForResult(new Intent(TradeListActivity.this, SelectTimeActivity.class), SELECT_TIME);
                }
                pop.dismiss();
            }
        });

    }


    private void getPopupData(){
        try {
            InputStream is =  this.getResources().getAssets().open(AppConfig.DATA.POPUP_TRAN_DATA);
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isReader);
            String content = "";
            String line ="";
            while ((line = bufferedReader.readLine()) != null){
                content += line;
            }

            JSONObject jsonObject = new JSONObject(content);

            JSONArray jsona = jsonObject.getJSONArray("data");

            for (int i = 0 , len = jsona.length(); i< len; i++){
                JSONObject json = jsona.optJSONObject(i);
                PopupItem p = new PopupItem();
                p.setId(json.optInt("id", -1));
                p.setName(json.optString("name"));
                if (i==0){
                    p.setState(true);
                }else{
                    p.setState(false);
                }
                popupData.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_TIME && resultCode == RESULT_OK) {
//            START_TIME = data.getStringExtra("START_TIME");
            String mStart = data.getStringExtra("START_TIME");
//            END_TIME = data.getStringExtra("END_TIME");
            String mEnd = data.getStringExtra("END_TIME");
            initData(ACTION_REFRESH, 0, type, recordType, mStart, mEnd);
        }
    }
}
