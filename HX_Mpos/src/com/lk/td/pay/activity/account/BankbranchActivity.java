package com.lk.td.pay.activity.account;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.adapter.BankBranchAdapter;
import com.lk.td.pay.beans.BankBranch;
import com.lk.td.pay.beans.BasicResponse;
import com.lk.td.pay.utils.ClearEditText;
import com.lk.td.pay.utils.SideBar;
import com.lk.td.pay.utils.T;
import com.lk.td.pay.utils.SideBar.OnTouchingLetterChangedListener;
import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.config.Urls;
import com.pay.library.tool.Logger;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.uils.BankbranchComparator;
import com.pay.library.uils.BankbranchMode;
import com.pay.library.uils.CharacterParser;
import com.td.app.pay.hx.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BankbranchActivity extends BaseActivity {
	private LinearLayout layout;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private BankBranchAdapter adapter;
	private ClearEditText mClearEditText;
	private List<HashMap<String, Object>> branchlist = null;
	private String provId ;
	private String cityId ;
	private String bankName;
	private Context context;
	private TextView tv_title;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<BankbranchMode> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private BankbranchComparator pinyinComparator;
	private String userName,bankId,bankBranch;
	private String[] subBranch,cnapsCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_branch_list);
		context = BankbranchActivity.this;
		layout = (LinearLayout) findViewById(R.id.ll_game_sort_list);
		tv_title = (TextView) findViewById(R.id.tv_title);
		//
		tv_title.setText(getString(R.string.select_bankbranch));

		provId = getIntent().getStringExtra("provId");
		cityId = getIntent().getStringExtra("cityId");
		bankName = getIntent().getStringExtra("bankName");
		System.out.println("provId["+provId+"]");
		System.out.println("cityId["+cityId+"]");
		System.out.println("bankName["+bankName+"]");
		initDate();
	}
	
	private void initDate(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("bankProId", provId);// 所在省ID
		params.put("bankCityId", cityId);// 所在市ID
		params.put("bankName", bankName);//银行名称
		System.out.println("["+params.toString()+"]");
		postbranch(Urls.BANKLIST, params);
	}
	private void initViews(List<BankBranch> list) {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();		
		pinyinComparator = new BankbranchComparator();		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			public void onTouchingLetterChanged(String s) {
				// TODO 自动生成的方法存根
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				String bankname = ((BankbranchMode)adapter.getItem(position)).getName();
				
				String bankId = null;
				for (int i = 0; i < subBranch.length; i++) {
					if(bankname.equals(subBranch[i])){
						bankId = cnapsCode[i];
					}
				}
				/*BankBranch	bankBranch = new BankBranch();
				bankBranch.setBankbankId(bankId);
				bankBranch.setBankbankName(bankname);*/
				
				Intent it = new Intent();
				it.putExtra("bankId", bankId);
				it.putExtra("bankName", bankname);
				setResult(1000, it);
				finish();
			}
		});
		
		int length = list.size();
		cnapsCode = new String[length];
		subBranch = new String[length];
		for (int i = 0; i < length; i++) {
			subBranch[i] = list.get(i).getSubBranch().toString();
			cnapsCode[i] = list.get(i).getCnapsCode().toString();
		}
		
		
		SourceDateList = filledData(subBranch,cnapsCode);
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new BankBranchAdapter(this, SourceDateList);
		
		sortListView.setAdapter(adapter);
		adapter.updateListView(SourceDateList);
		adapter.notifyDataSetChanged();
		
		mClearEditText = (ClearEditText) findViewById(R.id.et_game_sort_filter_edit);
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent it = new Intent();
			it.putExtra("subBranch", "");
			it.putExtra("cnapsCode", "");
			setResult(10, it);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<BankbranchMode> filledData(String [] date,String[] bankbranchs){
		List<BankbranchMode> mSortList = new ArrayList<BankbranchMode>();
		for(int i=0; i<date.length; i++){
			BankbranchMode BankbranchMode = new BankbranchMode();
			BankbranchMode.setName(date[i]);
			BankbranchMode.setBankbranch(bankbranchs[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				BankbranchMode.setSortLetters(sortString.toUpperCase());
			}else{
				BankbranchMode.setSortLetters("#");
			}
			mSortList.add(BankbranchMode);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<BankbranchMode> filterDateList = new ArrayList<BankbranchMode>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(BankbranchMode BankbranchMode : SourceDateList){
				String name = BankbranchMode.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(BankbranchMode);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}	
	
	@SuppressWarnings("unused")
	private void postbranch(String url, HashMap<String, String> params) {

		MyHttpClient.post(BankbranchActivity.this, url, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						dismissLoadingDialog();
						
					}

					@SuppressWarnings("null")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json(responseBody);
						try {
							HashMap<String, Object> firstHashMap = null;
							BasicResponse r = new BasicResponse(responseBody).getResult();
							if (r.isSuccess()) {
								branchlist = new ArrayList<HashMap<String, Object>>();
								BankBranch bankBranch ;
								JSONObject jsonOneBody = r.getJsonBody();
								JSONArray jsonOneArray = jsonOneBody.getJSONArray("bankCardList");
								firstHashMap = new HashMap<String, Object>();
								List<BankBranch> list = new ArrayList<BankBranch>();

								for (int i = 0; i < jsonOneArray.length(); i++) {
									bankBranch = new BankBranch();
									bankBranch.setCnapsCode(jsonOneArray.getJSONObject(i).getString("cnapsCode"));
									bankBranch.setSubBranch(jsonOneArray.getJSONObject(i).getString("subBranch"));
									list.add(bankBranch);
								}
								
								initViews(list);
								
								
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

						Toast.makeText(BankbranchActivity.this,
								"网络连接超时", Toast.LENGTH_SHORT).show();

					}

				});
	}
	
	
}

