package com.lk.td.pay.adapter;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import com.td.app.pay.hx.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BankAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> arrayList;
	private LayoutInflater inflater;

	public BankAdapter(Context mContext, List<String> arrayList) {

		this.mContext = mContext;
		this.arrayList = arrayList;
		inflater = LayoutInflater.from(mContext);

	}
	
	public void setArrayList(List<String> arrayList){
		
		this.arrayList=arrayList;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList==null?0:arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arrayList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder=new ViewHolder();
			convertView = inflater.inflate(R.layout.item_auto_layout, null);
			LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.auto_adapter_layout);
//			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			linearLayout.setGravity(Gravity.CENTER_VERTICAL);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			viewHolder.txtView=new TextView(mContext);
			viewHolder.txtView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			viewHolder.txtView.setGravity(Gravity.CENTER);
			viewHolder.txtView.setTextSize(20);
			viewHolder.txtView.setPadding(15, 10, 0, 10);
			linearLayout.addView(viewHolder.txtView);
			convertView.setTag(viewHolder);
			
		} else {

			viewHolder = (ViewHolder) convertView.getTag();

		}
		String value=(String) arrayList.get(position);
		viewHolder.txtView.setText(value); 
		return convertView;
	}

	class ViewHolder {

		TextView txtView;

	}

}
