package com.lk.td.pay.activity.main.quickpay;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.annotation.ContentView;
import com.android.annotation.ViewInject;
import com.android.annotation.event.OnClick;
import com.hx.view.wedget.password.CustomPasswordDialog;
import com.hx.view.wedget.password.GridPasswordView;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.utils.T;
import com.pay.library.uils.AmountUtils;
import com.pay.library.uils.ViewUtils;
import com.td.app.pay.hx.R;

/**
 * Created by wsq on 2016/5/20.
 */
@ContentView(R.layout.layout_quick_pay)
public class QuickPayActvity extends BaseActivity{

    @ViewInject(R.id.btn_back)Button btn_back;
    @ViewInject(R.id.btn_GetVerify)Button btn_GetVerify;
    @ViewInject(R.id.et_amt)EditText et_amt;
    @ViewInject(R.id.et_code)EditText et_code;

    public static final String PHONE_NUM = "PHONE_NUM";
    private String phoneNum;
    private SmsCodeCount smsCodeCount;
    private CustomPasswordDialog passwordDialog;

    private String beginChangeStr = "";
    private  int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);
        init();
    }

    public void init(){
        //获取手机号码
        phoneNum = getIntent().getStringExtra(PHONE_NUM);
        btn_back.setVisibility(View.VISIBLE);
        smsCodeCount = new SmsCodeCount(60*1000, 1000);

        et_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beginChangeStr = charSequence.toString();
                index = et_amt.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (TextUtils.isEmpty(s)){
                    return;
                }
                try{
                    Double.parseDouble(s);
                }catch (Exception e){
                    et_amt.setText(beginChangeStr);
                    et_amt.setSelection(index > 0 ? index-1 : 0 );
                }
            }
        });

    }




    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClickListner(View view){
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:

                onValidate();
                break;
            default:

                break;

        }
    }



    public void onValidate(){

        String amt = et_amt.getText().toString();
        String code = et_code.getText().toString();

        if (TextUtils.isEmpty(code) || code.length()!=6){

            T.ss("请输入六位的验证码");
            return;
        }
        if (!TextUtils.isEmpty(amt) && Double.parseDouble(amt) >= 1.00){
            getPassword(AmountUtils.changeY2Y(amt));
        }else {
            Toast.makeText(this, "请输入正确的提现金额,至少1元", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public void getPassword(String amt){
        CustomPasswordDialog.Builder builder = new CustomPasswordDialog.Builder(this);
        View passwordView = LayoutInflater.from(this).inflate(R.layout.layout_password_popup,  null);
        builder.setTitle("输入密码")
                .setShowBtn(false)
                .setMessage(passwordView);
        passwordDialog = builder.create();
        passwordDialog.show();

        final GridPasswordView gridPasswordView = (GridPasswordView) passwordView.findViewById(R.id.gridViewPassword);
        TextView text_amt = (TextView) passwordView.findViewById(R.id.text_amt);
        text_amt.setText(amt);
        gridPasswordView.setFocusable(true);

        gridPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
//                confirmWithdraw(psw); // 提现
                T.ss("支付成功");
                gridPasswordView.clearPassword();
                passwordDialog.dismiss();
                finish();
            }
        });
    }


    /**
     * 获取短信验证之后的倒计时显示
     */
    class SmsCodeCount extends CountDownTimer{

        public SmsCodeCount(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {

            btn_GetVerify.setText((millisUntilFinished/1000) + "秒");
            btn_GetVerify.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btn_GetVerify.setText("再次获取");
            btn_GetVerify.setEnabled(true);
        }
    }

    /**
     * 在销毁界面的时候，取消倒计时显示的线程
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != smsCodeCount){
            smsCodeCount.cancel();
        }
    }
}
