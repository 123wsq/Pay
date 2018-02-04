package com.lk.td.pay.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.lk.td.pay.fragment.MainFragment;
import com.lk.td.pay.fragment.MerchantFragment;
import com.lk.td.pay.fragment.MoreFragment;
import com.td.app.pay.hx.R;

public class MenuActivity extends BaseFragmentActivity implements OnClickListener{
	
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public List<RadioButton> Tabs = new ArrayList<RadioButton>();
	private List<String> titleList = new ArrayList<String>();
	public TextView titleText;
	private boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
	
		super.onCreate(arg0);
		setContentView(R.layout.activity_menu);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		titleText = (TextView) findViewById(R.id.title);
		titleText.setText("应用中心");
		Tabs.add((RadioButton)findViewById(R.id.home_btn));
		Tabs.add((RadioButton)findViewById(R.id.account_btn));
		Tabs.add((RadioButton)findViewById(R.id.more_btn));
		
		Tabs.get(0).setOnClickListener(this);
		Tabs.get(1).setOnClickListener(this);
		Tabs.get(2).setOnClickListener(this);

//		fragments.add(CashInFragment.newInstance());
		fragments.add(new MainFragment());
		fragments.add(MerchantFragment.newInstance());
		fragments.add(MoreFragment.newInstance());
		
		titleList.add("应用中心");
		titleList.add("我的账户");
        titleList.add("更多");       
		
		fragmentManager = getSupportFragmentManager();
		Tabs.get(0).callOnClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			replace(0);		
			break;
		case R.id.account_btn:
			replace(1);
			break;
		case R.id.more_btn:
			replace(2);
			break;
		}
	}
	
	private void replace(int index){
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.framelayout_content, fragments.get(index));
		titleText.setText(titleList.get(index));
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exitApp();
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
	
	/**
	 * @Title: exitApp
	 * @Description: 退出app
	 */
	private void exitApp() {
		if (!isExit) {
			isExit = true;
			showToast(R.string.quit_app);
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			onBackPressed();
		}
	}
	
	/**
	 * 关闭所有Activity
	 */
	public void onBackPressed() {				
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);		
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
	
}
