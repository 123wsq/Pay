package com.lk.td.pay.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hx.view.wedget.calendar.CalendarActivity;
import com.hx.view.widget.CustomListView;
import com.hx.view.widget.CustomListView.OnLoadMoreListener;
import com.hx.view.widget.CustomListView.OnRefreshListener;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.adapter.ProfitAdapter;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.ProfitBean;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.AmountUtils;
import com.td.app.pay.hx.R;

public class ProfitQueryActivity extends BaseActivity {

	private ArrayList<ProfitBean> val=new ArrayList<ProfitBean>();
	private CustomListView listview;
	private ProfitAdapter adapter;
	private final int PAGESIZE=10;
	private int currentPage=1;
	private Button btn_back, btn_reset;
	private TextView tv_title;

	public ProfitQueryActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profit_query);
		initView();
		loadData(0,0);
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("商户分润");

		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_reset.setVisibility(View.VISIBLE);
		btn_reset.setText("日期");
		btn_reset.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
            startActivityForResult(new Intent(ProfitQueryActivity.this,CalendarActivity.class).setAction(CalendarActivity.ACTION_PICKET_TWO_DAYS), 3);   			
		}
	});
      listview=(CustomListView) findViewById(R.id.listview_profit_query);
      listview.setCanRefresh(true);
      listview.setCanLoadMore(true);
      listview.setOnRefreshListener(onRefresh);
      listview.setOnLoadListener(onLoadMore);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void loadData(final int type,int start) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("start", start+"");
		params.put("pageSize", PAGESIZE+"");
		params.put("profitStartDate", cStart==null?"":cStart);
		params.put("profitEndDate", cEnd==null?"":cEnd);
		MyHttpClient.post(this, Urls.PROFIT_QUERY, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {

						Logger.json("[分润查询]", responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
                                JSONArray array=r.getJsonBody().getJSONArray("profitList");
                                int size=array.length();
                                if(val.size()>0&&type==0){
                                	val.clear();
                                	listview.setCanLoadMore(true);
                                }
                                if((size<PAGESIZE&&type==1)||(size<PAGESIZE&&type==0)){
                                	listview.setCanLoadMore(false);
                                	listview.hideFooterView();
                                	listview.invalidate();
                                }
                                for (int i = 0; i <size; i++) {
                                	JSONObject obj=array.getJSONObject(i);
                                	ProfitBean b=new ProfitBean();
                                	b.setProfitId(obj.optString("profitId"));
                                	b.setMngAmt(AmountUtils.changeFen2Yuan(obj.optString("mngAmt")));
                                	b.setTxnAmt(AmountUtils.changeFen2Yuan(obj.optString("txnAmt")));
                                	b.setCustClass(obj.optString("custClass"));
                                	b.setProfitDate(Utils.formatDate(obj.optString("profitDate"), ""));
                                	val.add(b);
								}
                                if(null==adapter){
                                	adapter=new ProfitAdapter(ProfitQueryActivity.this, val);
                                	listview.setAdapter(adapter);
                                }else{
                                	adapter.refreshAdapter(val);
                                	adapter.notifyDataSetChanged();
                                }
							} else {
								showDialog(r.getMsg());
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
						showLoadingDialog("查询中...");
					}

					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
						if(type==1){
							currentPage++;
							listview.onLoadMoreComplete();
						}else {
							listview.onRefreshComplete();
						}
					}
				});
	}
	
	OnRefreshListener onRefresh=new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			currentPage=1;
			cStart=null;
			cEnd=null;
             loadData(0, 0);			
		}
	};
	OnLoadMoreListener onLoadMore=new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore() {
			loadData(1, currentPage*PAGESIZE);
			
		}
	};
	private String cStart,cEnd;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 10) {
			if (data != null) {
				cStart = data.getStringExtra("d1");
				cEnd = data.getStringExtra("d2");
				loadData(0, 0);
				System.out.println(cStart + "----" + cEnd);
				//initData(currentType, cStart, cEnd, 1);
			}
		}
	};
}
