package com.lk.td.pay.adapter;

import java.util.ArrayList;

import com.lk.td.pay.beans.ProfitBean;
import com.td.app.pay.hx.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProfitAdapter extends BaseAdapter {
	private Context ctx;
	private ArrayList<ProfitBean> vals;

	public ProfitAdapter(Context ctx, ArrayList<ProfitBean> vals) {
		this.ctx = ctx;
		this.vals = vals;
	}

	@Override
	public int getCount() {
		return vals == null ? 0 : vals.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return vals.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	public void refreshAdapter(ArrayList<ProfitBean> vals) {
		this.vals = vals;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		H h;
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_profit_layout, null);
			h = new H();
//			h.tvno = (TextView) convertView.findViewById(R.id.item_tv_no);
			h.tva1 = (TextView) convertView.findViewById(R.id.item_tva1);
			h.tva2 = (TextView) convertView.findViewById(R.id.item_tva2);
			h.tvdate = (TextView) convertView.findViewById(R.id.item_tv_date);
			convertView.setTag(h);
		} else {
			h = (H) convertView.getTag();
		}
		ProfitBean b = vals.get(position);
		h.tva1.setText("管理分润:"+b.getMngAmt()+" 元");
		h.tva2.setText("交易分润:"+b.getTxnAmt()+" 元");
		h.tvdate.setText(b.getProfitDate());
//		h.tvno.setText("" + b.getProfitId());

		return convertView;
	}

	class H {
		TextView tvno, tva1, tva2, tvdate;
	}
}
