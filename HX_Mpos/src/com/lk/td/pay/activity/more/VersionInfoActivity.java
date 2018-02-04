package com.lk.td.pay.activity.more;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseActivity;
import com.td.app.pay.hx.R;

public class VersionInfoActivity extends BaseActivity {

    private TextView tvVersionNo;

    private TextView tv_title;
    private Button btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_info);
        tvVersionNo = (TextView) findViewById(R.id.tv_version_info_no);
        tvVersionNo.setText("V" + getVersion());
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_title.setText("版本信息");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
