package com.lk.td.pay.activity.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lk.td.pay.activity.account.LoginActivity;
import com.lk.td.pay.golbal.MApplication;
import com.pay.library.uils.DensityUtil;
import com.pay.library.uils.FileUtil;
import com.td.app.pay.hx.R;

import java.io.File;

public class SplashActivity extends Activity {

    private TranslateAnimation translateanim;
    private DisplayMetrics displayMeterMetrics;
    private MApplication mApplication;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mApplication = (MApplication) getApplication();
        getScreenWidthAndHeight();
        LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ImageView imageView = new ImageView(this);
        imageView.setPadding(0, 100, 0, 0);
        int image_size = DensityUtil.dp2px(this, 120);
        imageView.setLayoutParams(new LayoutParams(image_size, image_size));
        ll.addView(imageView);
        imageView.setImageResource(R.drawable.logo);
        File projectFile = FileUtil.mkdir(this);
        translateanim = new TranslateAnimation(0, 0, imageView.getY(), imageView.getY() + 250);
        translateanim.setDuration(1500);
        translateanim.setFillAfter(true);
        
        imageView.startAnimation(translateanim);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(ll, params);
        translateanim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                goLogin();
            }
        });
    }

    private void goLogin() {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获得屏幕的一些信息
     */
    private void getScreenWidthAndHeight() {
        displayMeterMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMeterMetrics);
        mApplication.setScreenWidth(displayMeterMetrics.widthPixels);
        mApplication.setScreenHeight(displayMeterMetrics.heightPixels);

    }

}
