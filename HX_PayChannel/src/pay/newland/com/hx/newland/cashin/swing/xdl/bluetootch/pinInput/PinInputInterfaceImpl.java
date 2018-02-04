package com.hx.newland.cashin.swing.xdl.bluetootch.pinInput;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.hx.newland.cashin.swing.xdl.bluetootch.ModuleBase;
import com.newland.mtype.ModuleType;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.LoadPKResultCode;
import com.newland.mtype.module.common.pin.LoadPKType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;

/**
 * Created by YJF 密码输入接口实现
 */
public class PinInputInterfaceImpl extends ModuleBase implements PinInputInterface {

	private PinInput pinInput;

	public PinInputInterfaceImpl() {
		pinInput = (PinInput) factory.getModule(ModuleType.COMMON_PININPUT);
	}

	// 0.大数据mac计算
	@Override
	public byte[] calcMac(MacAlgorithm macAlgorithm, PinManageType pinManageType, WorkingKey wk, byte[] input) {
		byte[] macResult = pinInput.calcMac(macAlgorithm, pinManageType, wk, input);
		return macResult;
	}

	// 1.撤消上一次的密码输入
	@Override
	public void cancelPinInput() {
		pinInput.cancelPinInput();
	}

	// 2.解密一串数据
	@Override
	public byte[] decrypt(WorkingKey wk, EncryptType encryptType, byte[] input, byte[] cbcInit) {
		byte[] result = pinInput.decrypt(wk, encryptType, input, cbcInit);
		return result;
	}

	// 3.加密一串数据
	@Override
	public byte[] encrypt(WorkingKey wk, EncryptType encryptType, byte[] input, byte[] cbcInit) {
		byte[] result = pinInput.encrypt(wk, encryptType, input, cbcInit);
		return result;
	}

	/*// 4.无键盘输入密码
	@Override
	public PinInputResult encryptPIN(WorkingKey wk, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol, byte[] pin) {
		return pinInput.encryptPIN(wk, pinManageType, acctInputType, acctSymbol, pin);
	}

	// 5.loadIPEK
	@Override
	public KSNLoadResult loadIPEK(KSNKeyType keytype, int KSNIndex, byte[] ksn, byte[] defaultKeyData, int mainKeyIndex, byte[] checkValue) {
		KSNLoadResult ksnLoadResult = pinInput.loadIPEK(keytype, KSNIndex, ksn, defaultKeyData, mainKeyIndex, checkValue);
		return ksnLoadResult;
	}*/

	// 6.装载主密钥
	@Override
	public byte[] loadMainKey(KekUsingType kekUsingType, int mainIndex, byte[] data, int kekIndex) {
		byte[] mainKey = pinInput.loadMainKey(kekUsingType, mainIndex, data, kekIndex);
		return mainKey;
	}

	/*@Override
	public byte[] loadMainKey(KekUsingType kekUsingType, int mainIndex, byte[] data, byte[] checkValue, int kekIndex) {
		byte[] mainKey = pinInput.loadMainKey(kekUsingType, mainIndex, data, checkValue, kekIndex);
		return mainKey;
	}*/
	// 7.load公钥
	@Override
	public LoadPKResultCode loadPublicKey(LoadPKType keytype, int pkIndex, String pkLength, byte[] pkModule, byte[] pkExponent, byte[] index,
			byte[] mac) {

		LoadPKResultCode loadPKResultCode = pinInput.LoadPublicKey(keytype, pkIndex, pkLength, pkModule, pkExponent, index, mac);
		return loadPKResultCode;
	}

	// 8.装载工作密钥
	@Override
	public byte[] loadWorkingKey(WorkingKeyType type, int mainKeyIndex, int workingKeyIndex, byte[] data) {
		byte[] wk = pinInput.loadWorkingKey(type, mainKeyIndex, workingKeyIndex, data);
		return wk;
	}

	@Override
	public byte[] loadWorkingKey(WorkingKeyType type, int mainKeyIndex, int workingKeyIndex, byte[] data, byte[] checkValue) {
		byte[] wk = pinInput.loadWorkingKeyAndVerify(type, mainKeyIndex, workingKeyIndex, data, checkValue);
		return wk;
	}

	// 9。调用一个pin输入过程
	@Override
	public PinInputEvent startStandardPinInput(WorkingKey workingKey, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol,
			int inputMaxLen, byte[] pinPadding, boolean isEnterEnabled, String displayContent, long timeout, TimeUnit timeunit) {
		if (null==pinInput){
			Log.d("===========","pinInput 为空了");
		}else {
			PinInputEvent event = pinInput.startStandardPinInput(workingKey, pinManageType, acctInputType, acctSymbol, inputMaxLen, pinPadding,
					isEnterEnabled, displayContent, timeout, timeunit);
			return event;
		}
		return null;
	}

	// 10开启一个密码输入过程
	@Override
	public void startStandardPinInput(WorkingKey workingKey, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol,
			int inputMaxLen, byte[] pinPadding, boolean isEnterEnabled, String displayContent, long timeout, TimeUnit timeunit,
			DeviceEventListener<PinInputEvent> inputListener) {

		if (null==pinInput){
			Log.d("===========","pinInput 为空了");
		}else {
			pinInput.startStandardPinInput(workingKey, pinManageType, acctInputType, acctSymbol, inputMaxLen, pinPadding, isEnterEnabled, displayContent,
					timeout, timeunit);
		}
	}
}
