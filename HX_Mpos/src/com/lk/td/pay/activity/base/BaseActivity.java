package com.lk.td.pay.activity.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hx.view.widget.CustomDialog;
import com.hx.view.widget.MyDialog;
import com.lk.td.pay.utils.AppManager;
import com.lk.td.pay.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pay.library.tool.MyHttpClient;
import com.td.app.pay.hx.R;

public abstract class BaseActivity extends FragmentActivity {

    MyDialog dialog;
    Context context;

    public DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.color.white)
            .showImageForEmptyUri(R.color.white)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        context = this;
        // loading.setCancelable(false);
    }

    /**
     * 绑定返回事件
     */
    protected void bindBackButton() {
        findViewById(R.id.btn_back).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void getDialogInstance() {

        if (dialog == null) {
            dialog = new MyDialog(this);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showLoadingDialog() {
        getDialogInstance();
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                BaseActivity.this.finish();

            }
        });
        dialog.show();
    }


    public void updateDialogDes(String str) {
        if (null != dialog) {
            if (dialog.isShowing()) {
                dialog.setText(str);
            }
        }
    }

    protected void showLoadingDialog(String text) {
        getDialogInstance();
        if (text != null) {
            dialog.setText(text);
        }

        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                BaseActivity.this.finish();

            }
        });
        dialog.show();
    }

    /**
     * 禁止关闭的dialog
     *
     * @param text
     */
    protected void showLoadingDialogCannotCancle(String text) {
        getDialogInstance();
        if (!TextUtils.isEmpty(text)) {
            dialog.setText(text);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    T.showCustomeShort(BaseActivity.this, "此阶段不能返回");
                    return true;
                }
                return false;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                BaseActivity.this.finish();

            }
        });
        dialog.show();
    }

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                .show();
    }

    public void showDialog(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = "交易失败";
        }


        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage(msg)
                .setOkBtn("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();


    }

    public void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 网络错误处理
     *
     * @param statusCode
     * @param error
     */
    public void networkError(int statusCode, Throwable error) {
        /*if(statusCode==0){
            T.ss("服务器连接超时，请稍后再试.");
			return;
		}*/
        T.sl("网络错误");
    }

    /**
     * 登录长时间未操作
     */
    public void loginTimeOut() {

    }

    @Override
    protected void onDestroy() {
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
        MyHttpClient.cancleRequest(this);
        super.onDestroy();
    }

    protected <T extends View> T fv(int id) {
        return (T) findViewById(id);
    }


//	protected abstract void initView();
//	
//	protected abstract void loadData();



    public void showCustomDialog(String title,String msg,  String okStr, String cancelStr,
                                 DialogInterface.OnClickListener okClickLisener,
                                 DialogInterface.OnClickListener cancelListener){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg);
        if (!TextUtils.isEmpty(okStr)){
            builder.setOkBtn(okStr, okClickLisener);
        }
        if (!TextUtils.isEmpty(cancelStr)){
            builder.setCancelBtn(cancelStr, cancelListener);
        }

        if (TextUtils.isEmpty(okStr) && TextUtils.isEmpty(cancelStr)){
            builder.setCancelBtn("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface alertDialog, int i) {
                    alertDialog.dismiss();
                }
            });
        }
        CustomDialog alertDialog =  builder.create();
        alertDialog.dismiss();
    }
}
