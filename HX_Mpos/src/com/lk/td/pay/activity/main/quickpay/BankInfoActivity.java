package com.lk.td.pay.activity.main.quickpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.annotation.ContentView;
import com.android.annotation.ViewInject;
import com.android.annotation.event.OnClick;
import com.android.annotation.event.OnTouch;
import com.hx.view.wedget.wheelview.view.DATE_TYPE;
import com.hx.view.wedget.wheelview.view.DateSelectorLayout;
import com.hx.view.widget.CustomPopupWindow;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.main.cashin.SignaturePadActivity;
import com.lk.td.pay.activity.more.ProtocolActivity;
import com.lk.td.pay.utils.T;
import com.pay.library.request.ParamsUtils;
import com.pay.library.uils.ExpresssoinValidateUtil;
import com.pay.library.uils.IDCardValidateTool;
import com.pay.library.uils.ViewUtils;
import com.td.app.pay.hx.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wsq on 2016/5/23.
 */
@ContentView(R.layout.layout_protocol_auth)
public class BankInfoActivity extends BaseActivity{

    @ViewInject(R.id.btn_back)Button btn_back;
    @ViewInject(R.id.tv_cardNo)TextView tv_cardNo;
    @ViewInject(R.id.et_period)TextView et_period;
    @ViewInject(R.id.cb_select_protocol)CheckBox cb_select_protocol;

    @ViewInject(R.id.et_cvv)EditText et_cvv;
    @ViewInject(R.id.et_cradNo)EditText et_cradNo;
    @ViewInject(R.id.et_Mobile)EditText et_Mobile;
    @ViewInject(R.id.auth_protocol)LinearLayout auth_protocol;
    @ViewInject(R.id.protocol_layout)LinearLayout protocol_layout;
    private boolean isAuth = false; //表示是否验证
    private String  cvv, period;
    private CustomPopupWindow dateSelector;
    private Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);
        init();
    }

    public void init(){
        mContext =this;
        btn_back.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        tv_cardNo.setText(intent.getStringExtra(ParamsUtils.CARD_NO));
        isAuth = intent.getBooleanExtra("isAuth", false);
        auth_protocol.setVisibility(isAuth ? View.GONE : View.VISIBLE);

    }

    @OnClick({R.id.btn_back,
            R.id.btn_protocol_next,
            R.id.tv_selector_protocol})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_protocol_next:
                verifyContent();
                break;
            case R.id.tv_selector_protocol:
                startActivity(new Intent(BankInfoActivity.this, ProtocolActivity.class));
                break;
        }
    }

    /**
     * 验证输入的内容
     */
    public void verifyContent(){

        //验证输入的身份证号是否正确
        String cardNo =et_cradNo.getText().toString();
        if(!IDCardValidateTool.validateCard(cardNo)){
            T.ss("请输入正确的身份证号码");
            return ;
        }
        //验证输入的手机号码是否正确
        String mobile  =et_Mobile.getText().toString();
        if (!ExpresssoinValidateUtil.isMobilePhone(mobile)){
            T.ss("请输入正确的电话号码");
            return;
        }

        if (isAuth){
            startActivity(new Intent(BankInfoActivity.this, QuickPayActvity.class));
        }else{
            //在此处需要验证，生成协议信息
            //发送短信消息
        }

        //输入的cvv
        cvv = et_cvv.getText().toString();
        if (TextUtils.isEmpty(cvv)){
            T.ss("CVV码不能为空！");
            return;
        }
        if (cvv.trim().length()!=3){
            T.ss("请输入3位的CVV");
            return;
        }


        //输入的有效期
        period = et_period.getText().toString();

        if (TextUtils.isEmpty(period)){
            T.ss("有效期限不能为空！");
            return ;
        }
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = format.parse(period);
        } catch (ParseException e) {
            e.printStackTrace();
            T.ss("日期格式错误!");
            return ;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //获取当前年月
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH)+1;
        //获取选择的年月
        int selYear = c.get(Calendar.YEAR);
        int selMonth = c.get(Calendar.MONTH)+1;
        if (curYear > selYear){
            T.ss("银行卡已失效");
            return;
        }else if(curYear == selYear){

            if (curMonth >= selMonth){
                T.ss("银行卡已失效");
                return;
            }
        }

        boolean selector = cb_select_protocol.isChecked();

        if (!selector){
            T.ss("请查看协议");
            return ;
        }
        Intent intent = new Intent(this, SignaturePadActivity.class);
        intent.putExtra(SignaturePadActivity.TYPE, SignaturePadActivity.TYPE_QUICK_PAY);
        startActivity(intent);

    }

    /**
     * 显示日期选择器
     */
    public void showPopup(){

        Log.d("","========您点击了====");


        if (dateSelector == null) {
            DateSelectorLayout layout = new DateSelectorLayout(mContext);
            layout.setType(DATE_TYPE.YEAR_MONTH);
            layout.setCycleEnable(true);
            layout.setLableColor(Color.parseColor("#5381F7"));
            layout.setSelectColor(Color.RED);
            layout.setSelectSize(25);
            layout.setShowLable(true);
            layout.setVisivilityItems(3);
            layout.initView();
            dateSelector = new CustomPopupWindow(mContext,
                    layout,
                    "确定","",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            et_period.setText(DateSelectorLayout.SELECTOR_YEAR+"-"+ DateSelectorLayout.SELECTOR_MONTH);
                            dateSelector.dismiss();
                        }
                    });

        }

        dateSelector.showAtLocation(protocol_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnTouch({R.id.et_period})
    public boolean onTouch(View view, MotionEvent event){
        if (view.getId() == R.id.et_period){

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    hideKeyMap();
                    showPopup();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;

            }
        }

        return true;
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyMap(){
        et_period.setFocusable(false);
        et_period.setFocusableInTouchMode(false);
        View view = getWindow().peekDecorView();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}