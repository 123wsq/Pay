package com.hx.newland.cashin.swing.xdl.bluetootch;

import android.util.Log;

import com.hx.newland.cashin.swing.xdl.bluetootch.device.DeviceControllerInterfaceImpl;
import com.newland.me.DeviceManager;
import com.newland.mtype.Device;
import com.newland.mtype.Module;
import com.newland.mtype.ModuleType;
/**
 * Created by YJF . 
 * 模块获取
 */
public class ModuleFactory {
	private DeviceManager deviceManager;

	public ModuleFactory() {
		deviceManager = DeviceControllerInterfaceImpl.getDeviceManager();
	}

	public Module getModule(ModuleType moduleType) {
		try {
			Device device = deviceManager.getDevice();

			Module module = deviceManager.getDevice().getStandardModule(moduleType);
			return module;
		} catch (Exception e) {
			return null;
		}

	}

	public Module getExModule(String moduleType) {
		try {
			Module module = deviceManager.getDevice().getExModule(moduleType);
			return module;
		} catch (Exception e) {
			return null;
		}

	}
}
