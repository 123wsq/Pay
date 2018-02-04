package com.hx.newland.cashin.swing.xdl.bluetootch.pinInput;

import java.util.concurrent.TimeUnit;

import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KSNKeyType;
import com.newland.mtype.module.common.pin.KSNLoadResult;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.LoadPKResultCode;
import com.newland.mtype.module.common.pin.LoadPKType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.MacResult;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinInputResult;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;

/**
 * Created by HJP
 *  on 2015/8/12.
 */
public interface PinInputInterface {
	public byte[] calcMac(MacAlgorithm macAlgorithm, PinManageType pinManageType, WorkingKey wk, byte[] input);

	public void cancelPinInput();

	public byte[] decrypt(WorkingKey wk, EncryptType encryptType, byte[] input, byte[] cbcInit);

	public byte[] encrypt(WorkingKey wk, EncryptType encryptType, byte[] input, byte[] cbcInit);

	/*public PinInputResult encryptPIN(WorkingKey wk, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol, byte[] pin);

	public KSNLoadResult loadIPEK(KSNKeyType keytype, int KSNIndex, byte[] ksn, byte[] defaultKeyData, int mainKeyIndex, byte[] checkValue);
*/
//	public byte[] loadMainKey(KekUsingType kekUsingType, int mainIndex, byte[] data, byte[] checkValue, int kekIndex);

	public byte[] loadMainKey(KekUsingType kekUsingType, int mainIndex, byte[] data, int kekIndex);

	public LoadPKResultCode loadPublicKey(LoadPKType keytype, int pkIndex, String pkLength, byte[] pkModule, byte[] pkExponent, byte[] index,
										  byte[] mac);

	public byte[] loadWorkingKey(WorkingKeyType type, int mainKeyIndex, int workingKeyIndex, byte[] data, byte[] checkValue);


	public byte[] loadWorkingKey(WorkingKeyType type, int mainKeyIndex, int workingKeyIndex, byte[] data);


	public PinInputEvent startStandardPinInput(WorkingKey workingKey, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol,
											   int inputMaxLen, byte[] pinPadding, boolean isEnterEnabled, String displayContent, long timeout, TimeUnit timeunit);

	public void startStandardPinInput(WorkingKey workingKey, PinManageType pinManageType, AccountInputType acctInputType, String acctSymbol,
									  int inputMaxLen, byte[] pinPadding, boolean isEnterEnabled, String displayContent, long timeout, TimeUnit timeunit,
									  DeviceEventListener<PinInputEvent> inputListener);
}
