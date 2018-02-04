package com.lk.td.pay.activity.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.pay.library.config.AppConfig;
import com.td.app.pay.hx.R;

public class ProtocolActivity extends BaseFragmentActivity {

//    private TextView contentText;
    private WebView webView;

    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setText(getString(R.string.app_name));
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) this.findViewById(R.id.web_view);
        String url = AppConfig.GET_AGREEMENT;
        initWebView(url);
    }


    private void initWebView(String url) {
        webView.loadUrl(url);
    }
}
