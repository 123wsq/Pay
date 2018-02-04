/**
 * 功能：银行卡adapter
 * 类名：BankCardListViewAdapter.java
 * 日期：2013-12-19
 * 作者：lukejun
 */
package com.lk.td.pay.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pay.library.bean.BankCardItem;
import com.td.app.pay.hx.R;

public class BankCardListViewAdapter extends BaseAdapter {

	private Context context;
	private List<BankCardItem> list;
	private boolean flag; // true 代表右图标可见

	public BankCardListViewAdapter(Context context, List<BankCardItem> list,
			boolean flag) {
		this.context = context;
		this.list = list;
		this.flag = flag;
	}
public void refresh(List<BankCardItem> list){
	this.list=list;
}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	Drawable draw_fast;

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		final BankCardItem detail = list.get(i);
		if (null == view) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.bank_card_list_item, null);
			viewHolder.bankImageView = (ImageView) view
					.findViewById(R.id.iv_bankc_img);
			viewHolder.bankNameTextView = (TextView) view
					.findViewById(R.id.item_tv_bc_name);
			viewHolder.lastNumberTextView = (TextView) view
					.findViewById(R.id.item_tv_bc_no);
			
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		String imageUrl = detail.getUrl();
//		viewHolder.bankImageView.setImageUrl(imageUrl,
//				R.drawable.bank_logo_default);
		viewHolder.bankNameTextView.setText(detail.getCardName());
		viewHolder.lastNumberTextView.setText(getlastcode(detail
				.getCardNo()));
		
//		if (flag) {
//			viewHolder.rightImage.setVisibility(View.VISIBLE);
//
//		}
		return view;
	}

	private String getlastcode(String code) {
		if (null == code)
			return "";
		else {
			return code.substring(code.length() - 4, code.length());
		}
	}

	class ViewHolder {
		ImageView bankImageView; // 图标
		TextView bankNameTextView;
		TextView lastNumberTextView;
		ImageView rightImage; // 右边图标
	}
}
