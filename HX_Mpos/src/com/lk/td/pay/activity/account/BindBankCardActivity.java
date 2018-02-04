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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.lk.td.pay.wedget.ShowBankListDialog;
import com.lk.td.pay.wedget.ShowBankListDialog.IGetBank;
import com.lk.td.pay.wedget.ShowProvinceListDialog;
import com.lk.td.pay.wedget.ShowProvinceListDialog.IGetProvinceIdAndCityId;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BankCardItem;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.BitmapUtil;
import com.pay.library.uils.FileUtil;
import com.pay.library.uils.ImageUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * 绑定银行卡
 */
public class BindBankCardActivity extends BaseFragmentActivity implements
        OnClickListener, IGetProvinceIdAndCityId, IGetBank {

    private final int ADD_ID_CARD_FRONT = 101;
    private final int ADD_ID_CARD_SIDE = 102;
    private ImageView btn_bca_front, btn_bca_back;// 正面,反面按钮
    private EditText et_cradNo;// 银行卡号
    private EditText et_oldcradNo;
    private LinearLayout ll_id_card;
    private EditText et_id;
    private TextView txt_province;// 省/市
    private TextView txt_city;// 区/县
    private TextView txt_branch;//支行名称
    private TextView txt_bank;//银行名称
    private Button btn_bca_next;
    private LinearLayout recruitmentReLayout, branchReLayout, bankReLayout;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private String cardFront;
    private String cardBack;
    private String et_cradNoStr, et_oldcradNoStr;
    private String certificateNo;
    private String provId;
    private String cityId;
    private ShowProvinceListDialog showProvinceListDialog;
    private ShowBankListDialog showBankListDialog;
    private ArrayList<HashMap<String, Object>> firstArrayList = null;// 存放省一类
    private boolean selected1, selected2;
    private String bankName;
    private List<String> list = null;
    private List<HashMap<String, Object>> branchlist = null;
    private String bankbankName;
    private String bankbankId;
    private String type;
    private String action;
    private LinearLayout ll_oldcard;
    private Context cxt;
    private List<BankCardItem> bankList = new ArrayList<BankCardItem>();
    private TextView tv_title;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bankcard);
        cxt = BindBankCardActivity.this;
        intUI();
    }


    /**
     * 初始化控件
     */
    private void intUI() {
        action = getIntent().getStringExtra("action");
        cardFront = FileUtil.getTdPath(this) + "cardFront.jpg";
        cardBack = FileUtil.getTdPath(this) + "cardBack.jpg";

        btn_bca_front = (ImageView) findViewById(R.id.btn_bca_front);
        btn_bca_front.setOnClickListener(this);
        btn_bca_back = (ImageView) findViewById(R.id.btn_bca_back);
        btn_bca_back.setOnClickListener(this);
        btn_bca_back.setOnClickListener(this);
        et_cradNo = (EditText) findViewById(R.id.et_cradNo);
        et_oldcradNo = (EditText) findViewById(R.id.et_oldcradNo);
        ll_id_card = (LinearLayout) findViewById(R.id.ll_id_card);
        et_id = (EditText) findViewById(R.id.et_id);
        ll_oldcard = (LinearLayout) findViewById(R.id.ll_oldcard);
        txt_province = (TextView) findViewById(R.id.txt_province);
        txt_city = (TextView) findViewById(R.id.txt_city);
        recruitmentReLayout = (LinearLayout) findViewById(R.id.recruitmentReLayout);
        recruitmentReLayout.setOnClickListener(this);
        btn_bca_next = (Button) findViewById(R.id.btn_bca_next);
        btn_bca_next.setOnClickListener(this);
        txt_bank = (TextView) findViewById(R.id.txt_bank);
        bankReLayout = (LinearLayout) findViewById(R.id.bankReLayout);
        bankReLayout.setOnClickListener(this);
        branchReLayout = (LinearLayout) findViewById(R.id.branchReLayout);
        branchReLayout.setOnClickListener(this);
        txt_branch = (TextView) findViewById(R.id.txt_branch);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("绑定银行卡");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        showProvinceListDialog = new ShowProvinceListDialog(BindBankCardActivity.this);
        showBankListDialog = new ShowBankListDialog(BindBankCardActivity.this);
        if (action.equals("1")) {
            tv_title.setText("绑定银行卡");
            ll_id_card.setVisibility(View.GONE);
            type = "1";
        } else if (action.equals("2")) {
            tv_title.setText("修改银行卡");
            type = "2";
            ll_id_card.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_bca_front:
                takePicture(ADD_ID_CARD_FRONT, cardFront);
                break;
            case R.id.btn_bca_back:
                takePicture(ADD_ID_CARD_SIDE, cardBack);
                break;
            case R.id.recruitmentReLayout:
                getProvincesAndCity();
                break;
            case R.id.branchReLayout:
                if (TextUtils.isEmpty(txt_province.getText().toString()) && TextUtils.isEmpty(txt_city.getText().toString())) {

                    Toast.makeText(this, "请选择行政区划！", Toast.LENGTH_SHORT).show();
                    return;

                } else if (TextUtils.isEmpty(txt_bank.getText().toString())) {
                    Toast.makeText(this, "请选择银行名称！", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    branchReLayout();
                }
                break;
            case R.id.bankReLayout:
                if (TextUtils.isEmpty(txt_province.getText().toString()) && TextUtils.isEmpty(txt_city.getText().toString())) {

                    Toast.makeText(this, "请选择行政区划！", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    bankReLayout();
                }


                break;
            case R.id.btn_bca_next:
                upload();
                break;
        }

    }

    //获取银行名称
    private void bankReLayout() {
        //bankProId bankCityId
        provId = txt_province.getHint().toString();
        cityId = txt_city.getHint().toString();

        if (TextUtils.isEmpty(provId) && TextUtils.isEmpty(cityId)) {

            Toast.makeText(this, "行政区划不能为空!", Toast.LENGTH_SHORT).show();
            return;

        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("bankProId", provId);// 所在省ID
        params.put("bankCityId", cityId);// 所在市ID
        postbank(Urls.BANKNAME, params);
    }


    public void branchReLayout() {

        provId = txt_province.getHint().toString();
        cityId = txt_city.getHint().toString();

        bankName = txt_bank.getText().toString();
        if (TextUtils.isEmpty(provId) && TextUtils.isEmpty(cityId)) {

            Toast.makeText(this, "行政区划不能为空!", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(bankName)) {

            Toast.makeText(this, "银行名称不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        Intent in = new Intent(BindBankCardActivity.this, BankbranchActivity.class);
        in.putExtra("provId", provId);
        in.putExtra("cityId", cityId);
        in.putExtra("bankName", bankName);
        startActivityForResult(in, 1);
    }

    @SuppressWarnings("unused")
    private void postbank(String url, HashMap<String, String> params) {

        MyHttpClient.post(BindBankCardActivity.this, url, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissLoadingDialog();
                        showBankListDialog.setContent("请选择银行名称",
                                list);
                        if (!showBankListDialog.isVisible()) {

                            showBankListDialog.show(
                                    getSupportFragmentManager(),
                                    "PROVINCE_DIALOG");
                            //showProvinceListDialog.setCancelable(false);

                        }


                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody).getResult();
                            if (r.isSuccess()) {
                                JSONObject jsonOneBody = r.getJsonBody();
                                JSONArray jsonSecondArray = jsonOneBody.getJSONArray("bankCardList");
                                String bankCardList = jsonSecondArray.toString();
                                bankCardList = bankCardList.replace("[", "").replace("]", "");
                                String[] str = bankCardList.split(",");
                                System.out.println();
                                list = new ArrayList<String>();
                                for (int i = 0; i < str.length; i++) {
                                    str[i] = str[i].replace("\"", "").replace("\"", "");
                                    list.add(str[i]);
                                }


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

                        Toast.makeText(BindBankCardActivity.this,
                                "网络连接超时", Toast.LENGTH_SHORT).show();

                    }

                });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String pathStr = null;
        if (requestCode == ADD_ID_CARD_FRONT && resultCode == RESULT_OK) {
            setBitmapToImageView(cardFront, btn_bca_front, 1);
            pathStr = cardFront;
            selected1 = true;
        } else if (requestCode == ADD_ID_CARD_SIDE && resultCode == RESULT_OK) {
            setBitmapToImageView(cardBack, btn_bca_back, 2);
            pathStr = cardBack;
            selected2 = true;

        } else if (resultCode == 1000) {
            if (data != null) {
                bankbankId = data.getStringExtra("bankId");
                bankbankName = data.getStringExtra("bankName");
                txt_branch.setText(bankbankName);
                System.out.println("---------------bankName------------------>>" + bankbankName);
                System.out.println("-------------------bankbranchs------------------------>>>" + bankbankId);
            }
        }

        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            showLoadingDialog();
            ImageUtils.saveFile(pathStr, 320, 400);
            dismissLoadingDialog();
        } catch (Exception e) {
            Toast.makeText(BindBankCardActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 给imageView设置Bitmap
     *
     * @param imagePath
     * @param iamgeView
     * @param w
     * @param
     */
    private void setBitmapToImageView(String imagePath, ImageView iamgeView, int w) {
        Bitmap tempValue = BitmapUtil.resizeImageSecondMethod(imagePath, iamgeView.getWidth(), iamgeView.getHeight());
        iamgeView.setImageBitmap(tempValue);
        iamgeView.setLayoutParams(new LinearLayout.LayoutParams(iamgeView.getWidth(), iamgeView.getHeight()));

    }


    /**
     * 拍照
     *
     * @param code
     */
    private void takePicture(int code, String filePath) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(filePath);
        Uri u = Uri.fromFile(f);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
        startActivityForResult(intent, code);
    }

    /**
     * 获取省市列表
     */
    private void getProvincesAndCity() {

        HashMap<String, String> params = new HashMap<String, String>();
        postFromDing(Urls.PROVINCE, params);

    }

    @SuppressWarnings("unused")
    private void postFromDing(String url, HashMap<String, String> params) {

        MyHttpClient.post(BindBankCardActivity.this, url, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {

                        dismissLoadingDialog();
                        showProvinceListDialog.setContent("请选择省份",firstArrayList);
                        if (!showProvinceListDialog.isVisible()) {

                            showProvinceListDialog.show(
                                    getSupportFragmentManager(),
                                    "PROVINCE_DIALOG");
                            //showProvinceListDialog.setCancelable(false);

                        }

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
                                        for (int j = 0; j < jsonSecondArray.length(); j++) {

                                            secondHashMap = new HashMap<String, Object>();
                                            secondHashMap.put("cityId",jsonSecondArray.getJSONObject(j).get("cityId"));
                                            secondHashMap.put("cityName",jsonSecondArray.getJSONObject(j).get("cityName"));
                                            secondHashMap.put("provId",jsonSecondArray.getJSONObject(j).get("provId"));
                                            secondArrayList.add(secondHashMap);
                                        }
                                    }
                                    firstHashMap.put("cityList",secondArrayList);
                                    firstHashMap.put("provName",jsonTwoBody.get("provName"));
                                    firstHashMap.put("provId",jsonTwoBody.get("provId"));
                                    firstArrayList.add(firstHashMap);
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {

                        T.sl("网络错误:" + error.getMessage());

                    }

                });
    }

    @Override
    public void onBackPressed() {
        onDestoryDialog();
        super.onBackPressed();
    }

    private void onDestoryDialog() {

        if (showProvinceListDialog != null
                && showProvinceListDialog.isVisible()) {

            showProvinceListDialog.dismiss();
            showProvinceListDialog = null;

        }

    }

    @Override
    public void getProvinceIdAndCityId(String provName, String provId,
                                       String cityName, String cityId) {
        showProvinceListDialog.dismiss();
        txt_province.setText(provName);
        txt_province.setHint(provId);
        txt_city.setText(cityName);
        txt_city.setHint(cityId);

    }

    private void upload() {
        if (!selected1) {
            Toast.makeText(this, "请上传银行卡正面照片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selected2) {
            Toast.makeText(this, "请上传银行卡反面照片", Toast.LENGTH_SHORT).show();
            return;
        }

        et_cradNoStr = et_cradNo.getText().toString();
        if (TextUtils.isEmpty(et_cradNoStr)) {

            Toast.makeText(this, "银行卡号不能为空!", Toast.LENGTH_SHORT).show();
            return;

        }

        if(action.equals("2")){
            certificateNo = et_id.getText().toString();
            if (TextUtils.isEmpty(certificateNo)) {
                Toast.makeText(this, "身份证号不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if (txt_branch.getText().toString().length() == 0) {
            T.ss("请输入支行名称！");
            return;
        }

        provId = txt_province.getHint().toString();
        cityId = txt_city.getHint().toString();
        if (TextUtils.isEmpty(cityId)) {

            Toast.makeText(this, "开户城市不能为空 ！", Toast.LENGTH_SHORT).show();
            return;

        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("operType", type);//1-银行卡绑定；2-银行卡信息修改；3-设为默认银行卡，4-解绑(删除)
        params.put("cardNo", et_cradNoStr);
        params.put("provinceId", provId);// 所在省ID
        params.put("cityId", cityId);// 所在市ID
        params.put("cnapsCode", bankbankId);//联行号暂时为空
        params.put("subBranch", bankbankName);
        if(action.equals("2")){
            params.put("certificateNo", certificateNo);
        }

        MyHttpClient.post(this, Urls.BANKCARD_EDIT, params, cardFront, cardBack,
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
                                        BindBankCardActivity.this,
                                        "已提交审核");
                                MApplication.getInstance()
                                        .refreshUserInfo();
                                User.cardBundingStatus = 1;
                                finish();
                            } else {
                                T.ss(r.getMsg());
                            }
                        } catch (JSONException e) {
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
                        showLoadingDialog("正在上传信息，请耐心等待！");
                        firstArrayList = null;
                    }

                    @Override
                    public void onFinish() {
                        dismissLoadingDialog();
                    }

                });

    }

    @Override
    public void getBankName(String bankCardList) {
        showBankListDialog.dismiss();
        txt_bank.setText(bankCardList);

    }
}
