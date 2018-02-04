
package com.hx.ty.cashin;

import android.os.Handler;

import com.whty.bluetoothsdk.util.Utils;
import com.whty.tymposapi.IDeviceDelegate;

import java.util.HashMap;

public class DeviceDelegate implements IDeviceDelegate {

    private Handler handler;

    public DeviceDelegate(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void onConnectedDevice(boolean isSuccess) {
        if (isSuccess) {
            handler.obtainMessage(31, "连接设备成功！").sendToTarget();
        } else {
            handler.obtainMessage(31, "连接设备失败！").sendToTarget();
        }
    }

    @Override
    public void onDisconnectedDevice(boolean isSuccess) {
        if (isSuccess) {
            handler.obtainMessage(31, "设备断开成功！").sendToTarget();
        } else {
            handler.obtainMessage(31, "设备断开失败！").sendToTarget();
        }
    }

    @Override
    public void onUpdateWorkingKey(boolean[] isSuccess) {
        if (isSuccess != null) {
            handler.obtainMessage(
                    31,
                    "更新磁道密钥：" + String.valueOf(isSuccess[0]) +
                            "\n更新PIN密钥：" + String.valueOf(isSuccess[1]) +
                            "\n更新MAC密钥：" + String.valueOf(isSuccess[2])).sendToTarget();
        } else {
            handler.obtainMessage(31, "终端加密失败！").sendToTarget();
        }
    }

    @Override
    public void onGetMacWithMKIndex(byte[] data) {
        if (data != null) {
            handler.obtainMessage(31,
                    Utils.bytesToHexString(data, data.length))
                    .sendToTarget();
        } else {
            handler.obtainMessage(31, "终端加密失败！").sendToTarget();
        }
    }

    @Override
    public void onReadCard(HashMap data) {
        if (data != null) {
            handler.obtainMessage(31, data.toString()).sendToTarget();
        } else {
            handler.obtainMessage(31, "刷卡指令执行失败！").sendToTarget();
        }
    }

//    @Override
//    public void onWaitingcard() {
//        handler.obtainMessage(SharedMSG.SHOW_MSG, "请刷卡或插卡").sendToTarget();
//    }

}
