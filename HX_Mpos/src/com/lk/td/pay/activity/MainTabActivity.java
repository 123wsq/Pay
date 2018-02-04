package com.lk.td.pay.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hx.view.widget.MyDialog;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.fragment.MainFragment;
import com.lk.td.pay.fragment.MerchantFragment;
import com.lk.td.pay.fragment.MoreFragment;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.AppManager;
import com.lk.td.pay.utils.AppUpdateUtil;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BindDeviceInfo;
import com.pay.library.bean.PosRate;
import com.pay.library.bean.User;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.tool.LSharePreference;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.store.SharedPrefConstant;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainTabActivity extends BaseActivity implements OnClickListener {

    private int[] ids = new int[]{R.id.main_tab_account,
            R.id.main_tab_function, R.id.main_tab_more};
    private ImageView iv1, iv2, iv3;
    private TextView tv1, tv2, tv3;
    private Context mContext;
    private FragmentManager fm;
    private String currentFName;
    private HashMap<String, Fragment> fragments;
    private MainFragment app;
    private MoreFragment more;
    private MerchantFragment account;
    FragmentTransaction fragmentTransation;
    List<BindDeviceInfo> devices = new ArrayList<BindDeviceInfo>();
    private LSharePreference lsp;
    private final int QUERY_SUCCESS = 99;
    private boolean showLocal = true;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.tab_main_bottom);

        mContext = this;
        initView();
        lsp = LSharePreference.getInstance(this);
        AppManager.getAppManager().addActivity(this);
        MApplication.getInstance().setmainHomeContext(mContext);

        checkUpdate();
        getUserInfo();
        getLocation();
        initScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("abc", "this is a data obj from TabmianActivity");
    }

    private void initView() {
        for (int i = 0; i < ids.length; i++) {
            findViewById(ids[i]).setOnClickListener(this);
        }
        fm = getSupportFragmentManager();
        fragments = new HashMap<>();
        app = new MainFragment();
        fragments.put("app", app);
        // 初始化第一个Fragment
        FragmentTransaction transation = fm.beginTransaction();
        transation.add(R.id.frame_content, app);
        transation.commit();
        currentFName = "app";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_account:
                fragmentTransation = fm.beginTransaction();
                if (null == account) {
                    fragmentTransation.hide(fragments.get(currentFName));
                    account = new MerchantFragment();
                    fragmentTransation.add(R.id.frame_content, account);
                    fragments.put("account", account);
                    fragmentTransation.commit();
                } else {
                    fragmentTransation.hide(fragments.get(currentFName));
                    fragmentTransation.show(account);
                    fragmentTransation.commit();
                }
//                account.reFreshStatus();
                currentFName = "account";
                switchStatus(2);
                break;
            case R.id.main_tab_function:
                if (currentFName.equals("app")) {
                    return;
                }
                fragmentTransation = fm.beginTransaction();
                fragmentTransation.hide(fragments.get(currentFName));
                fragmentTransation.show(app);
                fragmentTransation.commit();
                currentFName = "app";
                // mTab1.setBackgroundResource(R.drawable.menu1_2);
                // mTab2.setBackgroundResource(R.drawable.menu2_1);
                switchStatus(1);
                break;

            case R.id.main_tab_more:
                fragmentTransation = fm.beginTransaction();
                // FragmentTransaction ft=fm.beginTransaction();
                if (null == more) {
                    fragmentTransation.hide(fragments.get(currentFName));
                    more = new MoreFragment();
                    fragmentTransation.add(R.id.frame_content, more);
                    fragments.put("more", more);
                    fragmentTransation.commit();
                } else {
                    fragmentTransation.hide(fragments.get(currentFName));
                    fragmentTransation.show(more);
                    fragmentTransation.commit();
                }
                currentFName = "more";
                switchStatus(3);
                break;
        }
    }

    private void switchStatus(int which) {
        if (which == 1) {
            ((ImageView) findViewById(R.id.main_tab_iv1))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.app_blue));
            ((ImageView) findViewById(R.id.main_tab_iv2))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.pe128));
            ((ImageView) findViewById(R.id.main_tab_iv3))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.more128));
            // findViewById(R.id.main_tab_iv1).setBackgroundResource(R.drawable.app_blue);
            // findViewById(R.id.main_tab_iv2).setBackgroundResource(R.drawable.pe128);
            // findViewById(R.id.main_tab_iv3).setBackgroundResource(R.drawable.more128);
            ((TextView) findViewById(R.id.main_tab_tv1))
                    .setTextColor(getResources().getColor(
                            R.color.tab_text_color));
            ((TextView) findViewById(R.id.main_tab_tv2))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
            ((TextView) findViewById(R.id.main_tab_tv3))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
        } else if (which == 2) {
            ((ImageView) findViewById(R.id.main_tab_iv1))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.app128));
            ((ImageView) findViewById(R.id.main_tab_iv2))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.pe128_blue));
            ((ImageView) findViewById(R.id.main_tab_iv3))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.more128));
            // findViewById(R.id.main_tab_iv1).setBackgroundResource(R.drawable.app128);
            // findViewById(R.id.main_tab_iv2).setBackgroundResource(R.drawable.pe128_blue);
            // findViewById(R.id.main_tab_iv3).setBackgroundResource(R.drawable.more128);
            ((TextView) findViewById(R.id.main_tab_tv1))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
            ((TextView) findViewById(R.id.main_tab_tv2))
                    .setTextColor(getResources().getColor(
                            R.color.tab_text_color));
            ((TextView) findViewById(R.id.main_tab_tv3))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
        } else {
            ((ImageView) findViewById(R.id.main_tab_iv1))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.app128));
            ((ImageView) findViewById(R.id.main_tab_iv2))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.pe128));
            ((ImageView) findViewById(R.id.main_tab_iv3))
                    .setImageDrawable(getResources().getDrawable(
                            R.drawable.more_blue));
            // findViewById(R.id.main_tab_iv1).setBackgroundResource(R.drawable.app128);
            // findViewById(R.id.main_tab_iv2).setBackgroundResource(R.drawable.pe128);
            // findViewById(R.id.main_tab_iv3).setBackgroundResource(R.drawable.more_blue);
            ((TextView) findViewById(R.id.main_tab_tv1))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
            ((TextView) findViewById(R.id.main_tab_tv2))
                    .setTextColor(getResources().getColor(R.color.gray_holo_dark));
            ((TextView) findViewById(R.id.main_tab_tv3))
                    .setTextColor(getResources().getColor(
                            R.color.tab_text_color));
        }
    }

    MyDialog dialog = null;

    private void getUserInfo() {
        dialog = new MyDialog(this);
        dialog.setCancelable(false);
        dialog.setText("请稍后...");
        dialog.show();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("custMobile", User.uAccount);
        MyHttpClient.post(mContext, Urls.GET_USER_INFO, map,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        if (responseBody != null) {
                            Logger.json(new String(responseBody));
                            try {
                                JSONObject json = new JSONObject(new String(
                                        responseBody))
                                        .getJSONObject("REP_BODY");
                                if (json.getString("RSPCOD").equals("000000")) {
                                    User.cardNum = json.optInt("cardNum");
                                    User.termNum = json.optInt("termNum");
                                    User.uStatus = json.optInt("custStatus");
                                    User.cardBundingStatus = json.optInt("cardBundingStatus");
                                    User.macAddress = json.optString("macAddress");//蓝牙地址

                                } else {
                                    showDialog(json.getString("RSPMSG"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                    }

                });

        MyHttpClient.post(mContext, Urls.BIND_DEVICE_LIST, map,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[终端列表]", responseBody);
                        try {
                            BasicResponse res = new BasicResponse(responseBody)
                                    .getResult();
                            if (res.isSuccess()) {
                                JSONArray array = res.getJsonBody()
                                        .getJSONArray("termList");
                                for (int i = 0; i < array.length(); i++) {
                                    BindDeviceInfo d = new BindDeviceInfo();
                                    d.setAgentId(array.getJSONObject(i)
                                            .optString("agentId"));
                                    d.setTermNo(array.getJSONObject(i)
                                            .optString("termNo"));
                                    JSONArray rates = array.getJSONObject(i)
                                            .optJSONArray("rate");
                                    List<PosRate> ar = new ArrayList<PosRate>();
                                    d.setRate(ar);
                                    devices.add(d);
                                }
                                User.bindDevices = devices;
                                if (array.length() == 0) {
                                    User.bindStatus = 0;
                                } else {
                                    User.bindStatus = 1;
                                    User.termNo =devices.get(0).getTermNo();
                                }
                            } else {
                                T.ss(res.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyHttpClient.cancleRequest(mContext);
        lsp.delContent("custid");
        lsp.delContent("custmobile");
    }

    private long exit = System.currentTimeMillis();

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
            Toast.makeText(this, R.string.quit_app, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            onBackPressed();
            AppManager.getAppManager().AppExit();
        }
    }

    private boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void checkUpdate() {
        HashMap<String, String> params = new HashMap<String, String>();
        MyHttpClient.post(mContext, Urls.CHECK_UPDATE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(new String(responseBody));
                        try {
                            BasicResponse response = new BasicResponse(
                                    responseBody).getResult();
                            if (response.isSuccess()) {
                                String status = response.getJsonBody()
                                        .optString("checkState");

                                if (status.equals("2")) {
                                    // T.ss("已经是最新版本");
                                } else if (status.equals("1")) {
                                    String note = response.getJsonBody()
                                            .optString("fileDesc");
                                    String temp = response.getJsonBody()
                                            .optString("fileUrl");
                                    String url = AppConfig.HOST
                                            + response.getJsonBody().optString(
                                            "fileUrl");
                                    if (TextUtils.isEmpty(url)
                                            || "null".equals(temp)) {
                                        return;
                                    }
                                    AppUpdateUtil update = new AppUpdateUtil(
                                            mContext, url);
                                    update.showUpdateNoticeDialog(note);
                                } else if (status.equals("3")) {// 强制更新
                                    String note = response.getJsonBody()
                                            .optString("fileDesc");
                                    String url = AppConfig.HOST
                                            + response.getJsonBody().optString(
                                            "fileUrl");
                                    String temp = response.getJsonBody()
                                            .optString("fileUrl");
                                    if (TextUtils.isEmpty(url)
                                            || "null".equals(temp)) {
                                        return;
                                    }
                                    AppUpdateUtil update = new AppUpdateUtil(
                                            mContext, url);
                                    update.showUpdateNoticeDialog(note, false);
                                }
                            } else {
                                // T.ss(response.getMsg());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                    }
                });
    }

    /*
     * 定位
     */
    private void getLocation() {
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(3000);// 设置发起定位请求的间隔时间为3000ms
        option.disableCache(false);// 禁止启用缓存定位
        option.setPriority(LocationClientOption.NetWorkFirst);// 网络定位优先
        mLocationClient.setLocOption(option);// 使用设置
        mLocationClient.start();// 开启定位SDK
        mLocationClient.requestLocation();// 开始请求位置

    }

    private void initScan(){
        try {
            copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                StringBuffer sb = new StringBuffer(128);// 接受服务返回的缓冲区
                sb.append(location.getCity());// 获得城市
                String loc = sb.toString().trim();
                MApplication.mSharedPref.putSharePrefString(
                        SharedPrefConstant.CITY_NAME, loc);
                if (mLocationClient != null && mLocationClient.isStarted()) {
                    mLocationClient.stop();// 关闭定位SDK
                    mLocationClient = null;
                }
                System.out.println("city-->" + loc);
                Logger.d("city-->" + loc);
            } else {
            	MApplication.mSharedPref.putSharePrefString(
                        SharedPrefConstant.CITY_NAME, "");
            }
        }
    }

    public void copyDataBase() throws IOException {
        //  Common common = new Common();
        String dst = Environment.getExternalStorageDirectory().toString() + "/129AE5220F8D531981E7.lic";

        File file = new File(dst);
        if (!file.exists()) {
            // file.createNewFile();
        } else {
            file.delete();
        }

        try {
            InputStream myInput = getAssets().open("129AE5220F8D531981E7.lic");
            OutputStream myOutput = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            System.out.println("129AE5220F8D531981E7.lic" + "is not found");
        }
    }
}
