package com.hx.pay.activity.ty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hx.newland.cashin.swing.xdl.DeviceController;
import com.hx.newland.cashin.swing.xdl.DeviceControllerImpl;
import com.hx.ty.cashin.DeviceDelegate;
import com.lk.td.pay.activity.base.BaseActivity;
import com.lk.td.pay.activity.account.EquAddConfirmActivity;
import com.newland.me.DeviceManager;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.pay.library.bean.User;
import com.td.app.pay.hx.R;
import com.whty.comm.inter.ICommunication;
import com.whty.tymposapi.DeviceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckTYActivity extends BaseActivity {

    private static final String ME3X_DRIVER_NAME = "com.newland.me.ME3xDriver";
    private static final String TY_BLUETOOTH_DEVICE = "ICommunication.BLUETOOTH_DEVICE";
    public static final int TYPE_BIND_CARD = 1;  //表示绑定刷卡器
    public static final int TYPE_CONN = 2;       //表示连接蓝牙
    public static final String  TYPE = "TYPE";
    private int default_type = 1;
    private DeviceController controller = DeviceControllerImpl.getInstance();
    private ListView m_ListView;
    private ImageView imvAnimScan;
    private AnimationDrawable animScan;
    private BluetoothAdapter mAdapter;
    private List<BluetoothDevice> lstDevScanned;
    private BroadcastReceiver recvBTScan = null;
    private String blueTootchAddress;
    private Button checkBtn;
    private MyListViewAdapter m_Adapter;
    private Handler mMainMessageHandler;
    private String trmmodno;
    private String mac;
    private LinearLayout backBtn;// 返回
    private Context context;
    private boolean isInited = false;
    private boolean deviceConnected = false;

    private String amount = "";

    protected ArrayAdapter<String> arrayAdapter;

    private ListView appListView;
    BluetoothAdapter adapter;
    private TextView tv_title;
    private Button btn_back;


    Handler hdStopScan = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10024) {
                StopScanBTPos();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1003:
                    break;
                case 1004:
                    System.out.println("----->1004");
                    check();
                    break;
                case 1005:
                    if (controller.getDeviceConnState() == DeviceManager.DeviceConnState.CONNECTED){
                        User.macAddress = mac;
                        startActivity(new Intent(CheckTYActivity.this, SwingCardByTYBluetootchActivity.class));
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private DeviceApi deviceApi;
    private DeviceDelegate delegate;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_check);

        default_type = getIntent().getIntExtra(TYPE, default_type);

        context = CheckTYActivity.this;
        checkBtn = (Button) findViewById(R.id.btnBT);
        checkBtn.setOnClickListener(new MyOnClickListener());


        tv_title = (TextView) findViewById(R.id.tv_title);
        if (default_type == TYPE_CONN){
            tv_title.setText("连接蓝牙刷卡器");
        }else {
            tv_title.setText("绑定蓝牙刷卡器");
        }

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);

        m_ListView = (ListView) findViewById(R.id.lv_indicator_BTPOS);
        imvAnimScan = (ImageView) findViewById(R.id.img_anim_scanbt);
        animScan = (AnimationDrawable) getResources().getDrawable(
                R.anim.progressanmi);
        imvAnimScan.setBackgroundDrawable(animScan);
        adapter = BluetoothAdapter.getDefaultAdapter();
        m_ListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onBTPosSelected(CheckTYActivity.this, view, position);
                // 开始连接
                /*BluetoothDevice device = adapter
						.getRemoteDevice(blueTootchAddress);*/
                //Me3xDeviceDriver me3xDeviceController = new Me3xDeviceDriver(CheckActivity.this);
                handler = new MyHandler();
                delegate = new DeviceDelegate(handler);
                deviceApi = new DeviceApi(getApplicationContext());
                deviceApi.setDelegate(delegate);
                if (deviceApi.initDevice(ICommunication.BLUETOOTH_DEVICE)) {
                    System.out.println("初始化成功");
                    deviceConnected = true;
                } else {
                    System.out.println("初始化失败");
                }
//				initMe3xDeviceController(ME3X_DRIVER_NAME,new BlueToothV100ConnParams(blueTootchAddress));
                System.out.println("控制器已初始化!");
                getDeviceInfo();
                animScan.stop();
                imvAnimScan.setVisibility(View.GONE);

            }

        });
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    private void check() {
        /**************************************************/
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("------->1004++");
        Intent it = new Intent(CheckTYActivity.this, EquAddConfirmActivity.class);
        it.putExtra("ksn", trmmodno);
        it.putExtra("macAddress", mac);
        startActivity(it);
        System.out.println("------->1004--");
//        deviceApi.disconnectDevice();
        System.out.println("------->1004**");
        controller.disConnect();
        finish();
        /**************************************************/
    }

    @Override
    public void finish() {
        super.finish();
        if (deviceConnected) {
            new Thread() {
                public void run() {
                    boolean mDis = deviceApi.disconnectDevice();
                    if (mDis) {
                        System.out.println("断开成功");
                    }else {
                        System.out.println("断开失败");
                    }
                }
            }.start();
        }
    }

    private void onBTPosSelected(Activity activity, View itemView, int index) {
        StopScanBTPos();
        Map<String, ?> dev = (Map<String, ?>) m_Adapter.getItem(index);
        blueTootchAddress = (String) dev.get("ADDRESS");
        System.out
                .println("--------------------------blue" + blueTootchAddress);
    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            doScanBTPos();
        }
    }

    private void doScanBTPos() {
        if (lstDevScanned == null) {
            lstDevScanned = new ArrayList<BluetoothDevice>();
        }
        lstDevScanned.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        refreshAdapter();
        //
        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!mAdapter.isEnabled()) {
            // 弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enabler);
            return;
            // 不做提示，强行打开
            // mAdapter.enable();
        }
        //
        if (recvBTScan != null) {
            unregisterReceiver(recvBTScan);
        }
        recvBTScan = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    boolean isFound = false;
                    for (BluetoothDevice dev : lstDevScanned) {
                        if (dev.getAddress().equals(device.getAddress())) {
                            isFound = true;
                            break;
                        }
                    }
                    //
                    if (!isFound)
                        lstDevScanned.add(device);
                    refreshAdapter();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    onStopScanBTPos();
                }
            }

        };
        //
        IntentFilter localIntentFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        localIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(recvBTScan, localIntentFilter);
        //
        imvAnimScan.setVisibility(View.VISIBLE);
        animScan.start();
        hdStopScan.sendEmptyMessageDelayed(10240, 1000 * 20);
        mAdapter.startDiscovery();
    }

    private void refreshAdapter() {
        if (m_Adapter != null) {
            m_Adapter.clearData();
            m_Adapter = null;
        }
        //
        List<Map<String, ?>> data = generateAdapterData();
        m_Adapter = new MyListViewAdapter(this, data);
        m_ListView.setAdapter(m_Adapter);
    }

    protected List<Map<String, ?>> generateAdapterData() {
        List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
        for (BluetoothDevice dev : lstDevScanned) {
            System.out
                    .println("======================dev.getName()============>>>>"
                            + dev.getName());
            if (dev.getName() != null) {
                if (dev.getName().contains("TY")) {

                    Map<String, Object> itm = new HashMap<String, Object>();
                    itm.put("ICON",
                            dev.getBondState() == BluetoothDevice.BOND_BONDED ? Integer
                                    .valueOf(R.drawable.bluetooth_blue)
                                    : Integer
                                    .valueOf(R.drawable.bluetooth_blue_unbond));
                    itm.put("TITLE", dev.getName() + "(" + dev.getAddress()
                            + ")");
                    itm.put("ADDRESS", dev.getAddress());
                    data.add(itm);


                }
            }

        }
        return data;
    }

    private class MyListViewAdapter extends BaseAdapter {
        private List<Map<String, ?>> m_DataMap;
        private LayoutInflater m_Inflater;

        public void clearData() {
            m_DataMap.clear();
            m_DataMap = null;
        }

        public MyListViewAdapter(Context context, List<Map<String, ?>> map) {
            this.m_DataMap = map;
            this.m_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return m_DataMap.size();
        }

        @Override
        public Object getItem(int position) {
            return m_DataMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = m_Inflater.inflate(R.layout.bt_qpos_item, null);

            }
			/*ImageView m_Icon = (ImageView) convertView
					.findViewById(R.id.item_iv_icon);*/
            TextView m_TitleName = (TextView) convertView
                    .findViewById(R.id.item_tv_lable);

            //
            Map<String, ?> itemdata = (Map<String, ?>) m_DataMap.get(position);
            // int idIcon = (Integer) itemdata.get("ICON");
            String sTitleName = (String) itemdata.get("TITLE");
            m_TitleName.setText(sTitleName);
            return convertView;
        }

    }

    private void StopScanBTPos() {
        if (mAdapter != null) {
            mAdapter.cancelDiscovery();
            mAdapter = null;
        }

    }

    private void onStopScanBTPos() {
        animScan.stop();
        imvAnimScan.setVisibility(View.GONE);
        //
        unregisterReceiver(recvBTScan);
        refreshAdapter();
        recvBTScan = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cancelDiscovery();
        }
        dismissLoadingDialog();
    }

    class MessageHandler extends Handler {
        private long mLogCount = 0;

        public MessageHandler(Looper looper) {
            super(looper);
        }
    }

    public DeviceController initMe3xDeviceController(String driverPath, DeviceConnParams params) {
        controller.init(CheckTYActivity.this, driverPath, params, new DeviceEventListener<ConnectionCloseEvent>() {
            @Override
            public void onEvent(ConnectionCloseEvent event, Handler handler) {
                if (event.isSuccess()) {
                    System.out.println("设备被客户主动断开！");
                }
                if (event.isFailed()) {
                    System.out.println("设备链接异常断开！" + event.getException().getMessage());
                }
            }

            @Override
            public Handler getUIHandler() {
                return null;
            }
        });
        System.out.println("驱动版本号：" + controller.getCurrentDriverVersion());
        return controller;

    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                connectDevice();
                try {
                    processingLock();
//					DeviceInfo deviceInfo =  controller.getDeviceInfo();
                    trmmodno = deviceApi.getDeviceCSN().substring(2);
//					trmmodno = deviceInfo.getCSN();
                    mac = blueTootchAddress;
                    if(default_type == TYPE_CONN){
                        mHandler.sendEmptyMessageDelayed(1005, 100);
                    }else{
                        mHandler.sendEmptyMessage(1004);
                    }
                } catch (Exception e) {
                    System.err.println("设备连接失败!" + e.getMessage());
                } finally {
                    processingUnLock();
                }

            }
        }).start();
    }

    public void connectDevice() {
        System.out.println("设备连接中...");
        try {
//			controller.connect();
            deviceApi.connectDevice(blueTootchAddress);
            System.out.println("设备连接成功...");
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("设备链接异常断开...");
            throw new RuntimeException(e1.getMessage(), e1);
        }

    }

    public void processingUnLock() {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("PBOC_LOCK", false);
        editor.commit();
    }

    public void processingLock() {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("PBOC_LOCK", true);
        editor.commit();
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            String show_msg = "";
            switch (msg.what) {

                case 31:

                    show_msg = (String) msg.obj;
                    if (show_msg.equals("连接设备成功！"))
                        deviceConnected = true;

                    break;

                case 32:

                    show_msg = (String) msg.obj;
                    break;

                default:
                    break;
            }
        }
    }

}