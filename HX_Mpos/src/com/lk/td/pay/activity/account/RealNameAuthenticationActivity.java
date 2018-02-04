package com.lk.td.pay.activity.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.BitmapUtil;
import com.pay.library.uils.Checkingroutine;
import com.pay.library.uils.ExpresssoinValidateUtil;
import com.pay.library.uils.FileUtil;
import com.pay.library.uils.ImageUtils;
import com.pay.library.uils.MD5Util;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * 商户认证
 * IGetProvinceIdAndCityId
 */
public class RealNameAuthenticationActivity extends BaseFragmentActivity
        implements OnClickListener {

    private final int ADD_ID_CARD_HOLD = 100;
    private final int ADD_ID_CARD_FRONT = 101;
    private final int ADD_ID_CARD_SIDE = 102;
    private ImageView btn_hold_bca_front;// 手持照片按钮
    private ImageView btn_bca_front, btn_bca_back;// 正面,反面按钮
    private Button btnUpload;
    private EditText et_name;// 名字
    private EditText editTxtID;// 身份证号
    //    private EditText et_email;// 邮箱
    private EditText et_payPassword;// 密码
    //    private TextView txt_province;// 省
//    private TextView txt_city;// 市 区
    private LinearLayout recruitmentReLayout;// 省市的布局
    //    private ShowProvinceListDialog showProvinceListDialog;
    private ArrayList<HashMap<String, Object>> firstArrayList = null;// 存放省一类
    public static final int ACTION_UPLOAD_IMG = 0;
    public static final int ACTION_UPLOAD_INFO = 1;
    private String custName;
    private String txtID;
    //    private String email;
    private String payPwd;
    private String cardHandheld;
    private String cardFront;
    private String cardBack;
    private boolean selected1, selected2, selected3;
    private Context mContext;
    private Button btn_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_name_authentication);
        mContext = this;
        cardHandheld = FileUtil.getTdPath(mContext) + "cardHandheld.jpg";
        cardFront = FileUtil.getTdPath(mContext) + "cardFront.jpg";
        cardBack = FileUtil.getTdPath(mContext) + "cardBack.jpg";

        et_name = (EditText) findViewById(R.id.et_name);
        editTxtID = (EditText) findViewById(R.id.editTxtID);
//        et_email = (EditText) findViewById(R.id.et_email);
        et_payPassword = (EditText) findViewById(R.id.et_payPassword);
//        txt_province = (TextView) findViewById(R.id.txt_province);
//        txt_city = (TextView) findViewById(R.id.txt_city);
        recruitmentReLayout = (LinearLayout) findViewById(R.id.recruitmentReLayout);
        recruitmentReLayout.setOnClickListener(this);

        btn_bca_front = (ImageView) findViewById(R.id.btn_bca_front);
        btn_bca_back = (ImageView) findViewById(R.id.btn_bca_back);
        btnUpload = (Button) findViewById(R.id.btn_bca_next);
        btn_hold_bca_front = (ImageView) findViewById(R.id.btn_hold_bca_front);
        btn_hold_bca_front.setOnClickListener(this);
//        showProvinceListDialog = new ShowProvinceListDialog(
//                RealNameAuthenticationActivity.this);
        btn_bca_front.setOnClickListener(this);
        btn_bca_back.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("实名认证");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hold_bca_front:
                takePicture(ADD_ID_CARD_HOLD, cardHandheld);
                break;
            case R.id.btn_bca_front:
                takePicture(ADD_ID_CARD_FRONT, cardFront);
                break;
            case R.id.btn_bca_back:
                takePicture(ADD_ID_CARD_SIDE, cardBack);
                break;
            case R.id.btn_bca_next:
                generateImg();
                break;
            case R.id.recruitmentReLayout:
                getProvincesAndCity();
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void generateImg() {
        if (!selected1) {
            Toast.makeText(this, "请上传手持身份证照片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selected2) {
            Toast.makeText(this, "请上传身份证正面照片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selected3) {
            Toast.makeText(this, "请上传身份证反面照片", Toast.LENGTH_SHORT).show();
            return;
        }

        custName = et_name.getText().toString();
        if (TextUtils.isEmpty(custName)) {

            Toast.makeText(this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
            return;

        } else if (!ExpresssoinValidateUtil.isChineseString(custName)) {
            Toast.makeText(this, R.string.name_is_chinese, Toast.LENGTH_SHORT).show();
            return;
        }
        payPwd = et_payPassword.getText().toString().trim();
        if (payPwd.length() == 0) {
            T.ss("请输入支付密码");
            return;
        } else if (payPwd.length() < 6) {
            T.ss("支付密码为6-15个数字字母");
            return;
        }
//        email = et_email.getText().toString();
        txtID = editTxtID.getText().toString();
        if (TextUtils.isEmpty(txtID)) {
            Toast.makeText(this, "身份证号不能为空!", Toast.LENGTH_SHORT).show();
            return;

        }
        String identifyID = null;
        try {
            identifyID = Checkingroutine.IDCard.IDCardValidate(txtID);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!identifyID.equals("")) {
            Toast.makeText(this, identifyID, Toast.LENGTH_SHORT).show();
            return;
        }

//
//        if (TextUtils.isEmpty(email)) {
//            Toast.makeText(this, "邮箱不能为空!", Toast.LENGTH_SHORT).show();
//            return;
//
//        } else if (!email.contains("@f-wing.cn")) {
//            if (!StringUtils.isEmail(email)) {
//                Toast.makeText(this, "邮箱格式错误!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//
//        provId = txt_province.getHint() + "";
//        cityId = txt_city.getHint() + "";
//        if (TextUtils.isEmpty(cityId)) {
//            Toast.makeText(this, "所在城市不能为空!", Toast.LENGTH_SHORT).show();
//            return;
//
//        }

        upload();

    }


    private void upload() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("custName", custName);// 用户名
        params.put("certificateType", "01");// 证件类型
        params.put("certificateNo", txtID);
//        params.put("userEmail", email);// 邮箱
//        params.put("provinceId", provId);// 所在省ID
//        params.put("cityId", cityId);// 所在市ID
        params.put("payPwd", MD5Util.generatePassword(payPwd));
        // post("SY0007", params);
        MyHttpClient.post(this, Urls.IDENTITY_CHECH, params, cardHandheld, cardFront, cardBack,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                T.showCustomeOk(
                                        RealNameAuthenticationActivity.this,
                                        "已提交审核");
                                MApplication.getInstance()
                                        .refreshUserInfo();
                                User.uStatus = 1;

                                startActivity(new Intent(RealNameAuthenticationActivity.this, BindBankCardActivity.class).putExtra("action", "1"));
                                finish();
                            } else {
                                T.ss(r.getMsg());
                            }
                        } catch (JSONException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        T.sl("网络错误:" + error.getMessage());
                    }

                    @Override
                    public void onStart() {
                        showLoadingDialog("正在上传图片信息，请耐心等待...");
                        firstArrayList = null;
                    }

                    @Override
                    public void onFinish() {
                        dismissLoadingDialog();
                    }

                });

    }

    /**
     * 拍照
     *
     * @param code
     */
    private void takePicture(int code, String filePath) {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(filePath);
        Uri u = Uri.fromFile(f);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
        startActivityForResult(intent, code);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String pathStr = null;
        if (resultCode != RESULT_OK) {
            return;
        } else if (requestCode == ADD_ID_CARD_HOLD && resultCode == RESULT_OK) {
            setBitmapToImageView(cardHandheld, btn_hold_bca_front, 1);
            pathStr = cardHandheld;
            selected1 = true;
        } else if (requestCode == ADD_ID_CARD_FRONT && resultCode == RESULT_OK) {
            setBitmapToImageView(cardFront, btn_bca_front, 2);
            pathStr = cardFront;
            selected2 = true;
        } else if (requestCode == ADD_ID_CARD_SIDE && resultCode == RESULT_OK) {
            setBitmapToImageView(cardBack, btn_bca_back, 3);
            pathStr = cardBack;
            selected3 = true;
        }
        try {
            showLoadingDialog();
            ImageUtils.saveFile(pathStr, 320, 400);
            dismissLoadingDialog();
        } catch (Exception e) {
            Toast.makeText(mContext, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 给imageView设置Bitmap
     *
     * @param imagePath
     * @param iamgeView
     * @param whitch
     */
    private void setBitmapToImageView(String imagePath, ImageView iamgeView,
                                      int whitch) {
        Bitmap tempValue = BitmapUtil.resizeImageSecondMethod(imagePath,
                iamgeView.getWidth(), iamgeView.getHeight());
        iamgeView.setImageBitmap(tempValue);
        iamgeView.setLayoutParams(new LinearLayout.LayoutParams(iamgeView
                .getWidth(), iamgeView.getHeight()));
    }

    /**
     * 获取省市列表
     */
    private void getProvincesAndCity() {
        HashMap<String, String> params = new HashMap<String, String>();
        postFromDing(Urls.PROVINCE, params);

    }

    private void postFromDing(String url, HashMap<String, String> params) {

        MyHttpClient.post(RealNameAuthenticationActivity.this, url, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {

                        dismissLoadingDialog();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        ArrayList<HashMap<String, Object>> secondArrayList = null;// 存放区/市一级
                        HashMap<String, Object> firstHashMap = null;
                        HashMap<String, Object> secondHashMap = null;// 存放区/县一级

                        try {
                            BasicResponse result = new BasicResponse(
                                    responseBody).getResult();
                            if (result.isSuccess()) {

                                firstArrayList = new ArrayList<HashMap<String, Object>>();
                                JSONObject jsonOneBody = result.getJsonBody();
                                JSONArray jsonOneArray = jsonOneBody
                                        .getJSONArray("province");
                                for (int i = 0; i < jsonOneArray.length(); i++) {

                                    firstHashMap = new HashMap<String, Object>();
                                    JSONObject jsonTwoBody = jsonOneArray
                                            .getJSONObject(i);
                                    JSONArray jsonSecondArray = jsonTwoBody
                                            .getJSONArray("cityList");
                                    if (jsonSecondArray != null) {

                                        secondArrayList = new ArrayList<HashMap<String, Object>>();
                                        for (int j = 0; j < jsonSecondArray
                                                .length(); j++) {

                                            secondHashMap = new HashMap<String, Object>();
                                            secondHashMap.put("cityId",
                                                    jsonSecondArray
                                                            .getJSONObject(j)
                                                            .get("cityId"));
                                            secondHashMap.put("cityName",
                                                    jsonSecondArray
                                                            .getJSONObject(j)
                                                            .get("cityName"));
                                            secondHashMap.put("provId",
                                                    jsonSecondArray
                                                            .getJSONObject(j)
                                                            .get("provId"));
                                            secondArrayList.add(secondHashMap);
                                        }
                                    }
                                    firstHashMap.put("cityList",
                                            secondArrayList);
                                    firstHashMap.put("provName",
                                            jsonTwoBody.get("provName"));
                                    firstHashMap.put("provId",
                                            jsonTwoBody.get("provId"));
                                    firstArrayList.add(firstHashMap);
                                }
                            } else {
                                showToast(result.getMsg());
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {

                        firstArrayList = null;
                        Toast.makeText(RealNameAuthenticationActivity.this,
                                "网络连接超时", Toast.LENGTH_SHORT).show();

                    }

                });
    }

    @Override
    public void onBackPressed() {
        onDestoryDialog();
        super.onBackPressed();
    }


    private void onDestoryDialog() {

//        if (showProvinceListDialog != null
//                && showProvinceListDialog.isVisible()) {
//
//            showProvinceListDialog.dismiss();
//            showProvinceListDialog = null;
//
//        }

    }

}
