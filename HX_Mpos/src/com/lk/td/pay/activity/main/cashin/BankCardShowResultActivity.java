package com.lk.td.pay.activity.main.cashin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.pay.activity.newland.BuletootchController;
import com.hx.pay.activity.newland.BuletootchControllerImpl;
import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.MainTabActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.PosData;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.BitmapUtil;
import com.pay.library.uils.FileUtil;
import com.pay.library.uils.ImageUtils;
import com.pay.library.uils.StringUtils;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONException;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zhenglibin on 16/3/3.
 * 
 * 银行卡扫描结果
 */
public class BankCardShowResultActivity extends BaseActivity implements View.OnClickListener {

//    private static final int resultBitmapOfW = 400;
//    private static final int resultBitmapOfH = 80;

    private final int ADD_ID_CARD = 101;
    private ImageView cardNo;
    private EditText edt_card_no;
    private Button btn_back;
    private TextView tv_title;
    private Button btn_re_scan;
    private Button btn_edit;
    private Button btn_commit;
    private int resultBitmapArray[];
    private int BankAPP;
    private String placeActivity;
    private String CountStrs;
    private String bankCardImage;
    private int success;
    private char[] results;
    private String mCard;

    private Bitmap ResultBitmap;

    private Boolean flag = false;

    private String scanornot = "1";  //卡号输入模式，默认为1   表示扫描返回 2  手动输入
    private String type;
    private boolean addImage = false;
    private boolean saveImage = true;
    private boolean firstCamera = false;
    private BuletootchController controller = BuletootchControllerImpl.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_show_result);
        initData();
        initView();
        setData();
        judgeCamera();
    }

    private void judgeCamera() {
        if (bankCardImage.isEmpty() && bankCardImage.length() == 0) {
            new CustomDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("扫描银行卡已超时，请拍摄上传银行卡照片")
                    .setOkBtn("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takePicture(ADD_ID_CARD, mCard);
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    private void initData() {
        Intent intent = getIntent();
        resultBitmapArray = intent.getIntArrayExtra("PicR");
        results = intent.getCharArrayExtra("StringR");
        success = intent.getIntExtra("Success", 0);
        BankAPP = intent.getIntExtra("BankAPP", -1);
        type = intent.getStringExtra("TYPE");
        placeActivity = intent.getStringExtra("Action");
        CountStrs = intent.getStringExtra("CountStrs");
        bankCardImage = intent.getStringExtra("bankCardImage");
    }

    private void initView() {
        cardNo = (ImageView) this.findViewById(R.id.iv_bank_card);
        if (bankCardImage.isEmpty() && bankCardImage.length() == 0)
            cardNo.setOnClickListener(this);
        edt_card_no = (EditText) this.findViewById(R.id.edt_card_no);
        edt_card_no.setKeyListener(null);
        btn_re_scan = (Button) this.findViewById(R.id.btn_re_scan);
        btn_re_scan.setOnClickListener(this);
        btn_edit = (Button) this.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        btn_commit = (Button) this.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("扫描结果");
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        mCard = FileUtil.getTdPath(this) + "mCard.jpg";
    }

    private void setData() {
        if (results != null) {
            String resultS = String.valueOf(results).trim();
            edt_card_no.setText(resultS);
        }

        if (bankCardImage != null && !bankCardImage.isEmpty()) {
            ResultBitmap = BitmapFactory.decodeFile(bankCardImage);
            cardNo.setImageBitmap(ResultBitmap);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ResultBitmap != null) {
            ResultBitmap.recycle();
            ResultBitmap = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bank_card:   //扫描返回的图片
                takePicture(ADD_ID_CARD, mCard);
                break;
            case R.id.btn_back:
                if (null != controller){
                    controller.destroy();
                }
                finish();
                break;
            case R.id.btn_re_scan:  //重新扫描
                Intent intentTack = new Intent(BankCardShowResultActivity.this, BankScanCamera.class);
                intentTack.putExtra("BankAPP", 10011);
                startActivity(intentTack);
                finish();
                break;
            case R.id.btn_edit:   //30s扫描超时，点击启手动输入卡号
                System.out.println("开启软键盘");
                scanornot = "2";
                edt_card_no.setFocusable(true);
                edt_card_no.setFocusableInTouchMode(true);
                edt_card_no.requestFocus();
                edt_card_no.setSelection(edt_card_no.getText().length());
                edt_card_no.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btn_commit:    //开始上传扫描或输入的图片
                CharSequence cardnum = edt_card_no.getText();
                if (TextUtils.isEmpty(cardnum)) {
                    Toast.makeText(BankCardShowResultActivity.this, "请重新扫描银行卡或者手动输入卡号", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (StringUtils.isEmpty(bankCardImage)) {
                    Toast.makeText(BankCardShowResultActivity.this, "请重新扫描银行卡或者手动输入卡号", Toast.LENGTH_SHORT).show();
                    break;
                }
                uploadImage(PosData.getPosData().getPrdordNo(), bankCardImage, cardnum.toString(), scanornot);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String pathStr = null;
//        if (requestCode == ADD_ID_CARD && resultCode == RESULT_OK) {
        if (requestCode == ADD_ID_CARD && resultCode == RESULT_OK) {
            setBitmapToImageView(mCard, cardNo, 1);
            pathStr = mCard;
            addImage = true;
        }

        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            showLoadingDialog();
            ImageUtils.saveFile(pathStr, 800, 640);
            dismissLoadingDialog();
        } catch (Exception e) {
            Toast.makeText(BankCardShowResultActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            saveImage = false;
            e.printStackTrace();
        }
        if (saveImage) {
            bankCardImage = mCard;
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
     * 上传扫描的银行卡及其信息
     * @param prdordNo  订单号
     * @param signPath  扫描图片文件路径
     * @param cardnum   卡号
     * @param scanornot  卡号输入类型
     */
    private void uploadImage(String prdordNo, final String signPath, final String cardnum, final String scanornot) {
        HashMap<String, String> params = new HashMap<>();
        params.put("prdordNo", prdordNo);
        params.put("cardnum", cardnum);
        MyHttpClient.post(BankCardShowResultActivity.this,
                Urls.UPLOAD_BANK_CARD_IMAGE, params, signPath,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode,
                                          Header[] headers, byte[] responseBody) {
                        Logger.json("[===]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(
                                    responseBody).getResult();
                            if (r.isSuccess()) {
                                if (!TextUtils.isEmpty(type)) {
//                                    if (type.equals("1")) {
                                        startActivity(new Intent(
                                                        BankCardShowResultActivity.this,
                                                        CashInConfirmActivity.class)
                                                        .setAction(CashInConfirmActivity.ACTION_CASH_IN)
                                                        .putExtra("scanornot", scanornot)
                                                        .putExtra("scancardnum", cardnum)
                                        );
//                                    } else {
//                                        startActivity(new Intent(
//                                                BankCardShowResultActivity.this,
//                                                MainTabActivity.class));
//                                    }
                                }
                                File file = new File(signPath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                Toast.makeText(BankCardShowResultActivity.this, r.getMsg(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Intent it = new Intent(BankCardShowResultActivity.this,
                                        ShowMsgActivity.class);
                                it.setAction("ACTION_CASH_IN");
                                it.putExtra("code", r.isSuccess());
                                it.putExtra("msg", r.getMsg());
                                startActivity(it);
                                finish();
                            }
                        } catch (JSONException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode,
                                          Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        networkError(statusCode, error);
                    }

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        showLoadingDialog("正在上传银行卡照片...");
                    }

                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (null != controller){
                controller.destroy();
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
