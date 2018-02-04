package com.lk.td.pay.activity.account;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.view.wedget.password.CustomPasswordDialog;
import com.hx.view.wedget.password.GridPasswordView;
import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BankCardItem;
import com.pay.library.bean.User;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.request.ParamsUtils;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.AmountUtils;
import com.pay.library.uils.MD5Util;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AccountWithdrawActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener, OnTouchListener {

    EditText etWithdrawMoney, etPaypwd, etAccountName, etExplain;
    private CheckBox cb_account_card, cb_account_scran, cb_account_quick;
    private TextView tv_balance, tv_fee;
    private TextView tv_card_amount, tv_scan_amount, tv_quick_amount;
    private Button btn_back;
    private TextView tv_title;
    private String beginChangeStr = "";
    private  int index = 0;
    private String acctType = "";

    private CustomPasswordDialog passwordDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_withdraw);
        initView();
        queryBalance();
        if (TextUtils.isEmpty(User.cache_card)) {
            getBankCardStatus();
        } else {
            etAccountName.setText(User.cache_card + "");
        }
    }

    private List<BankCardItem> bankList = new ArrayList<BankCardItem>();

    private void getBankCardStatus() {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        MyHttpClient.post(this, Urls.GET_BANKCARD_LIST, requestMap,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[BankCardList]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                JSONArray array = r.getJsonBody().optJSONArray(
                                        "bankCardList");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject temp = array.getJSONObject(i);
                                    BankCardItem item = new BankCardItem();
                                    item.setCardName(temp.optString("issnam"));
                                    item.setCardNo(temp.optString("cardNo"));
                                    bankList.add(item);
                                }
                                if (bankList.size() > 0) {
                                    String temp = Utils.hiddenCardNo(bankList.get(0).getCardNo());
                                    etAccountName.setText(temp);
                                    User.cache_card = temp;
                                } else {
                                    etAccountName.setText("--");
                                }

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
                        networkError(statusCode, error);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
//							dismissLoadingDialog();
                    }
                });

    }


    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("账户提现");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setText("返回");
        btn_back.setOnClickListener(this);
        tv_balance = (TextView) findViewById(R.id.tv_balance_total);
        etAccountName = (EditText) findViewById(R.id.et_account_withdraw_name);
        etWithdrawMoney = (EditText) findViewById(R.id.et_account_withdraw_money);
        etPaypwd = (EditText) findViewById(R.id.et_account_withdraw_pay_pwd);
        tv_card_amount = (TextView) findViewById(R.id.tv_card_amount);
        tv_scan_amount = (TextView) findViewById(R.id.tv_scan_amount);
        tv_quick_amount = (TextView) findViewById(R.id.tv_quick_amount);
        
        cb_account_card = (CheckBox) this.findViewById(R.id.cb_account_card);
        cb_account_scran = (CheckBox) this.findViewById(R.id.cb_account_scran);
        cb_account_quick = (CheckBox) this.findViewById(R.id.cb_account_quick);
        
        cb_account_card.setOnCheckedChangeListener(this);
        cb_account_scran.setOnCheckedChangeListener(this);
        cb_account_quick.setOnCheckedChangeListener(this);
        
        findViewById(R.id.btn_account_withdraw_confirm).setOnClickListener(this);
        tv_fee = (TextView) findViewById(R.id.tv_loan_money);
        findViewById(R.id.btn_wd_calfee).setOnClickListener(this);

        etWithdrawMoney.setOnTouchListener(this);
        etWithdrawMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beginChangeStr = charSequence.toString();
                index = etWithdrawMoney.getSelectionStart();
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
                        Double dnum = Double.parseDouble(s);
                }catch (Exception e){
                    etWithdrawMoney.setText(beginChangeStr);
                    etWithdrawMoney.setSelection(index > 0 ? index-1 : 0 );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
    	String amt =  etWithdrawMoney.getText().toString();
        switch (v.getId()) {

            case R.id.btn_account_withdraw_confirm: //开始提现
            	calcFee(amt);
                break;
            case R.id.btn_wd_calfee:
                calcFee(amt);
                break;
            case  R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
    
    
    

 
    private void confirmWithdraw(String psw, String amt) {
       
       

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamsUtils.TX_AMT, AmountUtils.changeY2F(amt));
        // params.put("cardNo", "");
        params.put(ParamsUtils.CAS_TYPE, "00");
        params.put("payPwd", MD5Util.generatePassword(psw));
        params.put(ParamsUtils.ACC_TYPE, acctType);
        if (AppConfig.PAYTYPE.PAY_ACCOUNT.equals(acctType)) {
        	
        	params.put(ParamsUtils.AMT_ACCT_TYPE_02, "02");
        	params.put(ParamsUtils.AMT_ACCT_TYPE_03, "03");
        	params.put(ParamsUtils.AMT_ACCT_TYPE_04, "04");
		}else if (AppConfig.PAYTYPE.TERM.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_02, "02");
		}else if (AppConfig.PAYTYPE.QUICK.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_03, "03");
		}else if (AppConfig.PAYTYPE.QR_CODE.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_04, "04");
		}
        MyHttpClient.post(this, Urls.WITHFRAW, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[提现]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                T.showCustomeOk(AccountWithdrawActivity.this,
                                        "已提交审核", 3500);
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
                        networkError(statusCode, error);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });

    }


    private void queryBalance() {

        MyHttpClient.post(this, Urls.QUERY_BALANCE,
                new HashMap<String, String>(), new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[余额查询]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody)
                                    .getResult();
                            if (r.isSuccess()) {
                                JSONObject obj = r.getJsonBody();
                                User.amtT0 = AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT0));
                                User.amtT1 = AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1));
                                User.amtT1y = AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1_Y));
                                User.totalAmt = AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACBAL));
                                User.acT1AP = AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ACT1_AP)));
                                User.balance = AmountUtils.changeFen2Yuan(AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_ONCREDIT)));
                                User.freeze = AmountUtils.changeFen2Yuan( AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_FREEZE)));
                                User.reserveField = AmountUtils.changeFen2Yuan( AmountUtils.deletePoint(obj.optString(ParamsUtils.RESULT_RESERVEFIELD)));
                                User.acT1AP_ACT03 =AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT03));
                                User.acT1AP_ACT04 = AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT04));

                                //总余额
                                tv_balance.setText((getAmt(User.acT1AP, User.balance, User.freeze, User.reserveField)
                                		+Double.parseDouble(User.acT1AP_ACT03) +Double.parseDouble(User.acT1AP_ACT04)) + "元");
                                //刷卡余额
                                tv_card_amount.setText(getAmt(User.acT1AP, User.balance, User.freeze, User.reserveField)+"");
                                //快捷余额
                                tv_quick_amount.setText(AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT03)));
                                //扫码余额
                                tv_scan_amount.setText(AmountUtils.changeFen2Yuan(obj.optString(ParamsUtils.RESULT_ACT1AP_ACT04)));
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
                        networkError(statusCode, error);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }

                });

    }

    public Double getAmt(String acT1AP, String balance, String freeze, String reserveField ){

        if (TextUtils.isEmpty(acT1AP)){
            return  0.0;
        }
        double amtTotal = Double.parseDouble(acT1AP);
        double mbalance = 0;
        double mfreeze = 0;
        if (TextUtils.isEmpty(balance)){
            mbalance = 0;
        }else {
            mbalance = Math.abs(Double.parseDouble(balance));
        }
        if (TextUtils.isEmpty(freeze)){
            mfreeze = 0;
        }else {
            mfreeze = Math.abs(Double.parseDouble(freeze));
        }

        double amt = amtTotal - mbalance-mfreeze;

        return amt >= 0 ? amt : 0.0;
    }

    private void calcFee(final String inputAmt) {
    	
        if (TextUtils.isEmpty(inputAmt)) {
            Toast.makeText(this, "请输入提现金额", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double temp = 0;
        try {
            temp = Double.parseDouble(inputAmt);
            if (temp < 0) {
                T.ss("请输入正确的提现金额");
                return;
            }
           
        } catch (NumberFormatException e) {
            T.ss("金额输入有误");
            return;
        }
        
        double amt = 0;
        switch (getSelectorIndex()) {
		case 0:
			amt = Double.parseDouble(tv_card_amount.getText().toString());
			if (Double.parseDouble(inputAmt) > amt) {
				T.sl("余额不足");
				return;
			}
			break;
		case 1:
			amt = Double.parseDouble(tv_scan_amount.getText().toString());
			if (Double.parseDouble(inputAmt) > amt) {
				T.sl("余额不足");
				return;
			}
			break;
		case 2:
			amt = Double.parseDouble(tv_quick_amount.getText().toString());
			if (Double.parseDouble(inputAmt) > amt) {
				T.sl("余额不足");
				return;
			}
			break;

		default:
			break;
		}

        if (Double.parseDouble(inputAmt) < 1.00){
            Toast.makeText(AccountWithdrawActivity.this, "请输入正确的提现金额,至少1元",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final String psw = etPaypwd.getText().toString();
        
        if (psw.trim().length() < 6) {
            T.ss("支付密码最短为6位");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamsUtils.TX_AMT, AmountUtils.changeY2F(inputAmt));
        params.put(ParamsUtils.CAS_TYPE, AppConfig.CASTYPE.CAS_TYPE_UN);
        params.put(ParamsUtils.ACC_TYPE, acctType);
        
        if (AppConfig.PAYTYPE.PAY_ACCOUNT.equals(acctType)) {
        	params.put(ParamsUtils.AMT_ACCT_TYPE_02, "02");
        	params.put(ParamsUtils.AMT_ACCT_TYPE_03, "03");
        	params.put(ParamsUtils.AMT_ACCT_TYPE_04, "04");
		}else if (AppConfig.PAYTYPE.TERM.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_02, "02");
		}else if (AppConfig.PAYTYPE.QUICK.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_03, "03");
		}else if (AppConfig.PAYTYPE.QR_CODE.equals(acctType)) {
			params.put(ParamsUtils.AMT_ACCT_TYPE_04, "04");
		}
        MyHttpClient.post(this, Urls.CALC_FEE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json("[计算手续费]", responseBody);
                        try {
                            BasicResponse r = new BasicResponse(responseBody).getResult();
                            if (r.isSuccess()) {
                                int fee = r.getJsonBody().optInt("fee");
                                int service = r.getJsonBody().optInt("serviceFee");

//                                tv_fee.setText("￥" + AmountUtils.changeFen2Yuan((fee + service) + ""));
                                
                                showCustomDialog(AmountUtils.changeFen2Yuan((fee + service)+""), psw, inputAmt);
                            } else {
                                tv_fee.setText("计算失败...");
                                T.ss(r.getMsg());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        networkError(statusCode, error);
                        tv_fee.setText("计算失败...");
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        tv_fee.setText("计算中...");
                    }
                });
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
//                confirmWithdraw(psw, amt); // 提现
                gridPasswordView.clearPassword();
                passwordDialog.dismiss();
            }
        });

    }


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
		if (isChecked) {
			String amount = "";
			switch (buttonView.getId()) {
			case  R.id.cb_account_card:
				cb_account_scran.setChecked(false);
				cb_account_quick.setChecked(false);
				amount = tv_card_amount.getText().toString();
				acctType = AppConfig.PAYTYPE.TERM;
				break;
			case R.id.cb_account_scran:
				cb_account_card.setChecked(false);
				cb_account_quick.setChecked(false);
				amount = tv_scan_amount.getText().toString();
				acctType = AppConfig.PAYTYPE.QR_CODE;
				break;
			case R.id.cb_account_quick:
				cb_account_card.setChecked(false);
				cb_account_scran.setChecked(false);
				amount = tv_quick_amount.getText().toString();
				acctType = AppConfig.PAYTYPE.QUICK;
				break;
	
			default:
				break;
			}
			etWithdrawMoney.setText(amount);
		}else{
			etWithdrawMoney.setText("");
		}
	}
	
	
	public int getSelectorIndex(){
		
		if (cb_account_card.isChecked()) {
			return 0;
		}
		if (cb_account_scran.isChecked()) {
			return 1;
		}
		if (cb_account_quick.isChecked()) {
			return 2;
		}
		return -1;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		
		switch (v.getId()) {
		case R.id.et_account_withdraw_money:
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				
				if (getSelectorIndex() == -1) {
					T.sl("请选择提现类型");
					etWithdrawMoney.setFocusable(false);
					etWithdrawMoney.setFocusableInTouchMode(false);
				}else{
					etWithdrawMoney.setFocusable(true);
					etWithdrawMoney.setFocusableInTouchMode(true);
				}
				
				break;

			default:
				break;
			}
			
			
			break;

		default:
			break;
		}
		return false;
//			return onTouchEvent(event);
	}

	/**
	 * 显示获取到的费率
	 */
	public void showCustomDialog(String fee, final String psw, final String amt){
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您需要支付  "+fee+" 元的费率");
		builder.setOkBtn("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				 confirmWithdraw(psw, amt); // 提现
				 dialog.dismiss();
			}
		});
		builder.setCancelBtn("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
			}
		});
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
}
