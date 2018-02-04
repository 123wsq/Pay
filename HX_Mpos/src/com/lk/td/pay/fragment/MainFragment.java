package com.lk.td.pay.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hx.pay.activity.newland.SwingXDLCardBalanceActivity;
import com.hx.pay.activity.ty.SwingCardByTYBluetootchActivity;
import com.hx.view.bean.CardBean;
import com.hx.view.wedget.flashview.EffectConstants;
import com.hx.view.wedget.flashview.FlashView;
import com.hx.view.widget.CustomDialog;
import com.lk.td.pay.activity.NoticeActivity;
import com.lk.td.pay.activity.main.cashin.CashInActivity;
import com.lk.td.pay.activity.main.cashin.CheckActivity;
import com.lk.td.pay.activity.main.cashin.ShowMsgActivity;
import com.lk.td.pay.activity.account.AccountWithdrawActivity;
import com.lk.td.pay.activity.account.LoginActivity;
import com.lk.td.pay.activity.account.RealNameAuthenticationActivity;
import com.lk.td.pay.activity.main.quickpay.BankSelectActivity;
import com.lk.td.pay.adapter.MainDataAdapter;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.beans.MainDataBean;
import com.lk.td.pay.beans.PosData;
import com.lk.td.pay.golbal.MApplication;
import com.lk.td.pay.utils.T;
import com.pay.code.activity.CaptureActivity;
import com.pay.code.activity.CreateQRCodeActivity;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.BindDeviceInfo;
import com.pay.library.bean.PosRate;
import com.pay.library.bean.User;
import com.pay.library.config.Actions;
import com.pay.library.config.AppConfig;
import com.pay.library.config.Urls;
import com.pay.library.request.BasicRequest;
import com.pay.library.request.IntentParams;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.HStartActivity;
import com.td.app.pay.hx.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends BaseFragment implements OnClickListener,AdapterView.OnItemClickListener {


    private View view;
    private FlashView flash;
    private GridView grid_main;
    private MainDataAdapter mAdapter;
    private List<String> imgs = new ArrayList<String>();
    private final String DATA_PATH = "data/main";
    private List<MainDataBean> mData = new ArrayList<MainDataBean>();
    private final int COLUMNS = AppConfig.COLUMNS;
    List<BindDeviceInfo> devices = new ArrayList<BindDeviceInfo>();
    CustomDialog dialog;
    private List<CardBean> listType;
    private TextView tv_title;
    private ImageButton notice_main;
    private MyBroadcastReceiver myBroadcastReceiver;
    private boolean isDialogShow = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_layout, null);
        return  view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadNotice();
        loadAuth();
        getAdUrl();

        getData();
        listType = MApplication.getInstance().getCardType();

        onRegister();
    }

    private void initView() {
        grid_main = (GridView) view.findViewById(R.id.grid_main);
        grid_main.setNumColumns(COLUMNS);
        mAdapter = new MainDataAdapter(getActivity(), mData, COLUMNS);
        grid_main.setAdapter(mAdapter);
        grid_main.setOnItemClickListener(this);

        flash = (FlashView) view.findViewById(R.id.cycleview_main);
        flash.setImageUris(imgs);
        flash.setEffect(EffectConstants.DEFAULT_EFFECT);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.app_name));
        notice_main = (ImageButton) view.findViewById(R.id.notice_main);
        notice_main.setVisibility(View.VISIBLE);
        notice_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.notice_main:   //公告
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取首页轮播图路径
     */
    private void getAdUrl() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("custId", User.uId);
        params.put("custMobile", User.uAccount);
        MyHttpClient.post(getActivity(), Urls.MAIN_AD_IMG,
                params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        Logger.json(responseBody);
                        try {
                            BasicResponse json = new BasicResponse(responseBody)
                                    .getResult();
                            if (json.isSuccess()) {
                                JSONArray array = json.getJsonBody()
                                        .optJSONArray("imgList");
                                for (int i = 0; i < array.length(); i++) {
                                    String temp = array.getJSONObject(i)
                                            .optString("appimgPath");
                                    if (!TextUtils.isEmpty(temp)) {
                                        imgs.add(AppConfig.HOST + temp);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        initView();
                    }
                });
    }
    private void bindDevice() {

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_card_type));
        builder.setListView(listType, cardItemClickLisener);
        dialog =  builder.create();
        dialog.show();
    }
    AdapterView.OnItemClickListener cardItemClickLisener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            CardBean card = listType.get(position);
            Intent intent = new Intent();
            switch (card.getId()){
                case 1:
                    intent.setClass(getActivity(),SwingXDLCardBalanceActivity.class);
                    intent.setAction(Actions.ACTION_QUERY_BALANCE);
                    break;
                case 2:
                case 4:
                    PosData.getPosData().setActtext("ACTION_QUERY_BALANCE");
                    intent.setClass(getActivity(), CheckActivity.class);
                    intent.setAction(Actions.ACTION_QUERY_BALANCE);
                    intent.putExtra(CheckActivity.TYPE, CheckActivity.TYPE_CONN);
                    intent.putExtra(CheckActivity.SWING_TYPE,
                            card.getId()==2
                                    ?
                                    CheckActivity.SWING_XDL_BLUETOOTH
                                    :
                                    CheckActivity.SWING_XDL_KEYMAP);
                    MApplication.getInstance().setKeymap(
                            card.getId()==2
                                    ?
                              false : true);
                    break;
                case 3:

                    PosData.getPosData().setActtext("ACTION_QUERY_BALANCE");
                    intent.setClass(getActivity(),SwingCardByTYBluetootchActivity.class);
                    intent.setAction(Actions.ACTION_QUERY_BALANCE);

                    break;
                default:
                    break;
            }
            startActivity(intent);


            dialog.dismiss();
        }
    };

    /**
     * 加载首页公告通知
     */
    private void loadNotice() {
        HashMap<String, String> params = new HashMap<>();
        params.put("pageSize", 10 + "");
        params.put("start", "0");
        params.put("noticeStatus", "1");  //紧急公告
        MyHttpClient.post(context, Urls.SYSTEM_MESSAGE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        String response = new String(responseBody);
                        Logger.json(response);

                        try {
                            BasicResponse result = new BasicResponse(responseBody).getResult();
                            if (result.isSuccess()) {
                                JSONObject jsonOneBody = result.getJsonBody();
                                JSONArray array = jsonOneBody.getJSONArray("noticeList");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject temp = array.getJSONObject(i);
                                    String noticeId = temp.optString("noticeId");
                                    String message = temp.optString("noticeBody");
                                    String noticeTitle = temp.optString("noticeTitle");
                                    showNoticeDialog(noticeId, noticeTitle, message);
                                }
                            } else {
                                showToast(result.getMsg());
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

    private void showNoticeDialog(final String noticeId, String noticeTitle, String message) {
    	
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(noticeTitle);
        builder.setMessage(message);
        builder.setOkBtn(getString(R.string.no_tips), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMessage(User.uId, noticeId);
                dialog.dismiss();
            }
        });
        builder.setCancelBtn(getString(R.string.go_to), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void setMessage(String id, String noticeId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("Id", id);
        params.put("noticeId", noticeId);
        MyHttpClient.post(context, Urls.SET_MESSAGE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        String response = new String(responseBody);
                        Logger.json(response);
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

    public void getUserInf(String url){
    	
    	 HashMap<String, String> map = new HashMap<String, String>();
         map.put("custMobile", User.uAccount);
        
         BasicRequest.sendRequest(getActivity(), url, map, handler, false);
    	
    }
    
    
    private void loadAuth(){
        if (User.uStatus == 0) {
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.tip));
            builder.setMessage(getString(R.string.no_authentication));
            builder.setOkBtn(getString(R.string.go_to_authentication),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {

                            getActivity().startActivity(
                                    new Intent(getActivity(),
                                            RealNameAuthenticationActivity.class));
                            dialog.cancel();
                        }
                    });
            CustomDialog dialog = builder.create();
            dialog.show();
            return;
        }
    }

    /**
     * 获取主界面显示的数据
     */
    public void getData(){

        try {
           InputStream is =  getActivity().getResources().getAssets().open(DATA_PATH);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader buffReader = new BufferedReader(reader);
           String content = "";
            String line = "";
            while ( (line = buffReader.readLine())!=null){
                content += line;
            }

            JSONObject jsonObject =  new JSONObject(content);

            JSONArray jsona = jsonObject.getJSONArray("data");


            for (int i = 0 , len = jsona.length(); i<len ; i++){
                JSONObject json = jsona.getJSONObject(i);
                MainDataBean data = new MainDataBean();
                data.setId(json.optInt("id", 0));
                data.setName(json.getString("name"));
                data.setState(json.optBoolean("state",true));
                data.setImgPath(json.optString("url"));
                data.setEnable(json.optBoolean("enable", false));
                if (data.isState()){
                    mData.add(data);
                }
            }

            if (mData.size() %COLUMNS !=0){

                for (int j = 0, len = COLUMNS - mData.size() % COLUMNS; j < len; j++){
                    MainDataBean data = new MainDataBean();
                    data.setImgPath("assets://image/not_use.png");
                    data.setId(-1);
                    mData.add(data);
                }
            }
            if (mAdapter!=null){
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        MainDataBean data = mData.get(position);
        int id = data.getId();
        Intent intent = new Intent();
        if (!data.isEnable()){
            T.ss(getString(R.string.app_close));
            return;
        }
        getUserInf(Urls.GET_USER_INFO);
        
        switch (id){
            case 1: //手机收款
            	
                if (MApplication.getInstance().chechStatus())//实名认证
                    if (User.termNum == 0) {//绑定刷卡器
                        T.ss(getString(R.string.unbind_card));
                        return;
                    } else {
                    	PosData.getPosData().setPayType(AppConfig.PAYTYPE.TERM);
                        intent.setClass(getActivity(), CashInActivity.class).setAction(CashInActivity.ACTION_T1);
                        intent.putExtra(IntentParams.PAY_TYPE, AppConfig.PAYTYPE.TERM);
                        startActivity(intent);
                    }
                else {
                    return;
                }
                break;
            case 2://实时提现
                if (User.cardBundingStatus != 2) {
                    com.lk.td.pay.utils.T.ss(getString(R.string.unbind_card_pass));
                    return;
                }
                if (MApplication.getInstance().chechStatus()) {
                    intent.setClass(getActivity(), AccountWithdrawActivity.class);
                    startActivity(intent);
                }
                break;
            case 5:
                if (MApplication.getInstance().chechStatus())//实名认证
                    if (User.termNum == 0) {//绑定刷卡器
                        T.ss(getString(R.string.unbind_card));
                        return;
                    } else {
                        bindDevice();
                    }
                else {
                    return;
                }
                break;
            case 13: // 快捷支付

                intent.setClass(getActivity(), BankSelectActivity.class);
                startActivity(intent);
                break;
            case 14:  //扫码收款
            	  if (User.uStatus !=0){//实名认证
		            	PosData.getPosData().setPayType(AppConfig.PAYTYPE.QR_CODE);
		            	intent.setClass(getActivity(), CashInActivity.class);
		                intent.putExtra(IntentParams.PAY_TYPE, AppConfig.PAYTYPE.QR_CODE);
		                startActivity(intent);
            	  }else{
            		  T.ss("请先实名认证");
            	  }
            	break;
            case 15:
            	intent.setClass(getActivity(), CaptureActivity.class);
            	startActivity(intent);
            	break;
            default:
            	
            	break;
        }

    }
    
    
    public void onRegister(){
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(Actions.ACTION_LOGIN);
    	filter.addAction(Actions.ACTION_QR_CODE);
    	filter.addAction(Actions.ACTION_DIALOG);
    	filter.addAction(Actions.ACTION_GET_CODE);
    	if (myBroadcastReceiver==null) {
    		myBroadcastReceiver = new MyBroadcastReceiver();
		}
    	getActivity().registerReceiver(myBroadcastReceiver, filter);
    }
    
    public class MyBroadcastReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d("action", "ACTION====="+action);
			if (!TextUtils.isEmpty(action)) {
				Intent it = new Intent();
				if (action.trim().equals(Actions.ACTION_LOGIN)) {
					
					it.setClass(getActivity(), LoginActivity.class);
					it.setAction(intent.getStringExtra(IntentParams.ACTION));
					startActivity(it);
				}else if(action.trim().equals(Actions.ACTION_QR_CODE)){
					
					Log.d("===qr====", intent.getStringExtra(IntentParams.DATA));
					showDefaultDialog("扫描结果", intent.getStringExtra(IntentParams.DATA), true);
//					T.sl(intent.getStringExtra(IntentParams.DATA));
					
				}else if(action.trim().equals(Actions.ACTION_DIALOG)){
					
					String title = intent.getStringExtra(IntentParams.DIALOG_TITLE);
					String message = intent.getStringExtra(IntentParams.DIALOG_MSG);
					showDefaultDialog(title, message, false);
				}else if(action.trim().equals(Actions.ACTION_GET_CODE)){
					Map<String, Object> map = new HashMap<String, Object>();
                 	map.put(IntentParams.CODE, intent.getBooleanExtra(IntentParams.CODE, false));
                 	map.put(IntentParams.MSG,  intent.getStringExtra(IntentParams.MSG));
                 	
                 	HStartActivity.startActivity(getActivity(), ShowMsgActivity.class, map, Actions.ACTION_GET_CODE);
				}
			}
		}
	};
	
	public void showDefaultDialog(String title, final String message, boolean isOpen){
		if (!isDialogShow) {
		
			CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
			builder.setTitle(title);
			builder.setMessage(message);
			if (isOpen) {
				builder.setCancelBtn("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						isDialogShow = false;
					}
				});
				if (!TextUtils.isEmpty(message) && message.startsWith("http://")) {
				
					builder.setOkBtn("打开", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							isDialogShow = false;
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
							getActivity().startActivity(intent);
							dialog.dismiss();
						}
					});
				
				}
			
			}else {
				builder.setOkBtn("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						isDialogShow = false;
						HStartActivity.startActivity(getActivity(), LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
						
					}
				});
			}
			CustomDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			isDialogShow = true;
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (myBroadcastReceiver!= null) {
			getActivity().unregisterReceiver(myBroadcastReceiver);
		}
	}
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BasicRequest.FLAG_REQUEST_SUCCESS:
				String data = (String) msg.getData().getString(BasicRequest.KEY_DATA);
				JSONObject json;
				try {
					json =  new JSONObject(data);
					if (msg.getData().getString(BasicRequest.KEY_URL).equals(Urls.GET_USER_INFO)) {
						
						  User.cardNum = json.optInt("cardNum");
                          User.termNum = json.optInt("termNum");
                          User.uStatus = json.optInt("custStatus");
                          User.cardBundingStatus = json.optInt("cardBundingStatus");
                          User.macAddress = json.optString("macAddress");//蓝牙地址
                          getUserInf(Urls.BIND_DEVICE_LIST);
					}else if(msg.getData().getString(BasicRequest.KEY_URL).equals(Urls.BIND_DEVICE_LIST)){
						 JSONArray array = json.optJSONArray("termList");
						 
						 for (int i = 0; i < array.length(); i++) {
                             BindDeviceInfo d = new BindDeviceInfo();
                             d.setAgentId(array.optJSONObject(i)
                                     .optString("agentId"));
                             d.setTermNo(array.optJSONObject(i)
                                     .optString("termNo"));
                             
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
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			case BasicRequest.FLAG_REQUEST_FAIL:

			default:
				break;
			}
		};
	};
	
}
