package com.lk.td.pay.activity.main.quickpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.annotation.ContentView;
import com.android.annotation.ViewInject;
import com.android.annotation.event.OnClick;
import com.pay.library.uils.ViewUtils;
import com.td.app.pay.hx.R;


/**
 * Created by wsq on 2016/5/31.
 */
@ContentView(R.layout.layout_quick_protocol)
public class QuickPayProtocolActivity extends Activity{

    @ViewInject(R.id.btn_back)Button btn_back;
    @ViewInject(R.id.iv_protocol_image)ImageView iv_protocol_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);
        init();
    }

    public void init(){
        btn_back.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_confirm, R.id.btn_back, R.id.btn_cancel})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_confirm:
                startActivity(new Intent(QuickPayProtocolActivity.this, QuickPayActvity.class));
                finish();
                break;
            case R.id.btn_back:
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
