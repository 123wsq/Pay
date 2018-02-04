package com.lk.td.pay.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.td.app.pay.hx.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 显示省、市的数据适配
 * @author Administrator
 *
 */
public class ProvinceAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<HashMap<String, Object>> arrayList;
	private LayoutInflater inflater;

	public ProvinceAdapter(Context mContext, ArrayList<HashMap<String, Object>> arrayList) {

		this.mContext = mContext;
		this.arrayList = arrayList;
		inflater = LayoutInflater.from(mContext);

	}
	
	public void setArrayList(ArrayList<HashMap<String, Object>> arrayList){
		
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
			//linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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
		String value=(String) arrayList.get(position).get("provName");
		if(TextUtils.isEmpty(value)){
			
			value=(String) arrayList.get(position).get("cityName");
			if(TextUtils.isEmpty(value)){
				
				value="";
				
			}
			
		}
		viewHolder.txtView.setText(value); 
		return convertView;
	}

	class ViewHolder {

		TextView txtView;

	}

}
