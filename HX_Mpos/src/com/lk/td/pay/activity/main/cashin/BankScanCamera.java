package com.lk.td.pay.activity.main.cashin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etop.BankCard.BankCardAPI;
import com.etop.utils.BankCardScanUtils;
import com.etop.utils.BankViewfinderView;
import com.hx.pay.activity.newland.BuletootchController;
import com.hx.pay.activity.newland.BuletootchControllerImpl;
import com.pay.library.uils.BitmapUtil;
import com.pay.library.uils.FileUtil;
import com.td.app.pay.hx.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 扫描银行卡
 * @author wsq
 *
 */
public class BankScanCamera extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/.tang_di/DCIM/Camera/";

    private Camera camera;
    private SurfaceView surfaceView;
    private RelativeLayout re_c;
    private SurfaceHolder surfaceHolder;
    private ImageButton back;
    private ImageButton flash;
    private BankViewfinderView myView;

    private BankCardAPI api;
//    private BankCardInfoAPI cardinfoapi=null;
    private Bitmap bitmap;
    private int preWidth = 0;
    private int preHeight = 0;
    private boolean isROI = false;
    private int width;
    private int height;
    private TimerTask timer;
    private Vibrator mVibrator;
    private byte[] tackData;
    private int BankAPP;
    private String placeActivity;
    private boolean isFatty = false;
    private int[] m_ROI = {0, 0, 0, 0};
    private boolean bInitKernal = false;
    private Timer mTimer = new Timer();
    private TimerTask mTask;
    private final int TAKE_PHOTO = 1;
    private BuletootchController controller = BuletootchControllerImpl.getInstance();
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        Configuration cf = this.getResources().getConfiguration(); //获取设置的配置信息
        int noriention = cf.orientation;
        if (noriention == cf.ORIENTATION_LANDSCAPE) {
            if (!bInitKernal) {
                intScranCard();
            }
        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
         // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan_camera);

        Intent intent = getIntent();
        BankAPP = intent.getIntExtra("BankAPP", -1);
        placeActivity = intent.getStringExtra("Action");
        type = intent.getStringExtra("TYPE");
        findView();
    }

    @Override
    protected void onRestart() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceViwe);
        re_c = (RelativeLayout) findViewById(R.id.re_c);
        back = (ImageButton) findViewById(R.id.back_camera);
        flash = (ImageButton) findViewById(R.id.flash_camera);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;   // 屏幕宽度（像素）
        height = metric.heightPixels; // 屏幕高度（像素）
        if (width * 3 == height * 4) {
            isFatty = true;
        }

        int back_w = (int) (width * 0.066796875);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(back_w, back_w);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        int Fheight = height;
        if (isFatty)
            Fheight = (int) (height * 0.75);
        layoutParams.leftMargin = (int) (((width - Fheight * 0.06 * 1.585) - back_w));
        layoutParams.bottomMargin = (int) (height * 0.10486111111111111111111111111111);
        back.setLayoutParams(layoutParams);

        int flash_w = (int) (width * 0.066796875);
        layoutParams = new RelativeLayout.LayoutParams(flash_w, flash_w);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        if (isFatty)
            Fheight = (int) (height * 0.75);
        layoutParams.leftMargin = (int) (((width - Fheight * 0.06 * 1.585) - flash_w));
        layoutParams.topMargin = (int) (height * 0.10486111111111111111111111111111);
        flash.setLayoutParams(layoutParams);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(BankScanCamera.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	if (null != controller){
                    controller.destroy();
                }
                api.ScanEnd();
                finish();
            }
        });

        flash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    String mess = getResources().getString(R.string.toast_flash);
                    Toast.makeText(BankScanCamera.this, mess, Toast.LENGTH_LONG).show();
                } else {
                    if (camera != null) {
                        Camera.Parameters parameters = camera.getParameters();
                        String flashMode = parameters.getFlashMode();
                        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            parameters.setExposureCompensation(0);
                        } else {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 闪光灯常亮
                            parameters.setExposureCompensation(-1);
                        }
                        try {
                            camera.setParameters(parameters);
                        } catch (Exception e) {
                            String mess = getResources().getString(R.string.toast_flash);
                            Toast.makeText(BankScanCamera.this, mess, Toast.LENGTH_LONG).show();
                        }
                        camera.startPreview();
                    }
                }
            }
        });
        mTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        mTimer.schedule(mTask, 1000 * 30);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    try {
                        if (camera != null) {
                            camera.setPreviewCallback(null);
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    api.ScanEnd();
//                    takePicture(TAKE_PHOTO);
                    int buffl = 30;
                    char recogval[] = new char[buffl];
                    int pLineWarp[] = new int[32000];
                    Intent intent = new Intent(BankScanCamera.this, BankCardShowResultActivity.class);
                    intent.putExtra("PicR", pLineWarp);
                    intent.putExtra("StringR", recogval);
                    intent.putExtra("Success", 2);
                    intent.putExtra("BankAPP", BankAPP);
                    intent.putExtra("Action", placeActivity);
                    intent.putExtra("bankCardImage", "");
                    intent.putExtra("TYPE",type);
                    startActivity(intent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 拍照
     *
     * @param code
     */
    private void takePicture(int code) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(FileUtil.getTdPath(this) + "cardScan.jpg");
        Uri u = Uri.fromFile(f);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
//            String path = setBitmapToImageView(FileUtil.getTdPath(this) + "cardScan.jpg");
            int buffl = 30;
            char recogval[] = new char[buffl];
            int pLineWarp[] = new int[32000];
            Intent intent = new Intent(BankScanCamera.this, BankCardShowResultActivity.class);
            intent.putExtra("PicR", pLineWarp);
            intent.putExtra("StringR", recogval);
            intent.putExtra("Success", 2);
            intent.putExtra("BankAPP", BankAPP);
            intent.putExtra("Action", placeActivity);
            intent.putExtra("bankCardImage", FileUtil.getTdPath(this) + "cardScan.jpg");
            intent.putExtra("TYPE", type);
            startActivity(intent);
            finish();
//        }
    }

    private String setBitmapToImageView(String imagePath) {

//        Camera.Parameters parameters = camera.getParameters();
        Bitmap tempValue = BitmapUtil.resizeImageSecondMethod(imagePath, 64, 48);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tempValue.compress(Bitmap.CompressFormat.JPEG, 5, baos);

        Bitmap tmpbitmap = Bitmap.createBitmap(tempValue, m_ROI[0], m_ROI[1], m_ROI[2] - m_ROI[0], m_ROI[3] - m_ROI[1]);
//        System.out.println("m_ROI:" + m_ROI[0] + " " + m_ROI[1] + " " + m_ROI[2] + " " + m_ROI[3]);
        String bankCardImage = savePicture(tmpbitmap, "V");
        return bankCardImage;

//        int[] datas = BankCardScanUtils.convertYUV420_NV21toARGB8888(baos.toByteArray(),  640, 480);
//
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inInputShareable = true;
//        opts.inPurgeable = true;
//        bitmap = Bitmap.createBitmap(datas,  640, 480, Bitmap.Config.ARGB_8888);
//        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap, m_ROI[0], m_ROI[1], m_ROI[2] - m_ROI[0], m_ROI[3] - m_ROI[1]);
//        System.out.println("m_ROI:" + m_ROI[0] + " " + m_ROI[1] + " " + m_ROI[2] + " " + m_ROI[3]);
//        String bankCardImage = savePicture(tmpbitmap, "V");
//        return bankCardImage;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        intScranCard();
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
                if (null != controller){
                    controller.destroy();
                }
            	
                String mess = getResources().getString(R.string.toast_camera);
                Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        try {
            /**
             * 禁止打开相机时在此崩溃,TODO
             */
            camera.setPreviewDisplay(holder);
            initCamera(holder);
            Timer time = new Timer();
            if (timer == null) {
                timer = new TimerTask() {
                    public void run() {
                        // isSuccess=false;
                        if (camera != null) {
                            try {
                                camera.autoFocus(new AutoFocusCallback() {
                                    public void onAutoFocus(boolean success, Camera camera) {
                                        // isSuccess=success;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
            time.schedule(timer, 500, 2500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            camera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        synchronized (camera) {
                            new Thread() {
                                public void run() {
                                    initCamera(holder);
                                    super.run();
                                }
                            }.start();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {

            if(api!=null){
                api.ScanEnd();
                api=null;
            }
//            if(cardinfoapi!=null){
//                cardinfoapi.UninitCardInfo();
//                cardinfoapi=null;
//            }

            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	if (null != controller){
                controller.destroy();
            }
        	
            try {
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            api.ScanEnd();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    
    @TargetApi(14)
    private void initCamera(SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        Camera.Size size;
        int length = list.size();
        int previewWidth = 640;
        int previewheight = 480;
        int second_previewWidth = 0;
        int second_previewheight = 0;
        if (length == 1) {
            size = list.get(0);
            previewWidth = size.width;
            previewheight = size.height;
        } else {
            for (int i = 0; i < length; i++) {
                size = list.get(i);
                if (isFatty) {
                    if (size.height <= 960 || size.width <= 1280) {
                        second_previewWidth = size.width;
                        second_previewheight = size.height;

                        if (previewWidth <= second_previewWidth && second_previewWidth * 3 == second_previewheight * 4) {
                            previewWidth = second_previewWidth;
                            previewheight = second_previewheight;
                        }
                    }
                } else {
                    if (size.height <= 960 || size.width <= 1280) {
                        second_previewWidth = size.width;
                        second_previewheight = size.height;
                        if (previewWidth <= second_previewWidth && second_previewWidth <= width && second_previewheight <= height) {
                            previewWidth = second_previewWidth;
                            previewheight = second_previewheight;
                        }
                    }
                }
            }
        }
        preWidth = previewWidth;
        preHeight = previewheight;
        if (!isROI) {
            int $t = height / 10;
            int t = $t;
            int b = height - t;
            int $l = (int) ((b - t) * 1.58577);
            int l = (width - $l) / 2;
            int r = width - l;
            if (isFatty) {
                $t = height / 5;
                t = $t;
                b = height - t;
                $l = (int) ((b - t) * 1.58577);
                l = (width - $l) / 2;
                r = width - l;
            }
            double proportion = (double) width / (double) preWidth;
            double hproportion = (double) height / (double) preHeight;
            l = (int) (l / proportion);
            t = (int) (t / hproportion);
            r = (int) (r / proportion);
            b = (int) (b / hproportion);
            m_ROI[0] = l;
            m_ROI[1] = t;
            m_ROI[2] = r;
            m_ROI[3] = b;
            api.SetRegion(l, t, r, b);
            isROI = true;
            if (isFatty)
                myView = new BankViewfinderView(this, width, height, isFatty);
            else
                myView = new BankViewfinderView(this, width, height);
            re_c.addView(myView);
        }
        parameters.setPictureFormat(ImageFormat.JPEG);

        parameters.setPreviewSize(preWidth, preHeight);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        camera.setPreviewCallback(this);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public String savePicture(Bitmap bitmap, String tag) {
        String strCaptureFilePath = PATH + tag + "_Bank_" + pictureName() + ".jpg";
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(strCaptureFilePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "图片存储失败,请检查SD卡", Toast.LENGTH_SHORT).show();
        }
        return strCaptureFilePath;
    }

    public String pictureName() {
        String str = "";
        Time t = new Time();
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;
        if (month < 10)
            str = String.valueOf(year) + "0" + String.valueOf(month);
        else {
            str = String.valueOf(year) + String.valueOf(month);
        }
        if (date < 10)
            str = str + "0" + String.valueOf(date + "_");
        else {
            str = str + String.valueOf(date + "_");
        }
        if (hour < 10)
            str = str + "0" + String.valueOf(hour);
        else {
            str = str + String.valueOf(hour);
        }
        if (minute < 10)
            str = str + "0" + String.valueOf(minute);
        else {
            str = str + String.valueOf(minute);
        }
        if (second < 10)
            str = str + "0" + String.valueOf(second);
        else {
            str = str + String.valueOf(second);
        }
        return str;
    }

    private int counter = 0;
    private int counterCut = 0;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        tackData = data;
        Camera.Parameters parameters = camera.getParameters();
        counter++;
        if (counter == 2) {
            counter = 0;
            int buffl = 30;
            char recogval[] = new char[buffl];
            int line[] = new int[4];
            line[0] = 0;
            line[1] = 0;
            line[2] = 0;
            line[3] = 0;
            int pLineWarp[] = new int[32000];
            int r = api.ScanStreamNV21(data, parameters.getPreviewSize().width, parameters.getPreviewSize().height, line, recogval, pLineWarp);
            if (line[0] == 1) {
                if (myView != null) {
                    myView.setLeftLine(1);
                }
            } else {
                if (myView != null) {
                    myView.setLeftLine(0);
                }
            }
            if (line[1] == 1) {
                if (myView != null) {
                    myView.setTopLine(1);
                }
            } else {
                if (myView != null) {
                    myView.setTopLine(0);
                }
            }
            if (line[2] == 1) {
                if (myView != null) {
                    myView.setRightLine(1);
                }
            } else {
                if (myView != null) {
                    myView.setRightLine(0);
                }
            }
            if (line[3] == 1) {
                if (myView != null) {
                    myView.setBottomLine(1);
                }
            } else {
                if (myView != null) {
                    myView.setBottomLine(0);
                }
            }
            if (r == 0) {
                camera.stopPreview();
                // 震动
                mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                mVibrator.vibrate(100);
                // 删除正常识别保存图片功能
                int[] datas = BankCardScanUtils.convertYUV420_NV21toARGB8888(tackData, parameters.getPreviewSize().width,
                        parameters.getPreviewSize().height);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inInputShareable = true;
                opts.inPurgeable = true;
                bitmap = Bitmap.createBitmap(datas, parameters.getPreviewSize().width,
                        parameters.getPreviewSize().height, Bitmap.Config.ARGB_8888);
                Bitmap tmpbitmap = Bitmap.createBitmap(bitmap, m_ROI[0], m_ROI[1], m_ROI[2] - m_ROI[0], m_ROI[3] - m_ROI[1]);
                System.out.println("m_ROI:" + m_ROI[0] + " " + m_ROI[1] + " " + m_ROI[2] + " " + m_ROI[3]);
//                savePicture(bitmap, "M");
                String bankCardImage = savePicture(tmpbitmap, "V");
                api.ScanEnd();
                String cardinfo[]=new String[4];
//                cardinfoapi.GetCardInfo(recogval, cardinfo);
//                cardinfoapi.UninitCardInfo();

                Intent intent = new Intent(BankScanCamera.this, BankCardShowResultActivity.class);
                intent.putExtra("PicR", pLineWarp);
                intent.putExtra("StringR", recogval);
                intent.putExtra("StringBN",cardinfo[0]);  //银行名称
                intent.putExtra("StringCN",cardinfo[1]);   //卡名称
                intent.putExtra("StringBC",cardinfo[2]);  //银行代码
                intent.putExtra("StringCT",cardinfo[3]);   //卡类型
                intent.putExtra("Success", 2);
                intent.putExtra("BankAPP", BankAPP);
                intent.putExtra("Action", placeActivity);
                intent.putExtra("bankCardImage", bankCardImage);
                intent.putExtra("TYPE",type);
                startActivity(intent);
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                finish();
                camera.setPreviewCallback(null);
            }
            if (r == 1) {
                counterCut++;
                if (counterCut == 5) {
                    counterCut = 0;
                }
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {

        }
    }


    public void intScranCard(){

        if(api==null){

            api = new BankCardAPI();
            String FilePath = Environment.getExternalStorageDirectory().toString() + "/129AE5220F8D531981E7.lic";
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int nRet = api.ScanStart("", FilePath, "129AE5220F8D531981E7", 0x04, 0x02, telephonyManager, this);
            if (nRet != 0) {
                Toast.makeText(getApplicationContext(), "激活失败", Toast.LENGTH_SHORT).show();
                System.out.print("nRet=" + nRet);
            } else {
                bInitKernal = true;
//                if(cardinfoapi==null) {
////                    cardinfoapi =new BankCardInfoAPI();
//                    cardinfoapi.InitCardInfo();
//                }
            }

        }
    }
   
}
