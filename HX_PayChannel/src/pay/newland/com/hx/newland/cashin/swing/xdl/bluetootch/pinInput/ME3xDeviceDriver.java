package com.hx.newland.cashin.swing.xdl.bluetootch.pinInput;

import android.content.Context;
import android.os.Handler;

import com.hx.newland.cashin.swing.xdl.bluetootch.device.DeviceControllerInterface;
import com.hx.newland.cashin.swing.xdl.bluetootch.device.DeviceControllerInterfaceImpl;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;

/**
 * Created by YJF 
 * 初始化设备控制器
 */
public class ME3xDeviceDriver {
	private Context baseActivity;
	private DeviceControllerInterface controller;


	public ME3xDeviceDriver(Context baseActivity) {
		this.baseActivity = baseActivity;
	}

	public DeviceControllerInterface initMe3xDeviceController(String driverPath, DeviceConnParams params) {
		controller = DeviceControllerInterfaceImpl.getInstance(driverPath);
		controller.init(baseActivity, driverPath, params, new DeviceEventListener<ConnectionCloseEvent>() {
			@Override
			public void onEvent(ConnectionCloseEvent event, Handler handler) {

			}

			@Override
			public Handler getUIHandler() {
				return null;
			}
		});
		return controller;
	}

}
