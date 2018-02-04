package com.lk.td.pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hx.view.widget.CustomListView;
import com.hx.view.widget.CustomListView.OnRefreshListener;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.adapter.NoticeMsgAdapter;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.NoticeBean;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NoticeActivity extends BaseActivity {
    CustomListView lv;
    private NoticeMsgAdapter adapter;

    private Button btn_back;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initView();
        loadData();
        Logger.init().setMethodCount(0).hideThreadInfo();
    }

    ArrayList<NoticeBean> adaVal = new ArrayList<NoticeBean>();

    private void loadData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pageSize", 10 + "");
        params.put("noticeStatus", "2"); //全部公告
        params.put("start", "0");
        MyHttpClient.post(this, Urls.SYSTEM_MESSAGE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        String response = new String(responseBody);
                        Logger.json(response);

                        try {
                            BasicResponse result = new BasicResponse(responseBody).getResult();
                            if (result.isSuccess()) {
                                JSONObject jsonOneBody = result.getJsonBody();
                                JSONArray array = jsonOneBody.getJSONArray("noticeList");
                                if (adaVal.size() > 0) {
                                    adaVal.clear();
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject temp = array.getJSONObject(i);
                                    adaVal.add(new NoticeBean(temp
                                            .optString("noticeTitle"), temp
                                            .optString("noticeBody"), temp
                                            .optString("noticeId"), temp.optString("createDate")));

                                }
                                if (adaVal.size() == 0) {
                                    findViewById(R.id.empty_notice).setVisibility(View.VISIBLE);
                                }
                                if (null == adapter) {
                                    adapter = new NoticeMsgAdapter(
                                            NoticeActivity.this, adaVal);
                                    lv.setAdapter(adapter);
                                } else {
                                    adapter.refreshValues(adaVal);
                                    adapter.notifyDataSetChanged();
                                    lv.onRefreshComplete();
                                    System.out.println("refresh--ok");
                                }
                            } else {
                                showToast(result.getMsg());
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
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("消息中心");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv = findView(R.id.lv_notice);
        lv.setCanLoadMore(false);
        lv.setCanRefresh(true);
        lv.setOnItemClickListener(onitemClick);
        lv.setOnRefreshListener(onRefrsh);
    }

    OnRefreshListener onRefrsh = new OnRefreshListener() {

        @Override
        public void onRefresh() {

            loadData();
            System.out.println("onrefresh--------------------------");

        }
    };

    OnItemClickListener onitemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position < 0) {
                return;
            }
            startActivity(new Intent(NoticeActivity.this,
                    NoticeDetailActivity.class).putExtra("data",
                    adaVal.get(position - 1)));
        }
    };

    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyHttpClient.cancleRequest(this);
    }
}
