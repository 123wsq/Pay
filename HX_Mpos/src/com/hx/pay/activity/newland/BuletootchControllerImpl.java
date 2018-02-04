package com.hx.pay.activity.newland;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.hx.newland.cashin.swing.xdl.AppExCode;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.DataEncryptWKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.MacWKIndexConst;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const.PinWKIndexConst;
import com.lk.td.pay.beans.PosData;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.DeviceOutofLineException;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ModuleType;
import com.newland.mtype.UpdateAppListener;
import com.newland.mtype.common.MESeriesConst.TrackEncryptAlgorithm;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.AbstractProcessDeviceEvent;
import com.newland.mtype.event.DeviceEvent;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.log.DeviceLogger;
import com.newland.mtype.log.DeviceLoggerFactory;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.cardreader.CardReaderResult;
import com.newland.mtype.module.common.cardreader.CardResultType;
import com.newland.mtype.module.common.cardreader.CardRule;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;
import com.newland.mtype.module.common.cardreader.OpenCardType;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.QPBOCModule;
import com.newland.mtype.module.common.iccard.ICCardModule;
import com.newland.mtype.module.common.iccard.ICCardSlot;
import com.newland.mtype.module.common.iccard.ICCardSlotState;
import com.newland.mtype.module.common.iccard.ICCardType;
import com.newland.mtype.module.common.lcd.LCD;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KSNKeyType;
import com.newland.mtype.module.common.pin.KSNLoadResult;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.LoadPKResultCode;
import com.newland.mtype.module.common.pin.LoadPKType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.printer.PrintContext;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.printer.PrinterResult;
import com.newland.mtype.module.common.printer.PrinterStatus;
import com.newland.mtype.module.common.rfcard.RFCardModule;
import com.newland.mtype.module.common.rfcard.RFCardType;
import com.newland.mtype.module.common.rfcard.RFKeyMode;
import com.newland.mtype.module.common.rfcard.RFResult;
import com.newland.mtype.module.common.storage.Storage;
import com.newland.mtype.module.common.storage.StorageResult;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.ISOUtils;

/**
 * DeviceController接口的具体实现
 * 
 * @author evil
 * 
 */
public class BuletootchControllerImpl implements BuletootchController {
	private static String DRIVER_NAME = "";

	private DeviceLogger logger = DeviceLoggerFactory.getLogger(BuletootchControllerImpl.class);
	private static DeviceManager deviceManager = ConnUtils.getDeviceManager();

	private DeviceConnParams connParams;

	private BuletootchControllerImpl() {
	}

	public void init(Context context, String driverName, DeviceConnParams params, DeviceEventListener<ConnectionCloseEvent> listener) {
		deviceManager.init(context, driverName, params, listener);
		this.connParams = params;

	}

	public DeviceConnParams getDeviceConnParams() {
		Device device = deviceManager.getDevice();
		if (device == null)
			return null;

		return (DeviceConnParams) device.getBundle();
	}

	public static BuletootchController getInstance() {
		return new BuletootchControllerImpl();
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

	@Override
	public void updateWorkingKey(WorkingKeyType workingKeyType, byte[] encryData, byte[] checkValue) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		int mkIndex = MKIndexConst.DEFAULT_MK_INDEX;
		switch (workingKeyType) {
		case PININPUT:
			pinInput.loadWorkingKeyAndVerify(WorkingKeyType.PININPUT, mkIndex, PinWKIndexConst.DEFAULT_PIN_WK_INDEX, encryData, checkValue);
			break;
		case DATAENCRYPT:
			pinInput.loadWorkingKeyAndVerify(WorkingKeyType.DATAENCRYPT, mkIndex, DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX, encryData, checkValue);
			break;
		case MAC:
			pinInput.loadWorkingKeyAndVerify(WorkingKeyType.MAC, mkIndex, MacWKIndexConst.DEFAULT_MAC_WK_INDEX, encryData, checkValue);
			break;
		default:
			throw new DeviceRTException(AppExCode.LOAD_WORKINGKEY_FAILED, "unknown key type!" + workingKeyType);
		}
	}


	@SuppressWarnings("deprecation")
	@Override
	public void showMessage(String msg) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.draw(msg);
		}
	}

	@Override
	public void clearScreen() {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.clearScreen();
		}
	}

	@Override
	public SwipResult swipCard(String msg, long timeout, TimeUnit timeUnit) {
		CardReader cardReader = (CardReader) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
		if (cardReader == null) {
			throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "not support read card!");
		}
		try {
			CardReaderResult cardReaderResult = cardReader.openCardReader(new OpenCardType[] { OpenCardType.SWIPER }, 30, TimeUnit.SECONDS, msg, CardRule.ALLOW_LOWER);
			ModuleType[] openedModuleTypes = cardReaderResult.getOpenedCardReaders();
			if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
				logger.info("start cardreader,but return is none!may user canceled?");
				return null;
			}
			if (openedModuleTypes.length > 1) {
				logger.warn("should return only one type of cardread action!but is " + openedModuleTypes.length);
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "should return only one type of cardread action!but is " + openedModuleTypes.length);
			}
			switch (openedModuleTypes[0]) {
			case COMMON_SWIPER:
				CardResultType cardResultType = cardReaderResult.getCardResultType();
				if (cardResultType == CardResultType.SWIPE_CARD_FAILED) {
					throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "swip failed!");
				}
				Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
				SwipResult swipRslt = getSwipResult(swiper, DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX, TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, 0);
				if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
					return swipRslt;
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "" + swipRslt.getRsltType());
			default:
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "not support cardreader module:" + openedModuleTypes[0]);
			}
		} finally {
			cardReader.closeCardReader();
		}
	}

	private SwipResult getSwipResult(Swiper swiper, int trackKey, String encryptType, int flag) {
		isConnected();
		SwipResult swipRslt;
		if (flag == Const.CardType.COMMON) {
			swipRslt = swiper.readEncryptResult(new SwiperReadModel[] { SwiperReadModel.READ_SECOND_TRACK, SwiperReadModel.READ_THIRD_TRACK }, new WorkingKey(trackKey), encryptType);
		} else {
			swipRslt = swiper.readEncryptResult(new SwiperReadModel[] { SwiperReadModel.READ_IC_SECOND_TRACK }, new WorkingKey(trackKey), encryptType);
		}
		return swipRslt;
	}

	@Override
	public DeviceInfo getDeviceInfo() {
		return deviceManager.getDevice().getDeviceInfo();
	}

	@Override
	public void reset() {
		deviceManager.getDevice().reset();
	}

	@Override
	public void destroy() {
		deviceManager.destroy();
	}

	public DeviceConnState getDeviceConnState() {
		return deviceManager.getDeviceConnState();
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
	public void printBitMap(int position, Bitmap bitmap) {
		Printer printer = (Printer) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		printer.print(position, bitmap, 30, TimeUnit.SECONDS);
	}

	@Override
	public PrinterResult printString(String data) {
		Printer printer = (Printer) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		return printer.print(data, 30, TimeUnit.SECONDS);
	}

	@Override
	public PrinterResult printScript(String data) {
		Printer printer = (Printer) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		byte[] printData = null;
		try {
			printData = data.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new DeviceRTException(-1, "脚本执行失败!");
		}
		return printer.printByScript(PrintContext.defaultContext(), printData, 60, TimeUnit.SECONDS);

	}

	@Override
	public byte[] encrypt(WorkingKey wk, EncryptType encryptType, byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.encrypt(wk, encryptType, input, new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}

	@Override
	public byte[] decrypt(WorkingKey wk, EncryptType encryptType, byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.decrypt(wk, encryptType, input, new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}

	@Override
	public byte[] caculateMac(MacAlgorithm macAlgorithm, byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.calcMac(macAlgorithm, new WorkingKey(MacWKIndexConst.DEFAULT_MAC_WK_INDEX), input);
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

	@Override
	public void showMessageWithinTime(String msg, int showtime) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.drawWithinTime(msg, showtime);
		}

	}


	private <T extends AbstractProcessDeviceEvent> T preEvent(SimpleTransferListener transferListener, T event, int defaultExCode) {
		if (!event.isSuccess()) {
			if (event.isUserCanceled()) {
				return null;
			}
			if (event.getException() != null) {
				if (event.getException() instanceof RuntimeException) {// 运行时异常直接抛出
					transferListener.onFailed("运行异常");
				}
				transferListener.onFailed(""+event.getException());
			}
			transferListener.onFailed(""+defaultExCode);
		}
		return event;
	}

	@Override
	public void loadMainKey(KekUsingType kekUsingType, int mkIndex, byte[] keyData, byte[] checkValue) {
		// 方法1，pos不做kcv校验，适用于主密钥没有校验值的情况，该方法只保证密钥成功灌入pos，不做正确性校验
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		// pinInput.loadMainKey(kekUsingType, mkIndex, keyData);
		// --------------------------------------------------------------------------------
		// 方法2：pos内部做kcv校验
		pinInput.loadMainKeyAndVerify(kekUsingType, mkIndex, keyData, checkValue, -1);
	}

	@Override
	public String getCurrentDriverVersion() {
		if (deviceManager != null)
			return deviceManager.getDriverMajorVersion() + "." + deviceManager.getDriverMinorVersion();

		return "n/a";
	}

	@Override
	public void isConnected() {
		synchronized (DRIVER_NAME) {
			if (null == deviceManager || deviceManager.getDevice() == null) {
				throw new DeviceOutofLineException("无法连接设备!");
			}
		}
	}

	@Override
	public EmvModule getEmvModule() {
		isConnected();
		return (EmvModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_EMV);
	}


	@Override
	public void updateFirmware(String filePath, UpdateAppListener listener) {
		isConnected();
		deviceManager.getDevice().updateApp(new File(filePath), listener);
	}

	@Override
	public void startTransfer(final Context context, OpenCardType[] openCardType, String msg, BigDecimal amt, long timeout, TimeUnit timeunit, CardRule opencardrule, SimpleTransferListener transferListener){
		isConnected();
		CardReader cardReader = (CardReader) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
		if (cardReader == null) {
			transferListener.onFailed("不支持读卡!");
			return;
		}

		EventHolder<OpenCardReaderEvent> listener = new EventHolder<OpenCardReaderEvent>();
		cardReader.openCardReader(openCardType, timeout, timeunit, msg, opencardrule, listener);
		try {
			listener.startWait();
		} catch (InterruptedException e) {
			cardReader.cancelCardRead();
			transferListener.onOpenCardreaderCanceled();
		} finally {
			clearScreen();
		}

		OpenCardReaderEvent event = listener.event;
		event = preEvent(transferListener, event, AppExCode.GET_TRACKTEXT_FAILED);
		
		if (event == null) {
			transferListener.onFailed("用户取消操作");
			transferListener.onOpenCardreaderCanceled();
			return;
		}
		ModuleType[] openedModuleTypes = event.getOpenedCardReaders();
		if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
			transferListener.onOpenCardreaderCanceled();
			transferListener.onFailed("用户取消操作");
			return;
		}
		if (openedModuleTypes.length > 1) {
			logger.warn("should return only one type of cardread action!but is " + openedModuleTypes.length);
			transferListener.onFailed("读卡失败");
			return;
		}

		
		switch (openedModuleTypes[0]) {
		case COMMON_SWIPER: {
			try{
				CardResultType cardResultType = event.getCardResultType();
				logger.info("========刷卡结果=============" + cardResultType.toString());
				if (cardResultType != CardResultType.SWIPE_CARD_SUCCESS) {
					
					transferListener.onFailed(""+cardResultType.toString());
					return;
				}
				
				SwipResult swipRslt = getTrackText(Const.CardType.COMMON);
				if (swipRslt== null){
					transferListener.onFailed("刷卡撤销");
					return;
				}
				
				if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
					transferListener.onStatus();
					byte[] secondTrack = swipRslt.getSecondTrackData();
					byte[] thirdTrack = swipRslt.getThirdTrackData();
					String secondTrackStr=(secondTrack == null ? "" : new String(secondTrack, "UTF-8"));//二磁道
					String thirdTrackStr=(thirdTrack == null ? "" : new String(thirdTrack, "UTF-8"));//三磁道
					System.err.println("getValidDate:" + swipRslt.getValidDate() );
					System.err.println("swipRslt.getAccount():" + swipRslt.getAccount().getAcctNo() );
					
					String cardNo=String.valueOf(swipRslt.getAccount().getAcctNo());//银行卡号
					PosData.getPosData().setCardNo(cardNo);
					PosData.getPosData().setRate("1");
					PosData.getPosData().setTermType("01");
					PosData.getPosData().setTrack(secondTrackStr + "|"+thirdTrackStr);
					PosData.getPosData().setRandom("000000");
					PosData.getPosData().setPeriod(swipRslt.getValidDate());
					PosData.getPosData().setCrdnum("");
	
					PosData.getPosData().setIcdata("");
					if(PosData.getPosData().getIcdata().equals("")||PosData.getPosData().getIcdata() == null){
						PosData.getPosData().setMediaType("01");//磁卡类型01 磁条卡 02 IC卡
					}else{
						PosData.getPosData().setMediaType("02");//磁卡类型01 磁条卡 02 IC卡
					}
					PosData.getPosData().setSwipResult(swipRslt);
					transferListener.onSuccess();
					break;
				}
			}catch(Exception e){
				transferListener.onFailed(e.getMessage());
			}
			
		}
		
		case COMMON_ICCARD: {
			Log.d("===========", "======IC====");
			EmvModule module = getEmvModule();
			module.setOnlinePinConfig(null);
			EmvTransController controller = module.getEmvTransController(transferListener);
			controller.startEmv(amt, new BigDecimal("0"), true);
			
			break;
		}
		case COMMON_RFCARD:
			Log.d("==========", "========非接通道=====");
			
			try {
				QPBOCModule qPBOCModule = (QPBOCModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_QPBOC);
				EmvTransInfo emvTransInfo = qPBOCModule.startQPBOC(amt, timeout, timeunit);
				transferListener.onQpbocFinished(emvTransInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				transferListener.onFailed(e.getMessage());
			}
			break;
		default:
			transferListener.onFailed("not support cardreader module:"+openedModuleTypes[0]);
		}

	}

	@Override
	public SwipResult getTrackText(int flag) throws InterruptedException {

		int trackKey = DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX;
		Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
		SwipResult swipRslt = getSwipResult(swiper, trackKey, TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, flag);
		if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
			return swipRslt;
		}
		throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED, "交易撤销");
	}

	@Override
	public PrinterStatus getPrinterStatus() {
		isConnected();
		Printer printer = (Printer) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
		return printer.getStatus();
	}

	@Override
	public byte[] call(ICCardSlot slot, ICCardType cardType, byte[] req, long timeout, TimeUnit timeunit) {
		ICCardModule iccardModule = (ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARD);
		return iccardModule.call(slot, cardType, req, timeout, timeunit);
	}

	@Override
	public Map<ICCardSlot, ICCardSlotState> checkSlotsState() {
		ICCardModule iccardModule = (ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARD);
		return iccardModule.checkSlotsState();
	}

	@Override
	public void powerOff(ICCardSlot slot, ICCardType cardType) {
		ICCardModule iccardModule = (ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARD);
		iccardModule.powerOff(slot, cardType);
	}

	@Override
	public byte[] powerOn(ICCardSlot slot, ICCardType cardType) {
		ICCardModule iccardModule = (ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARD);
		return iccardModule.powerOn(slot, cardType);
	}

	@Override
	public RFResult powerOn(RFCardType qPCardType, int timeout) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		return qpCardModule.powerOn(qPCardType, timeout);
	}

	@Override
	public byte[] call(byte[] req, long timeout, TimeUnit timeunit) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		return qpCardModule.call(req, timeout, timeunit);
	}

	@Override
	public void powerOff(int timeout) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.powerOff(timeout);
	}

	@Override
	public void authenticateByExtendKey(RFKeyMode qpKeyMode, byte[] SNR, int blockNo, byte[] key) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.authenticateByExtendKey(qpKeyMode, SNR, blockNo, key);
	}

	@Override
	public void authenticateByLoadedKey(RFKeyMode qpKeyMode, byte[] SNR, int blockNo) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.authenticateByLoadedKey(qpKeyMode, SNR, blockNo);
	}

	@Override
	public void storeKey(RFKeyMode qpKeyMode, int keyIndex, byte[] key) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.storeKey(qpKeyMode, keyIndex, key);
	}

	@Override
	public void loadKey(RFKeyMode qpKeyMode, int keyIndex) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.loadKey(qpKeyMode, keyIndex);
	}

	@Override
	public byte[] readDataBlock(int blockNo) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		return qpCardModule.readDataBlock(blockNo);
	}

	@Override
	public void writeDataBlock(int blockNo, byte[] data) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.writeDataBlock(blockNo, data);
	}

	@Override
	public void incrementOperation(int blockNo, byte[] data) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.incrementOperation(blockNo, data);
	}

	@Override
	public void decrementOperation(int blockNo, byte[] data) {
		RFCardModule qpCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARD);
		qpCardModule.decrementOperation(blockNo, data);
	}

	@Override
	public boolean initializeRecord(String recordName, int recordLength, int params1Offset, int params1Length, int params2Offset, int params2Length) {
		Storage storage = (Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
		return storage.initializeRecord(recordName, recordLength, params1Offset, params1Length, params2Offset, params2Length);
	}

	@Override
	public int fetchRecordCount(String recordName) {
		Storage storage = (Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
		return storage.fetchRecordCount(recordName);
	}

	@Override
	public StorageResult addRecord(String recordName, byte[] content) {
		Storage storage = (Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
		return storage.addRecord(recordName, content);
	}

	@Override
	public StorageResult updateRecord(String recordName, int recordNo, String checkParams1, String checkParams2, byte[] content) {
		Storage storage = (Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
		return storage.updateRecord(recordName, recordNo, checkParams1, checkParams2, content);
	}

	@Override
	public byte[] fetchRecord(String recordName, int recordNo, String checkParams1, String checkParams2) {
		Storage storage = (Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
		return storage.fetchRecord(recordName, recordNo, checkParams1, checkParams2);
	}

	@Override
	public KSNLoadResult ksnLoad(KSNKeyType keytype, int KSNIndex, byte[] ksn,
			byte[] defaultKeyData, int mainKeyIndex, byte[] checkValue) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.ksnLoad(keytype, KSNIndex, ksn, defaultKeyData, mainKeyIndex, checkValue);
	}

	@Override
	public LoadPKResultCode LoadPublicKey(LoadPKType keytype, int pkIndex,
			String pkLength, byte[] pkModule, byte[] pkExponent, byte[] index,
			byte[] mac) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.LoadPublicKey(keytype, pkIndex, pkLength, pkModule, pkExponent, index, mac);
	}


	

}
