package com.lk.td.pay.activity.main.cashin;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.pay.library.config.Actions;
import com.td.app.pay.hx.R;

/**
 * 类名: ShowMsgActivity
 * 作者:lukejun
 * 时间：2014年2月13日 下午3:51:06
 * 说明:
 */
public class ShowMsgActivity extends BaseActivity {
    private TextView tvMsg;
    private ImageView ivCode;
    private String action, msg;
    private Button backBtn;
    private boolean code = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_msg);
        backBtn = (Button) findViewById(R.id.btn_back);
        action = getIntent().getAction();
        code = getIntent().getBooleanExtra("code", false);
        ivCode = (ImageView) findViewById(R.id.iv_show_msg_code);
        if (action != null && action.equals("ACTION_CASH_IN")) {
            msg = getIntent().getStringExtra("msg");
            if (code) {
                ivCode.setImageResource(R.drawable.iv_success);
            } else {
                ivCode.setImageResource(R.drawable.iv_fail);
            }
        }

        if (action != null && action.equals("ACTION_CARD_QUERY")) {
            backBtn.setText("银行卡查询");
            msg = getIntent().getStringExtra("msg");
            if (code) {
                ivCode.setImageResource(R.drawable.iv_balance);
            } else {
                ivCode.setImageResource(R.drawable.iv_fail);
            }
        }
        
        if (action != null && action.equals(Actions.ACTION_GET_CODE)) {
            msg = getIntent().getStringExtra("msg");
            if (code) {
                ivCode.setImageResource(R.drawable.iv_success);
            } else {
                ivCode.setImageResource(R.drawable.iv_fail);
            }
        }
        tvMsg = (TextView) findViewById(R.id.tv_show_msg_info);
        tvMsg.setText(msg);
        findViewById(R.id.btn_show_msg_confirm).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }

        });
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

}