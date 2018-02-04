package com.lk.td.pay.activity.main.quickpay;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.annotation.ContentView;
import com.android.annotation.ViewInject;
import com.android.annotation.event.OnClick;
import com.hx.view.bean.PopupItem;
import com.hx.view.widget.CustomDialog;
import com.hx.view.widget.CustomPopupWindow;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.pay.library.request.BasicRequest;
import com.pay.library.request.ParamsUtils;
import com.pay.library.uils.BankCardValidate;
import com.pay.library.uils.ViewUtils;
import com.td.app.pay.hx.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsq on 2016/5/13.
 */
@ContentView(R.layout.layout_bank_select)
public class BankSelectActivity extends BaseActivity{

    @ViewInject(R.id.btn_back) Button btn_back;
    @ViewInject(R.id.tv_title)TextView tv_title;
    @ViewInject(R.id.et_cradNo)EditText et_cradNo;
    @ViewInject(R.id.select_bank)LinearLayout select_bank;
    @ViewInject(R.id.txt_bank)TextView txt_bank;

    private static final int BANK_CARD_MIN = 13;
    private static final int BANK_CARD_MAX = 19;

    private CustomDialog dialog;
    private String cradNo;
    private List<PopupItem> mData;

    private CustomPopupWindow bankPopup;
    private BankSelectActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        init();
    }

    public void init(){
        tv_title.setText(getString(R.string.quick_pay));
        btn_back.setVisibility(View.VISIBLE);

        mData = new ArrayList<PopupItem>();

        mContext = this;

        getBankList();


    }

    @OnClick({  R.id.btn_back,                  //返回按钮
                R.id.btn_next,                  //下一步
                R.id.txt_bank                   //选择银行卡
            })
    public void onclickListener(View view ){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.txt_bank:
                bankPopup = new CustomPopupWindow(mContext, mData,"请选择银行类型", new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        txt_bank.setText(mData.get(position).getName());
                        bankPopup.dismiss();
                    }
                });
                bankPopup.showAtLocation(select_bank, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_next:
                verifyContent();
                break;
        }
    }


    public void verifyContent(){

    	String text_bank = txt_bank.getText().toString();
    	if (TextUtils.isEmpty(text_bank)) {
			T.ss("请选择银行卡");
    		return;
		}
    	
    	
        //输入的卡号
        cradNo =et_cradNo.getText().toString();

        //银行卡号验证
        if (TextUtils.isEmpty(cradNo)){
            T.ss("银行卡号不能为空！");
            return;
        }
        int length = cradNo.length();
        if (length < BANK_CARD_MIN || length > BANK_CARD_MAX){
            T.ss("银行卡号位数错误！");
            return ;
        }
        if (!BankCardValidate.validateCard(cradNo)){
            T.ss("请输入正确的银行卡号！");
            return ;
        }


        showDialog();

    }

    public void showDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("您在本应用中还未做过快捷支付， 请先审核协议")
                .setOkBtn("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent(mContext, BankInfoActivity.class);
                        intent.putExtra(ParamsUtils.CARD_NO, cradNo);
                        intent.putExtra("isAuth",false);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelBtn("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                finish();
            }
        });
        dialog = builder.create();

        dialog.show();

    }

    /**
     * 获取银行列表
     */
    public void getBankList(){

        try {
            InputStream is = getResources().getAssets().open("data/banklist");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String content = "";
            String line = "";

            while ((line = reader.readLine())!=null){
                content += line;
            }
            BasicResponse response = new BasicResponse(content.getBytes()).getResult();

            JSONArray jsona = response.getJsonBody().getJSONArray("BANKLIST");

            for (int i = 0, len = jsona.length(); i <len ; i++) {
                PopupItem item = new PopupItem();
                JSONObject json = jsona.optJSONObject(i);
                item.setId(i);
                item.setName(json.optString("bankName"));
                mData.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BasicRequest.FLAG_REQUEST_SUCCESS:

                    break;
//                case BasicRequest.FLAG_REQUEST_UNSUCCESS:

//                    break;
                case BasicRequest.FLAG_REQUEST_FAIL:

                    break;
            }
        }
    };




}
