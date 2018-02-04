package com.lk.td.pay.activity.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.lk.td.pay.utils.T;
import com.td.app.pay.hx.R;

public class BaseFragmentActivity extends FragmentActivity {


    public DisplayMetrics dm;
    private boolean isStop;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        progressDialog = new ProgressDialog(this);
        Drawable drawable = getResources().getDrawable(R.drawable.loading_animation);
        progressDialog.setIndeterminateDrawable(drawable);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");
    }


    @Override
    protected void onResume() {
        super.onResume();
        isStop = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.dt_in_from_right, R.anim.dt_out_to_left);
    }

    public void startActivity(Intent intent, int enterAnim, int exitAnim) {
        super.startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
    }

//	@Override
//	public void startActivityForResult(Intent intent, int requestCode,
//			Bundle options) {
//		super.startActivityForResult(intent, requestCode, options);
//		overridePendingTransition(R.anim.dt_in_from_right,
//				R.anim.dt_out_to_left);
//	}

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent,
                                          int requestCode) {
        super.startActivityFromFragment(fragment, intent, requestCode);
        overridePendingTransition(R.anim.dt_in_from_right,
                R.anim.dt_out_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.dt_in_from_left,
                R.anim.dt_out_to_right);
    }

    public void hideInput() {
        // ���ؼ���
        if (getCurrentFocus() != null
                && getCurrentFocus().getApplicationWindowToken() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus()
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    public void showLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    public void showLoadingDialog(String msg) {
        if (progressDialog != null) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int id) {
        Toast.makeText(this, getResources().getText(R.string.quit_app), Toast.LENGTH_SHORT).show();
    }

    public void networkError(int statusCode, Throwable error) {
        /*if(statusCode==0){
            T.ss("服务器连接超时，请稍后再试.");
			return;
		}*/
        T.sl("网络错误");
    }
}
