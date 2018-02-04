package com.lk.td.pay.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.view.widget.CustomDialog;
import com.hx.view.widget.MyDialog;
import com.lk.td.pay.activity.more.AboutActivity;
import com.lk.td.pay.activity.more.HelpActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.AppManager;
import com.lk.td.pay.utils.AppUpdateUtil;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;

import java.util.HashMap;

/**
 * @author 樊贝
 */
public class MoreFragment extends BaseFragment implements OnClickListener {

    private TextView more_share_text;// 分享
    private TextView more_about_text;// 关于我们
    private TextView more_feedback_text;// 意见反馈
    private TextView more_help_text;// 使用帮助
    private TextView more_version_text;//版本号
    private LinearLayout more_version_layout;// 版本更新
    private LinearLayout merchant_contact_layout;// 拨打客服电话
    private String userName;
    private MApplication mApplication;
    private TextView tv_title;

    public static BaseFragment newInstance() {
        BaseFragment fragment = new MoreFragment();
        return fragment;
    }

    private View layoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MApplication) getActivity().getApplication();
        userName = User.uName;
        dialog = new MyDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layoutView == null) {

            layoutView = inflater.inflate(R.layout.fragment_more, container,
                    false);
            tv_title = (TextView) layoutView.findViewById(R.id.tv_title);
            tv_title.setText("更多");
            more_share_text = (TextView) layoutView
                    .findViewById(R.id.more_share_text);
            more_share_text.setOnClickListener(this);
            more_version_layout = (LinearLayout) layoutView
                    .findViewById(R.id.more_version_layout);
            more_version_layout.setOnClickListener(this);
            more_about_text = (TextView) layoutView
                    .findViewById(R.id.more_about_text);
            more_about_text.setOnClickListener(this);
            merchant_contact_layout = (LinearLayout) layoutView
                    .findViewById(R.id.merchant_contact_layout);
            merchant_contact_layout.setOnClickListener(this);
            more_feedback_text = (TextView) layoutView
                    .findViewById(R.id.more_feedback_text);
            more_feedback_text.setOnClickListener(this);
            more_help_text = (TextView) layoutView
                    .findViewById(R.id.more_help_text);
            more_help_text.setOnClickListener(this);
            more_version_text = (TextView) layoutView.findViewById(R.id.more_version_text);
            more_version_text.setText("V" + getVersion());
            layoutView.findViewById(R.id.btn_logout).setOnClickListener(this);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) layoutView.getParent();
        if (parent != null) {
            parent.removeView(layoutView);
        }
        return layoutView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.more_share_text:

                share();
                break;
            case R.id.more_version_layout:
                // startActivity(new Intent(getActivity(),
                // VersionInfoActivity.class));
                checkUpdate();
                break;
            case R.id.more_about_text:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.merchant_contact_layout:
                dialTelephone();
                break;
            case R.id.more_feedback_text:
                feedback();
                break;
            case R.id.more_help_text:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.id.btn_logout:
                CustomDialog.Builder al = new CustomDialog.Builder(getActivity());
                al.setTitle("操作提示")
                        .setMessage(getResources().getString(R.string.sure_logout))
                        .setOkBtn("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelBtn("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                al.create().show();
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void checkUpdate() {
        HashMap<String, String> params = new HashMap<String, String>();
        MyHttpClient.post(getActivity(), Urls.CHECK_UPDATE, params,
                new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(new String(responseBody));
                        try {
                            BasicResponse response = new BasicResponse(responseBody).getResult();
                            if (response.isSuccess()) {
                                String status = response.getJsonBody().optString("checkState");

                                if (status.equals("2")) {
                                    T.ss("已经是最新版本");
                                } else if (status.equals("1")) {
                                    String note = response.getJsonBody().optString("fileDesc");
                                    String temp = response.getJsonBody().optString("fileUrl");
                                    String url = AppConfig.HOST + response.getJsonBody().optString("fileUrl");
                                    System.out.println("url===" + url);
                                    if (TextUtils.isEmpty(url) || "null".equals(temp)) {
//                                    	 T.ss("已是最新版本");
                                        return;
                                    }
                                    AppUpdateUtil update = new AppUpdateUtil(getActivity(), url);
                                    update.showUpdateNoticeDialog(note);
                                } else if (status.equals("3")) {//强制更新
                                    String note = response.getJsonBody().optString("fileDesc");
                                    String url = AppConfig.HOST + response.getJsonBody().optString("fileUrl");
                                    String temp = response.getJsonBody().optString("fileUrl");
                                    if (TextUtils.isEmpty(url) || "null".equals(temp)) {
                                        T.ss("已是最新版本");
                                        return;
                                    }
                                    AppUpdateUtil update = new AppUpdateUtil(getActivity(), url);
                                    update.showUpdateNoticeDialog(note, false);
                                }
                            } else {
                                T.ss(response.getMsg());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        T.ss("网络错误:" + error.getMessage());
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        }
    }

    MyDialog dialog;

    private void logout() {
        MyHttpClient.post(getActivity(), "SY0006.json", new HashMap<String, String>(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();
                dialog.setText("正在退出...");
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
                AppManager.getAppManager().AppExit();
            }
        });
    }

    /**
     * 分享功能
     */
    private void share() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getActivity().getTitle()));
        getActivity().overridePendingTransition(R.anim.share_in_from_bottom, 0);
    }

    /**
     * 弹出客服电话对话框
     */
    private void dialTelephone() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.app_name));

        builder.setMessage("服务热线："+getString(R.string.tel));
        builder.setCancelBtn("拨打客服电话",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        call();
                    }
                });
        builder.setOkBtn(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 进入拨打电话页面
     */
    private void call() {
        Intent it = new Intent();
        it.setAction("android.intent.action.DIAL");
        it.setData(Uri.parse("tel:"+getString(R.string.tel)));
        startActivity(it);
    }

    /**
     * 意见反馈
     */
    private void feedback() {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setType("text/plain");
        data.setData(Uri.parse("mailto:way.ping.li@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "用户：" + userName + " 意见反馈");
        data.putExtra(Intent.EXTRA_TEXT, "请输入反馈意见内容");
        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(data);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "客户端没有安装邮件", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            System.out.println("----------version------------->>>" + version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
