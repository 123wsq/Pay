package com.lk.td.pay.wedget;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import com.lk.td.pay.adapter.BankAdapter;
import com.lk.td.pay.golbal.MApplication;
import com.td.app.pay.hx.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class ShowBankListDialog extends DialogFragment implements OnItemClickListener{
	private TextView titleTxt;
	private ListView provinceListView;
	private String tipStr;
	private List<String> arrayList;
	private BankAdapter BankAdapter;
	private MApplication mApplication;
	private IGetBank iGetBankAndCityId;
	private String bankName;
	
	public ShowBankListDialog(IGetBank iGetBankAndCityId){
		
		this.iGetBankAndCityId=iGetBankAndCityId;
		
	}

public void setContent(String tipStr, List<String> arrayList){
		
		this.tipStr=tipStr;
		this.arrayList=arrayList;
	}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	mApplication=(MApplication) getActivity().getApplication();
	LayoutInflater inflater = getActivity().getLayoutInflater();
	View view = inflater.inflate(R.layout.province_city_layout, null);
	titleTxt = (TextView) view.findViewById(R.id.titleTxt);
	titleTxt.setText(tipStr);
	provinceListView=(ListView) view.findViewById(R.id.provinceListView);
	BankAdapter=new BankAdapter(getActivity(), arrayList);
	provinceListView.setAdapter(BankAdapter);
	provinceListView.setOnItemClickListener(this);
	Dialog dialog = new Dialog(getActivity(), R.style.dialog);
	dialog.setCanceledOnTouchOutside(false);
	dialog.setContentView(view);
	Window window=dialog.getWindow();
	window.getDecorView().setPadding(30, 50, 30, 100);
	WindowManager.LayoutParams params = window.getAttributes();  
	params.width = WindowManager.LayoutParams.FILL_PARENT;
	params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	params.gravity=Gravity.CENTER;
	window.setAttributes(params);
	
	return dialog;
}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String value = (String) arrayList.get(position);
		
		iGetBankAndCityId.getBankName(value);
		System.out.println("-----------------bankName--------------->>>"+value);
	}
	
	public interface IGetBank{
    	
    	public void getBankName(String bankName);
    	
    }

}
