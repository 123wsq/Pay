package com.lk.td.pay.wedget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.account.LoginActivity;
import com.td.app.pay.hx.R;

public class MyDialogActivity extends Activity {

	public static final String ACTION_RELOGIN = "0";
	TextView title, msg;
	Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydialog_layout);
		this.setFinishOnTouchOutside(false);
		title = (TextView) findViewById(R.id.dialog_title);
		msg = (TextView) findViewById(R.id.dialog_msg);
		String titl=getIntent().getStringExtra("title");
		String msgg=getIntent().getStringExtra("msg");
	    title.setText(titl);
	    msg.setText(msgg);
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MyDialogActivity.this,
								LoginActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
						finish();
					}
				});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
