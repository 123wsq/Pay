package com.hx.newland.cashin.swing.xdl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.hx.newland.cashin.swing.xdl.bluetootch.Const.DataEncryptWKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MacWKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.PinWKIndexConst;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.BatteryInfoResult;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.DeviceOutofLineException;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ModuleType;
import com.newland.mtype.common.ExCode;
import com.newland.mtype.common.MESeriesConst.TrackEncryptAlgorithm;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.AbstractProcessDeviceEvent;
import com.newland.mtype.event.DeviceEvent;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.log.DeviceLogger;
import com.newland.mtype.log.DeviceLoggerFactory;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.cardreader.CardRule;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;
import com.newland.mtype.module.common.cardreader.OpenCardType;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.QPBOCModule;
import com.newland.mtype.module.common.keyboard.KeyBoard;
import com.newland.mtype.module.common.keyboard.KeyBoardReadingEvent;
import com.newland.mtype.module.common.lcd.DispType;
import com.newland.mtype.module.common.lcd.LCD;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KSNKeyType;
import com.newland.mtype.module.common.pin.KSNLoadResult;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.module.common.swiper.TrackSecurityPaddingType;
import com.newland.mtype.module.external.me11.ME11External;
import com.newland.mtype.module.external.me11.ME11SwipResult;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

/**
 * 具体实现类
 */
public class DeviceControllerImpl implements DeviceController {
	private DeviceLogger logger = DeviceLoggerFactory
			.getLogger(DeviceControllerImpl.class);
	private static String DRIVER_NAME = "";
	private static DeviceManager deviceManager = ConnUtils.getDeviceManager();
	private String driverName;
	private DeviceConnParams connParams;

	private DeviceControllerImpl() {
	}
	
	public void init(Context context, String driverName,
			DeviceConnParams params,
			DeviceEventListener<ConnectionCloseEvent> listener) {
		deviceManager.init(context, driverName, params, listener);
		this.connParams = params;
		this.driverName = driverName;
	}
	/**
	 * 事件线程阻塞控制监听
	 * <p>
	 * 
	 * @author lance
	 * 
	 * @param <T>
	 */
	private class EventHolder<T extends DeviceEvent> implements DeviceEventListener<T> {

		private T event;

		private final Object syncObj = new Object();

		private boolean isClosed = false;

		public void onEvent(T event, Handler handler) {
			this.event = event;
			synchronized (syncObj) {
				isClosed = true;
				syncObj.notify();
			}
		}

		public Handler getUIHandler() {
			return null;
		}

		void startWait() throws InterruptedException {
			synchronized (syncObj) {
				if (!isClosed)
					syncObj.wait();
			}
		}

	}
	public DeviceConnParams getDeviceConnParams() {
		Device device = deviceManager.getDevice();
		if (device == null)
			return null;
		return (DeviceConnParams) device.getBundle();
	}

	public static DeviceController getInstance() {
		return new DeviceControllerImpl();
	}

	public DeviceConnState getDeviceConnState() {
		return deviceManager.getDeviceConnState();
	}

	@Override
	public void connect() throws Exception {
		deviceManager.connect();
		deviceManager.getDevice().setBundle(connParams);
	}

	@Override
	public void disConnect() {
		deviceManager.disconnect();
	}

	private void isConnected() {
		synchronized (this.driverName) {
			if (null == deviceManager || deviceManager.getDevice() == null) {
				throw new DeviceOutofLineException("device not connect!");
			}
		}
	}

	@Override
	public void destroy() {
		deviceManager.destroy();
	}

	@Override
	public void setParam(int tag, byte[] value) {
		TLVPackage tlvpackage = ISOUtils.newTlvPackage();
		tlvpackage.append(tag, value);
		deviceManager.getDevice().setDeviceParams(tlvpackage);
	}

	@Override
	public byte[] getParam(int tag) {
		TLVPackage pack = deviceManager.getDevice().getDeviceParams(tag);
		return pack.getValue(getOrginTag(tag));
	}

	private int getOrginTag(int tag) {
		if ((tag & 0xFF0000) == 0xFF0000) {
			return tag & 0xFFFF;
		} else if ((tag & 0xFF00) == 0xFF00) {
			return tag & 0xFF;
		}
		return tag;
	}

	@Override
	public void updateWorkingKey(WorkingKeyType workingKeyType,
			byte[] encryData, byte[] checkValue) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		int mkIndex = MKIndexConst.DEFAULT_MK_INDEX;
		WorkingKeyType wkType = WorkingKeyType.MAC;

		byte[] rslt = null;
		switch (workingKeyType) {
		case PININPUT:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex,
					PinWKIndexConst.DEFAULT_PIN_WK_INDEX, encryData);
			break;
		case DATAENCRYPT:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex,
					DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX, encryData);
			break;
		case MAC:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex,
					MacWKIndexConst.DEFAULT_MAC_WK_INDEX, encryData);
			break;
		default:
			throw new DeviceRTException(AppExCode.LOAD_WORKINGKEY_FAILED,
					"unknown key type!" + workingKeyType);
		}
		byte[] expectedKcv = new byte[4];
		System.arraycopy(rslt, 0, expectedKcv, 0, expectedKcv.length);
		if (!Arrays.equals(expectedKcv, checkValue)) {
			throw new RuntimeException("failed to check kcv!:["
					+ Dump.getHexDump(expectedKcv) + ","
					+ Dump.getHexDump(checkValue) + "]");
		}
	}

	@Override
	public DeviceInfo getDeviceInfo() {
		return deviceManager.getDevice().getDeviceInfo();
	}

	@Override
	public DeviceInfo getDeviceInfo_me11() {
		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		return me11Model.getDeviceInfo();
	}

	

	private <T extends AbstractProcessDeviceEvent> T preEvent(T event,
			int defaultExCode) {
		if (!event.isSuccess()) {
			if (event.isUserCanceled()) {
				return null;
			}
			if (event.getException() != null) {
				if (event.getException() instanceof RuntimeException) {// 运行时异常直接抛出.
					throw (RuntimeException) event.getException();
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"open card reader meet error!", event.getException());
			}
			throw new DeviceRTException(ExCode.UNKNOWN,
					"unknown exception!defaultExCode:" + defaultExCode);
		}
		return event;
	}

	private SwipResult getSwipResult(Swiper swiper, int trackKey,
			String encryptType) {
		SwipResult swipRslt = swiper.readEncryptResult(new SwiperReadModel[] {
				SwiperReadModel.READ_SECOND_TRACK,
				SwiperReadModel.READ_THIRD_TRACK },
				TrackSecurityPaddingType.NONE, new WorkingKey(trackKey),
				encryptType, null, null);
		return swipRslt;
	}
	
	@Override
    public SwipResult readEncryptICResult() {
        Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
        SwipResult swipRslt = swiper.readEncryptResult(new SwiperReadModel[] {
            SwiperReadModel.READ_IC_SECOND_TRACK },
            TrackSecurityPaddingType.NONE, new WorkingKey(DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX),
            TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, null, null);
        return swipRslt;
    }

	@Override
	public SwipResult swipCard(String msg, long timeout, TimeUnit timeUnit) {
		// showMessage(msg);
		CardReader cardReader = (CardReader) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_CARDREADER);
		if (cardReader == null) {
			throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
					"not support read card!");
		}
		try {
			ModuleType[] openedModuleTypes = cardReader.openCardReader(msg,
					new ModuleType[] { ModuleType.COMMON_SWIPER }, 30,
					TimeUnit.SECONDS);
			if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
				logger.info("start cardreader,but return is none!may user canceled?");
				return null;
			}
			if (openedModuleTypes.length > 1) {
				logger.warn("should return only one type of cardread action!but is "
						+ openedModuleTypes.length);
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"should return only one type of cardread action!but is "
								+ openedModuleTypes.length);
			}
			switch (openedModuleTypes[0]) {
			case COMMON_SWIPER:
				Swiper swiper = (Swiper) deviceManager.getDevice()
						.getStandardModule(ModuleType.COMMON_SWIPER);
				SwipResult swipRslt = getSwipResult(swiper,
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX,
						TrackEncryptAlgorithm.BY_UNIONPAY_MODEL);
				if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
					return swipRslt;
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"swip failed:" + swipRslt.getRsltType());
			default:
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"not support cardreader module:" + openedModuleTypes[0]);
			}
		} finally {
			cardReader.closeCardReader();
		}
	}

	@Override
	public SwipResult swipCardForPlain(String msg, long timeout,
			TimeUnit timeUnit) {
		CardReader cardReader = (CardReader) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_CARDREADER);
		if (cardReader == null) {
			throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
					"not support read card!");
		}
		try {
			EventHolder<OpenCardReaderEvent> listener = new EventHolder<OpenCardReaderEvent>();
			cardReader.openCardReader(msg,
					new ModuleType[] { ModuleType.COMMON_SWIPER }, timeout,
					timeUnit, listener);
			try {
				listener.startWait();
			} catch (InterruptedException e) {
				cardReader.cancelCardRead();
			} finally {
				clearScreen();
			}
			OpenCardReaderEvent event = listener.event;
			event = preEvent(event, AppExCode.GET_TRACKTEXT_FAILED);
			if (event == null) {
				return null;
			}
			ModuleType[] openedModuleTypes = event.getOpenedCardReaders();
			if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
				logger.info("start cardreader,but return is none!may user canceled?");
				return null;
			}
			if (openedModuleTypes.length > 1) {
				logger.warn("should return only one type of cardread action!but is "
						+ openedModuleTypes.length);
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"should return only one type of cardread action!but is "
								+ openedModuleTypes.length);
			}
			switch (openedModuleTypes[0]) {
			case COMMON_SWIPER:
				Swiper swiper = (Swiper) deviceManager.getDevice()
						.getStandardModule(ModuleType.COMMON_SWIPER);
				SwipResult swipRslt = swiper
						.readPlainResult(new SwiperReadModel[] {
								SwiperReadModel.READ_SECOND_TRACK,
								SwiperReadModel.READ_THIRD_TRACK });
				if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
					return swipRslt;
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"swip failed:" + swipRslt.getRsltType());
			default: {
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"not support cardreader module:" + openedModuleTypes[0]);
			}
			}
		} finally {
			cardReader.closeCardReader();
		}
	}

	@Override
	public ME11SwipResult swipCard_me11(byte[] time, long timeout,
			TimeUnit timeUnit) {
		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0xff,
				TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), time,
				null, null);
		return swipeResult;
	}

	@Override
	public ME11SwipResult swipCardForPlain_me11(byte[] time, long timeout,
			TimeUnit timeUnit) {
		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0x64,
				TrackEncryptAlgorithm.BY_PLAIN_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), time,
				null, null);
		return swipeResult;
	}

	@Override
	public ME11SwipResult swipCardFor_me11(byte[] time, long timeout,
			TimeUnit timeUnit) {
		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0x64,
				TrackEncryptAlgorithm.BY_M10_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), time,
				null, null);
		return swipeResult;
	}

	private EmvModule getEMVModule() {
		return (EmvModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_ME11EMV);
	}

	@Override
	public EmvModule getEmvModule() {
		isConnected();
		return (EmvModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_EMV);
	}

	private QPBOCModule getQPBOCModule() {
		return (QPBOCModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_QPBOC);
	}

	@Override
	public void startEmv(BigDecimal amt, TransferListener transferListener) {
		isConnected();
		try {
			EmvModule module = getEMVModule();
			// OnlinePinConfig config = new OnlinePinConfig();
			// new WorkingKey(
			// KeyIndexConst.KSN_INITKEY_INDEX), PinManageType.DUKPT,
			// acctInputType, acctHash, inputMaxLen, new byte[] { 'F', 'F',
			// 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
			// isEnterEnabled, msg, (int) timeout, TimeUnit.MILLISECONDS,
			// listener
			// config.setWorkingKey(new
			// WorkingKey(KeyIndexConst.KSN_INITKEY_INDEX));
			// config.setPinManageType(PinManageType.DUKPT);
			// config.setPinPadding(new byte[] { 'F', 'F','F', 'F', 'F', 'F',
			// 'F', 'F', 'F', 'F' });
			// config.setDisplayContent("请输入密码:");
			// config.setTimeout(30);
			// config.setInputMaxLen(6);
			// config.setEnterEnabled(true);
			// module.setOnlinePinConfig(config);

			EmvTransController controller = module
					.getEmvTransController(transferListener);
			controller.startEmv(amt, new BigDecimal("0"), true);
		} finally {
			logger.info("closeCardReader3");
			// cardReader.closeCardReader();
		}
	}

	@Override
	public PinInputEvent startPininput(String acctHash, int inputMaxLen,
			String msg) {
		if (acctHash == null) {
			throw new DeviceRTException(AppExCode.GET_PININPUT_FAILED,
					"acctHash should not be null!");
		}

		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		PinInputEvent event = pinInput
				.startStandardPinInput(new WorkingKey(
						PinWKIndexConst.DEFAULT_PIN_WK_INDEX),
						PinManageType.MKSK, AccountInputType.USE_ACCT_HASH,
						acctHash, inputMaxLen, new byte[] { 'F', 'F', 'F', 'F',
								'F', 'F', 'F', 'F', 'F', 'F' }, true, msg, 30,
						TimeUnit.SECONDS);
		return event;
	}

	@Override
	public PinInputEvent startPininput(AccountInputType acctInputType,
			String acctHash, int inputMaxLen, boolean isEnterEnabled,
			String msg, long timeout) throws InterruptedException {
		isConnected();
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		EventHolder<PinInputEvent> listener = new EventHolder<PinInputEvent>();
//		pinInput.startStandardPinInput(new WorkingKey(
//				KeyIndexConst.KSN_INITKEY_INDEX), PinManageType.DUKPT,
//				acctInputType, acctHash, inputMaxLen, new byte[] { 'F', 'F',
//						'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
//				isEnterEnabled, msg, (int) timeout, TimeUnit.MILLISECONDS,
//				listener);
		try {
			listener.startWait();
		} catch (InterruptedException e) {
			pinInput.cancelPinInput();
			throw e;
		} finally {
			clearScreen();
		}
		PinInputEvent event = listener.event;
		event = preEvent(event, AppExCode.GET_PININPUT_FAILED);
		if (event == null) {
			logger.info("start getChipherText,but return is none!may user canceled?");
			return null;
		}
		return event;
	}

	@Override
	public void startPininput(String acctHash, int inputMaxLen, String msg,
			DeviceEventListener<PinInputEvent> listener) {
		if (acctHash == null) {
			throw new DeviceRTException(AppExCode.GET_PININPUT_FAILED,
					"acctHash should not be null!");
		}

		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		pinInput.startStandardPinInput(
				new WorkingKey(PinWKIndexConst.DEFAULT_PIN_WK_INDEX),
				PinManageType.MKSK,
				AccountInputType.USE_ACCT_HASH,
				acctHash,
				inputMaxLen,
				new byte[] { 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
				true, msg, 30, TimeUnit.SECONDS, listener);
	}

	@Override
	public byte[] encrypt(WorkingKey wk, byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.encrypt(wk, EncryptType.ECB, input, new byte[] { 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}

	@Override
	public byte[] encrypt(WorkingKey wk, String input, String acct) {

		byte[] pinBlock = ByteUtils.process(
				ByteUtils.getPinBlock(input),
				ByteUtils.getPanInfo(acct));
		
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.encrypt(wk, EncryptType.ECB, pinBlock, new byte[] { 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}
	
	
	
	@Override
	public byte[] caculateMac(byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.calcMac(MacAlgorithm.MAC_ECB, new WorkingKey(
				MacWKIndexConst.DEFAULT_MAC_WK_INDEX), input);
	}

	@Override
	public String inputPlainPwd(String title, String content, int minLength,
			int maxLength, long timeout) throws InterruptedException {
		KeyBoard keyboard = (KeyBoard) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_KEYBOARD);
		EventHolder<KeyBoardReadingEvent<String>> listener = new EventHolder<KeyBoardReadingEvent<String>>();
		keyboard.readPwd(DispType.NORMAL, title, content, minLength, maxLength,
				(int) timeout, TimeUnit.SECONDS, listener);
		try {
			listener.startWait();
		} catch (InterruptedException e) {
			keyboard.cancelLastReading();
			throw e;
		} finally {
			clearScreen();
		}
		KeyBoardReadingEvent<String> event = listener.event;
		if (event == null)
			return null;

		return event.getRslt();
	}

	@Override
	public void reset() {
		deviceManager.getDevice().reset();
	}

	@Override
	public BatteryInfoResult getPowerLevel() {
		return deviceManager.getDevice().getBatteryInfo();
	}

	@Override
	public void showMessage(String msg) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.draw(msg);
		}
	}

	@Override
	public void clearScreen() {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.clearScreen();
		}
	}

	@Override
	public void showMessageWithinTime(String msg, int showtime) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.drawWithinTime(msg, showtime);
		}
	}

	@Override
	public void printBitMap(int position, Bitmap bitmap) {
		Printer printer = (Printer) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		printer.print(position, bitmap, 30, TimeUnit.SECONDS);
	}

	@Override
	public void printString(String data) {
		Printer printer = (Printer) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		printer.print(data, 30, TimeUnit.SECONDS);
	}

	@Override
	public String getCurrentDriverVersion() {
		if (deviceManager != null)
			return deviceManager.getDriverMajorVersion() + "."
					+ deviceManager.getDriverMinorVersion();
		return "n/a";
	}

	@Override
	public void shutdown() {
		if (deviceManager != null)
			deviceManager.getDevice().shutdown();

	}

	@Override
	public KSNLoadResult ksnLoad(KSNKeyType keytype, int KSNIndex, byte[] ksn,
			byte[] defaultKeyData, int mainKeyIndex, byte[] checkValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ME11SwipResult startTransfer(Context context,
			OpenCardType[] cardReaders, String msg, String amount,
			long timeout, TimeUnit timeunit, CardRule opencardrule,
			TransferListener transferListener) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
