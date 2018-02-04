/**
 * 功能：全局变量
 * 类名：TCPayApplication.java
 * 日期：2013-11-26
 * 作者：lukejun
 */
package com.lk.td.pay.golbal;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hx.view.bean.CardBean;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.T;
import com.lk.td.pay.utils.UnCeHandle;
import com.newland.mtype.module.common.iccard.ICCardSlot;
import com.newland.mtype.module.common.iccard.ICCardSlotState;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.SharedPref;
import com.pay.library.tool.store.Hawk;
import com.pay.library.tool.store.SharedPrefConstant;
import com.pay.library.uils.Logger;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lukejun
 * @ClassName: TCPayApplication
 * @Description: 全局变量
 * @date 2013-11-26 下午1:32:44
 */
public class MApplication extends Application implements UncaughtExceptionHandler {

    private static MApplication mApplication = null;
    public static SharedPref mSharedPref = null;
    public int screenWidth = 0;
    public int screenHeight = 0;
    private Context mainHomeContext = null;
    private User user = new User();

    private boolean isKeymap;
    private List<CardBean> cardList = null;
    private final String CARD_TYPE = "data/cards";


    private int Ic_pinInput_flag = 0; // 当前做出是否是IC外部输入密码模式，0否，1是
    private int Open_card_reader_flag = 0; // 当前操作是否是开启读卡器操作，0否，1是
    private BigDecimal amt;
    private byte[] result;
    private Map<ICCardSlot,ICCardSlotState> map=new HashMap<ICCardSlot, ICCardSlotState>(); //卡槽状态

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mainHomeContext = this;
        Hawk.init(mApplication);
        mSharedPref = SharedPref
                .getInstance(SharedPrefConstant.PREF_NAME, this); // 单例sp
        UnCeHandle unCatch = new UnCeHandle(this);
        Thread.setDefaultUncaughtExceptionHandler(unCatch);
        initImageLoader(getApplicationContext());
        cardList = new ArrayList<CardBean>();

        
        getCardData();
    }

    
    @Override
    protected void attachBaseContext(Context base) {
    	// TODO Auto-generated method stub
    	super.attachBaseContext(base);
    	
    	MultiDex.install(base);
    }
    
    public static MApplication getInstance() {
        /*if(null==mApplicationContext){
			mApplicationContext=new MApplication();
		}*/
        return mApplication;
    }

    /**
     * 获取主页面Context
     * 用于在activity外部启动或调用
     */
    public void setmainHomeContext(Context context) {
        this.mainHomeContext = context;
    }

    public Context getMainHomeContext() {
        return mainHomeContext;
    }

    public boolean chechStatus() {
        if (User.uStatus == 2) {
            return true;
        } else if (User.uStatus == 1) {
            T.ss("实名认证审核中");
            return false;
        } else if (User.uStatus == 0) {
            T.ss("尚未实名认证");
            return false;
        } else if (User.uStatus == 3) {
            T.ss("实名认证未通过");
            return false;
        }
        return false;
    }

    /**
     * 清除用户状态信息
     * <h1>不清除会导致登录用户状态错误</h1>
     */
    public void clearSavedInfo() {
        User.uAccount = null;
        User.login = false;
        User.uName = null;
        User.cardNum = 0;
        User.termNum = 0;
        User.uId = null;
        User.uStatus = 0;
        User.bindStatus = 0;
        User.amtT0 = null;
        User.amtT1 = null;
        User.bindDevices = null;
        User.totalAmt = null;
        User.cardInfo = null;
        //MApplication.mSharedPref.clear();
    }

    public void refreshUserInfo() {
        System.out.println("刷新用户信息");
        HashMap<String, String> params = new HashMap<String, String>();
        MyHttpClient.post(mApplication, Urls.GET_USER_INFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("刷新用户信息---");
                try {
                    BasicResponse r = new BasicResponse(responseBody).getResult();
                    if (r.isSuccess()) {
                        User.uStatus = r.getJsonBody().optInt("custStatus");
                        User.termNum = r.getJsonBody().optInt("termNum");
                        User.cardNum = r.getJsonBody().optInt("cardNum");
                        User.cardBundingStatus = r.getJsonBody().optInt("cardBundingStatus");
                    } else {
                    }
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                // TODO 自动生成的方法存根

            }
        });
    }
	

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Logger.d("Test", "system crush!!!");
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 获取卡头类型
     */
    private void getCardData(){
        try {
            InputStream is =  this.getResources().getAssets().open(CARD_TYPE);
            InputStreamReader  isReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isReader);
            String content = "";
            String line ="";
            while ((line = bufferedReader.readLine()) != null){
                content += line;
            }

            JSONObject jsonObject = new JSONObject(content);

            JSONArray jsona = jsonObject.getJSONArray("data");

            for (int i = 0 , len = jsona.length(); i< len; i++){
                JSONObject json = jsona.optJSONObject(i);
                CardBean card = new CardBean();
                card.setId(json.optInt("id", -1));
                card.setCardName(json.optString("name"));
                cardList.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<CardBean> getCardType(){
        return cardList;
    }

    public int getIc_pinInput_flag() {
        return Ic_pinInput_flag;
    }

    public void setIc_pinInput_flag(int ic_pinInput_flag) {
        Ic_pinInput_flag = ic_pinInput_flag;
    }

    public int getOpen_card_reader_flag() {
        return Open_card_reader_flag;
    }

    public void setOpen_card_reader_flag(int open_card_reader_flag) {
        Open_card_reader_flag = open_card_reader_flag;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public Map<ICCardSlot, ICCardSlotState> getMap() {
        return map;
    }

    public void setMap(Map<ICCardSlot, ICCardSlotState> map) {
        this.map = map;
    }

    public boolean isKeymap() {
        return isKeymap;
    }

    public void setKeymap(boolean keymap) {
        isKeymap = keymap;
    }
}
