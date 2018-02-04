package com.lk.td.pay.adapter;

import java.util.ArrayList;
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

public class BluetoothAdapter extends BaseAdapter {

	private Context context;
	private List<DeviceInfo> deviceInfos;

	public BluetoothAdapter(Context c) {
		super();
		context = c;
		deviceInfos = new ArrayList<DeviceInfo>();
	}

	public void clear() {
		deviceInfos.clear();
		this.notifyDataSetChanged();
	}

	public void addDevice(DeviceInfo deviceInfo) {
		deviceInfos.add(deviceInfo);
		this.notifyDataSetChanged();
	}

	public DeviceInfo getDeviceInfo(int position) {
		return deviceInfos.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return deviceInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView bluetoothText;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.bluetooth_item, null);
			bluetoothText = (TextView) convertView.findViewById(R.id.bluetooth_item_name);
			convertView.setTag(bluetoothText);
		} else {
			bluetoothText= (TextView) convertView.getTag();
		}
		bluetoothText.setText(deviceInfos.get(position).getName());
		return convertView;
	}

}
