package com.lk.td.pay.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hx.view.widget.CustomListView.OnRefreshListener;
import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;
import com.lk.td.pay.beans.NoticeBean;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;

public class NoticeDetailActivity extends BaseActivity {
	private NoticeBean bean;
	private TextView title, tv_content, tv_time;
	private Button btn_back;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_detail_layout);
		initView();
		loadData(0);
		Logger.init().setMethodCount(0).hideThreadInfo();
	}

	ArrayList<NoticeBean> adaVal = new ArrayList<NoticeBean>();

	private void loadData(int id) {

	}

	private void initView() {
		bean = (NoticeBean) getIntent().getSerializableExtra("data");
		title = findView(R.id.tv_msg_title);
		tv_content = findView(R.id.tv_msg_content);
		tv_time = findView(R.id.tv_msg_time);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_title.setText("消息详情");
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		title.setText(toS(bean.getTitle()));
		tv_content.setText(toS(bean.getContent()));
		tv_time.setText(datePaser(bean.getTime()));
		
	}
	
	private String datePaser(String str){
		SimpleDateFormat d	=new SimpleDateFormat("yyyyMMddHHmmss");
		 try {
			Date date=d.parse(str);
			SimpleDateFormat temp=new SimpleDateFormat("MM月dd日 HH:mm:ss");
			return temp.format(date);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return "";
		}
		
		
	}

	private String toS(String str) {
		if (null == str)
			return "";
		return str;
	}

	OnRefreshListener onRefrsh = new OnRefreshListener() {

		@Override
		public void onRefresh() {

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
