package com.lk.td.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.landicorp.robert.comm.api.DeviceInfo;
import com.td.app.pay.hx.R;
import com.lk.td.pay.beans.NoticeBean;

public class NoticeMsgAdapter extends BaseAdapter {

	private Context context;
	private List<NoticeBean> val;

	public NoticeMsgAdapter(Context c, ArrayList<NoticeBean> adaval) {
		context = c;
		val = adaval;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return val == null ? 0 : val.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return val.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void refreshValues(ArrayList<NoticeBean> adaval){
		this.val=adaval;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.items_nitoce_msg, null);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.item_tv_notice_title);
			holder.tv_msg = (TextView) convertView
					.findViewById(R.id.item_tv_notice_msg);
			holder.tv_time=(TextView) convertView.findViewById(R.id.item_tv_notice_time);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_title.setText(val.get(position).getTitle());
		String temp=val.get(position).getContent();
		holder.tv_msg.setText(temp);
		holder.tv_time.setText(datePaser(val.get(position).getTime()));
		return convertView;
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
			return "--";
		}
		
		
	}
	class Holder {
		TextView tv_title;
		TextView tv_msg;
TextView tv_time;
	}

}
