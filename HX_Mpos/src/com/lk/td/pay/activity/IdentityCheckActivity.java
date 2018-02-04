package com.lk.td.pay.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pay.library.android.http.AsyncHttpResponseHandler;
import com.pay.library.bean.User;
import com.pay.library.config.Urls;
import com.pay.library.tool.MyHttpClient;
import com.pay.library.tool.Utils;
import com.pay.library.uils.ExpresssoinValidateUtil;
import com.pay.library.uils.ImageUtils;
import com.td.app.pay.hx.R;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.utils.T;

//选择文件上传
public class IdentityCheckActivity extends BaseActivity implements
		OnClickListener {

	private EditText et_cardnum, et_name, et_cardno, et_email, et_city;
	private Button doupload;
	private ArrayList<String> imguri = new ArrayList<String>();
	private String tag = "FileSwitchWays.class";
	String marker, id;
	HashMap<String, String> map = new HashMap<String, String>();
	private Context context;
	private String path;
	private Uri uri;
	private Bitmap bitmap1, bitmap2, bitmap3;
	ImageView cardImg1, cardImg2, cardImg3;
	private int currentImg = 1;
	private String userAccount;
	private String cardnum, name, cityid, provinceid,email;
	private boolean selected1 = false, selected2 = false, selected3;
	private TextView tv_inverise;

	private TextView tv_title;
	private Button btn_back;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identity_check_layout);
		context = IdentityCheckActivity.this;
		init();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	// 初始化
	private void init() {

		cardImg1 = (ImageView) findViewById(R.id.iv_check_front);
		cardImg2 = (ImageView) findViewById(R.id.iv_check_back);
		cardImg3 = (ImageView) findViewById(R.id.iv_check_hold);
		cardImg1.setOnClickListener(this);
		cardImg2.setOnClickListener(this);
		cardImg3.setOnClickListener(this);
		doupload = (Button) findViewById(R.id.btn_check_next);
		doupload.setOnClickListener(this);
		et_name = (EditText) findViewById(R.id.et_name);
		et_email = (EditText) findViewById(R.id.et_email);
		et_cardno = (EditText) findViewById(R.id.editTxtID);
		findViewById(R.id.txt_province).setOnClickListener(this);

		tv_title  = (TextView) findViewById(R.id.tv_title);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_province:
			
			break;
		case R.id.iv_check_front:
			currentImg = 1;
			chooseFile(1);
			break;
		case R.id.iv_check_back:
			currentImg = 2;
			chooseFile(2);
			break;
		case R.id.iv_check_hold:
			currentImg = 3;
			chooseFile(3);
			break;
		case R.id.btn_check_next:
			cardnum = et_cardno.getText().toString().trim();
			name=et_name.getText().toString()
					.trim();
			email=et_email.getText().toString().trim();
			
			if (TextUtils.isEmpty(cardnum)) {
				T.ss("请输入证件号码");
				return;
			}
			if (ExpresssoinValidateUtil.isChineseString(cardnum)) {
				T.ss("证件号码中存在中文字符");
				return;
			}
			if (!ExpresssoinValidateUtil.isIdCard(cardnum)) {
				T.ss("身份证号码有误，请核对后输入");
				return;
			}
			if (!selected1) {
				T.ss("请选择身份证正面照片");
				return;
			}
			if (!selected2) {
				T.ss("请选择身份反面照片");
				return;
			}
			exeUpload();
			break;

		}

	}

	private void chooseFile(int mark) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("请选择文件");
		alert.setItems(new String[] { "从相册选择", "相机拍照" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setType("image/*");
							startActivityForResult(intent, 132);
						} else {
							path = getExternalFilesDir(
									Environment.DIRECTORY_DCIM).getPath()
									+ getFormatFileName();

							File file = new File(path);
							if (!file.exists()) {
								try {
									file.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							uri = Uri.fromFile(file);
							Intent intent = new Intent();
							intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.putExtra(
									MediaStore.Images.Media.ORIENTATION, 0);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							IdentityCheckActivity.this.startActivityForResult(
									intent, 1322);
						}

					}
				});
		alert.show();

	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 132) {// 从相册获取
			if (null == data) {
				return;
			}
			String path = null;
			Uri uri = data.getData();
			if (uri != null) {
				path = ImageUtils.getAbsoluteImagePath(
						IdentityCheckActivity.this, uri);
				Log.d(tag, "图片地址:" + path);
			}
			File file = new File(path);
			Options op = new BitmapFactory.Options();
			long length = file.length() / 1000;
			if (length < 50) {
				op.inSampleSize = 1;
			} else if (length > 50 && length < 100) {
				op.inSampleSize = 2;
			} else if (length > 100 && length < 200) {
				op.inSampleSize = 2;
			} else if (length > 300 && length < 500) {
				op.inSampleSize = 4;
			} else if (length > 500 && length < 1000) {
				op.inSampleSize = 4;
			} else if (length > 1000 && length < 1500) {
				op.inSampleSize = 6;
			} else if (length > 1500) {
				op.inSampleSize = 8;
			}
			op.inJustDecodeBounds = false;
			op.outHeight = 500;
			op.outWidth = 500;
			Bitmap temp = BitmapFactory.decodeFile(path, op);
			BitmapDrawable drawable = new BitmapDrawable(temp);
			// System.out.println("Bitmap大小==" + temp.getByteCount());

			if (currentImg == 1) {
				cardImg1.setBackgroundDrawable(drawable);
				bitmap1 = temp;
				selected1 = true;
			} else if (currentImg == 2) {
				cardImg2.setBackgroundDrawable(drawable);
				bitmap2 = temp;
				selected2 = true;
			} else {
				cardImg3.setBackgroundDrawable(drawable);
				bitmap3 = temp;
				selected3 = true;
			}
			imguri.add(path);

		} else if (requestCode == 1322) {// 从相机获取照片
			File file = new File(path);
			if (file.length() < 10) {
				return;
			}
			Uri u = Uri.fromFile(file);
			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 6;
			op.outHeight = 500;
			op.outWidth = 500;
			long fileLength = file.length() / 1000;
			if (fileLength > 2000) {
				op.inSampleSize = 8;
			} else if (fileLength > 1500 && fileLength < 2000) {
				op.inSampleSize = 7;
			}
			op.inJustDecodeBounds = false;
			File fileSave = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/DCIM/" + getFormatFileName());
			Bitmap temp = BitmapFactory.decodeFile(path, op);
			if (null == temp) {
				return;
			}
			try {
				ImageUtils.saveImageToSD(fileSave, temp, 100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			BitmapDrawable draw = new BitmapDrawable(temp);
			if (currentImg == 1) {
				cardImg1.setBackgroundDrawable(draw);
				bitmap1 = temp;
				selected1 = true;
			}
			if (currentImg == 3) {
				cardImg3.setBackgroundDrawable(draw);
				bitmap3 = temp;
				selected3 = true;
			} else {
				cardImg2.setBackgroundDrawable(draw);
				bitmap2 = temp;
				selected2 = true;
			}
			imguri.add(path);

		}
	}

	private String Bitmap2String(Bitmap bitmap) {
		InputStream in = Utils.Bitmap2IS(bitmap, 100);
		byte[] data = null;
		try {
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		// 对字节数组Base64编码
		byte[] encode = Base64.encode(data, Base64.DEFAULT);
		return new String(encode);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 10) {
				map.put("", "");
				MyHttpClient.post(IdentityCheckActivity.this, "SY0007", map,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								// TODO 自动生成的方法存根

							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								// TODO 自动生成的方法存根
								networkError(statusCode, error);
							}
						});
			}
		};
	};

	/**
	 * 执行上传
	 */
	public void exeUpload() {
		showLoadingDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// HashMap<String, String> map = new HashMap<String, String>();
				map.put("certificateType", "01");
				map.put("custId", User.uId);
				map.put("userEmail", email);
				map.put("certificateNo ", cardnum);
				map.put("custName", name);
				map.put("cardFront", Bitmap2String(bitmap1));
				map.put("cardBack", Bitmap2String(bitmap2));
				map.put("cardHandheld", Bitmap2String(bitmap3));
				handler.sendEmptyMessage(10);
			}
		}).start();
	}

	private String getFormatFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return format.format(new Date()) + ".jpg";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setResult(100);
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
//
//		MyHttpClient.post(IdentityCheckActivity.this, url, params,
//				new com.loopj.android.http.AsyncHttpResponseHandler() {
//
//					@Override
//					public void onStart() {
//						showLoadingDialog();
//					}
//
//					@Override
//					public void onFinish() {
//
//						dismissLoadingDialog();
//
//						if (firstArrayList != null && firstArrayList.size() > 0) {
//
//							showProvinceListDialog.setContent("请选择省份",
//									firstArrayList);
//							if (!showProvinceListDialog.isVisible()) {
//
//								showProvinceListDialog.show(
//										getSupportFragmentManager(),
//										"PROVINCE_DIALOG");
//								// showProvinceListDialog.setCancelable(false);
//
//							}
//
//						}
//					}
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							byte[] responseBody) {
//						ArrayList<HashMap<String, Object>> secondArrayList = null;// 存放区/市一级
//						HashMap<String, Object> firstHashMap = null;
//						HashMap<String, Object> secondHashMap = null;// 存放区/县一级
//
//						try {
//							BasicResponse result = new BasicResponse(
//									responseBody).getResult();
//							if (result.isSuccess()) {
//
//								firstArrayList = new ArrayList<HashMap<String, Object>>();
//								JSONObject jsonOneBody = result.getJsonBody();
//								JSONArray jsonOneArray = jsonOneBody
//										.getJSONArray("province");
//								for (int i = 0; i < jsonOneArray.length(); i++) {
//
//									firstHashMap = new HashMap<String, Object>();
//									JSONObject jsonTwoBody = jsonOneArray
//											.getJSONObject(i);
//									JSONArray jsonSecondArray = jsonTwoBody
//											.getJSONArray("cityList");
//									if (jsonSecondArray != null) {
//
//										secondArrayList = new ArrayList<HashMap<String, Object>>();
//										for (int j = 0; j < jsonSecondArray
//												.length(); j++) {
//
//											secondHashMap = new HashMap<String, Object>();
//											secondHashMap.put("cityId",
//													jsonSecondArray
//															.getJSONObject(j)
//															.get("cityId"));
//											secondHashMap.put("cityName",
//													jsonSecondArray
//															.getJSONObject(j)
//															.get("cityName"));
//											secondHashMap.put("provId",
//													jsonSecondArray
//															.getJSONObject(j)
//															.get("provId"));
//											secondArrayList.add(secondHashMap);
//										}
//									}
//									firstHashMap.put("cityList",
//											secondArrayList);
//									firstHashMap.put("provName",
//											jsonTwoBody.get("provName"));
//									firstHashMap.put("provId",
//											jsonTwoBody.get("provId"));
//									firstArrayList.add(firstHashMap);
//								}
//							}
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							byte[] responseBody, Throwable error) {
//
//						firstArrayList = null;
//						Toast.makeText(RealNameAuthenticationActivity.this,
//								"网络连接超时", Toast.LENGTH_SHORT).show();
//
//					}
//
//				});
//	}
//
//	@Override
//	public void onBackPressed() {
//		onDestoryDialog();
//		super.onBackPressed();
//	}
//
//	@Override
//	public void getProvinceIdAndCityId(String provName, String provId,
//			String cityName, String cityId) {
//
//		showProvinceListDialog.dismiss();
//		txt_province.setText(provName);
//		txt_province.setHint(provId);
//		txt_city.setText(cityName);
//		txt_city.setHint(cityId);
//
//	}
//
//	private void onDestoryDialog() {
//
//		if (showProvinceListDialog != null
//				&& showProvinceListDialog.isVisible()) {
//
//			showProvinceListDialog.dismiss();
//			showProvinceListDialog = null;
//
//		}
//
//	}
}}
