package com.lk.td.pay.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;


public class AboutActivity extends BaseActivity implements OnClickListener {

    private Button btn_back;
    private TextView more_tel, more_qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setText("关于我们");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        findViewById(R.id.about_protocol_text).setOnClickListener(this);
        findViewById(R.id.tv_title).setVisibility(View.GONE);

        more_tel = (TextView) findViewById(R.id.more_tel);
        more_qq = (TextView) findViewById(R.id.more_qq);

        init();
    }

    public void init(){

        String format_tel = getString(R.string.contact_tel);
        String result_tel = String.format(format_tel,getString(R.string.tel));
        more_tel.setText(result_tel);

        String format_qq = getString(R.string.contact_qq);
        String result_qq = String.format(format_qq,getString(R.string.qq));
        more_qq.setText(result_qq);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.about_protocol_text:
                goProtocol();
                break;
        }

    }

    private void goProtocol() {
        Intent it = new Intent(this, ProtocolActivity.class);
        startActivity(it);
    }

}
