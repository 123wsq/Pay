package com.hx.pay.activity.newland;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.hx.newland.cashin.swing.xdl.bluetootch.BluetootchTransferListener;
import com.hx.newland.cashin.swing.xdl.bluetootch.Const;
import com.hx.newland.cashin.swing.xdl.bluetootch.swiper.SwiperInterfaceImpl;
import com.lk.td.pay.beans.PosData;
import com.newland.mtype.common.MESeriesConst;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.ISOUtils;

/**
 * 交易过程监听实现
 * 
 * @author evil
 * 
 */
public abstract class SimpleTransferListener implements BluetootchTransferListener {
	
	private SwingCardByXDLBluetootchActivity swingcardbyxdlbluetootchactivity;
	private static List<Integer> L_55TAGS = new ArrayList<Integer>();
	String pininputString;
	static {
		L_55TAGS.add(0x9f26);
		L_55TAGS.add(0x9F27);
		L_55TAGS.add(0x9F10);
		L_55TAGS.add(0x9F37);
		L_55TAGS.add(0x9F36);
		L_55TAGS.add(0x95);
		L_55TAGS.add(0x9A);
		L_55TAGS.add(0x9C);
		L_55TAGS.add(0x9F02);
		L_55TAGS.add(0x5F2A);
		L_55TAGS.add(0x82);
		L_55TAGS.add(0x9F1A);
		L_55TAGS.add(0x9F03);
		L_55TAGS.add(0x9F33);
		L_55TAGS.add(0x9F74);
		L_55TAGS.add(0x9F34);
		L_55TAGS.add(0x9F35);
		L_55TAGS.add(0x9F1E);
		L_55TAGS.add(0x84);
		L_55TAGS.add(0x9F09);
		L_55TAGS.add(0x9F41);
		L_55TAGS.add(0x91);
		L_55TAGS.add(0x71);
		L_55TAGS.add(0x72);
		L_55TAGS.add(0xDF31);
		L_55TAGS.add(0x9F63);
		L_55TAGS.add(0x8A);
		L_55TAGS.add(0xDF32);
		L_55TAGS.add(0xDF33);
		L_55TAGS.add(0xDF34);
	//	L_55TAGS.add(0xDF75);
	}

	public SimpleTransferListener( SwingCardByXDLBluetootchActivity swingcardbyxdlbluetootchactivity) {
		this.swingcardbyxdlbluetootchactivity = swingcardbyxdlbluetootchactivity;
	}


	@Override
	public void onQpbocFinished(EmvTransInfo context) throws Exception {
		System.err.println("qpboc交易结束:" + context.externalToString());
		// ExecuteRslt结果集： 0x0f成功  ，别的都是失败

		Log.d("qpboc返回码 ","====="+context.getExecuteRslt());
	if (context.getExecuteRslt() == 0x0f) {
		
		onStatus();

		TLVPackage tlvPackage = context.setExternalInfoPackage(L_55TAGS);

		SwiperInterfaceImpl swiperInterfaceImpl = new SwiperInterfaceImpl();
		SwipResult swipRslt = swiperInterfaceImpl.readEncryptResult(new SwiperReadModel[] { SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK },
				new WorkingKey(Const.DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX),
				MESeriesConst.TrackEncryptAlgorithm.BY_UNIONPAY_MODEL);

		if (null != swipRslt && swipRslt.getRsltType() == SwipResultType.SUCCESS) {
			try {

				PosData.getPosData().setSwipResult(swipRslt);
				byte[] secondTrack = swipRslt.getSecondTrackData();
				byte[] thirdTrack = swipRslt.getThirdTrackData();

				String secondTrackStr=(secondTrack == null ? "" : new String(secondTrack, "UTF-8"));//二磁道
				String thirdTrackStr=(thirdTrack == null ? "" : new String(thirdTrack, "UTF-8"));//三磁道
				Log.d("二磁道信息:", " =====" + secondTrackStr);
				Log.d("三磁道信息:", " =====" + thirdTrackStr);
				String cardNo = String.valueOf(swipRslt.getAccount().getAcctNo());//银行卡号
				Log.d("银行卡号", "====== " + cardNo);
				PosData.getPosData().setCardNo(cardNo);
				PosData.getPosData().setRate("1");
				PosData.getPosData().setTermType("01");

				PosData.getPosData().setTrack(secondTrackStr + "|" + thirdTrackStr);
				PosData.getPosData().setRandom("000000");
				PosData.getPosData().setPeriod(swipRslt.getValidDate());
				Log.d("银行卡有效期", "=====" + swipRslt.getValidDate());
				PosData.getPosData().setCrdnum(context.getCardSequenceNumber());
				PosData.getPosData().setIcdata(ISOUtils.hexString(tlvPackage.pack()));

				if (PosData.getPosData().getIcdata().equals("") || PosData.getPosData().getIcdata() == null) {
					PosData.getPosData().setMediaType("01");//磁卡类型  01 磁条卡 02 IC卡
				} else {
					PosData.getPosData().setMediaType("02");//磁卡类型  01 磁条卡 02 IC卡
				}
				onSuccess();
			}catch (Exception e){
				Log.d("异常信息:  ",""+e.toString());
				onFailed("交易失败");
			}
		} else {
			onFailed("交易失败");
		}
		}else{
			System.err.println("错误的qpboc状态返回！"+context.getExecuteRslt() );
			onFailed("刷卡失败");
		}
		swingcardbyxdlbluetootchactivity.processingUnLock();
	}

	@Override
	public void onEmvFinished(boolean isSuccess, EmvTransInfo context){
//		
		if (isSuccess) {
			TLVPackage tlvPackage = context.setExternalInfoPackage(L_55TAGS);
			Log.d("==============", "=======onEmvFinished=====");
			System.err.println(">>>>55域打包集合:" + ISOUtils.hexString(tlvPackage.pack()) );
			onSuccess();
		}
		swingcardbyxdlbluetootchactivity.processingUnLock();
	}

	@Override
	public void onError(EmvTransController arg0, Exception arg1){

		Log.d("==============", "=======onError=====");
		onFailed("交易失败");
		swingcardbyxdlbluetootchactivity.processingUnLock();
	}

	@Override
	public void onFallback(EmvTransInfo arg0){
		Log.d("==============", "=======onFallback=====");
//		startSwipTransfer();
		onFailed("交易失败");
		swingcardbyxdlbluetootchactivity.processingUnLock();
		
	}

	@Override
	public void onRequestOnline(EmvTransController controller, EmvTransInfo context) throws Exception {
		
		Log.d("==============", "=======onRequestOnline=====");
		
		onStatus();
		
		System.err.println(">>>>交易完成，交易结果(DF75):" );
		TLVPackage tlvPackage = context.setExternalInfoPackage(L_55TAGS);
		System.err.println(">>>>55域打包集合:" + ISOUtils.hexString(tlvPackage.pack()) );
		// 此处判断是开启度开启操作card_reader_flag=0，PBOC流程 card_reader_flag=1
		PosData.getPosData().setCardNo(context.getCardNo());
		PosData.getPosData().setRate("1");
		PosData.getPosData().setTermType("01");
		
		PosData.getPosData().setRandom("000000");
		String period = context.getCardExpirationDate().substring(0, 4);
		PosData.getPosData().setPeriod(period);
		PosData.getPosData().setCrdnum(context.getCardSequenceNumber());
		PosData.getPosData().setIcdata(ISOUtils.hexString(tlvPackage.pack()));
		if(PosData.getPosData().getIcdata().equals("")||PosData.getPosData().getIcdata() == null){
			PosData.getPosData().setMediaType("01");//磁卡类型01 磁条卡 02 IC卡
		}else{
			PosData.getPosData().setMediaType("02");//磁卡类型01 磁条卡 02 IC卡
		}
		
		// 获取IC卡磁道信息
		SwipResult swipResult = BuletootchControllerImpl.getInstance().getTrackText(Const.CardType.ICCARD);
		if (null != swipResult.getSecondTrackData()) {
			System.err.println(">>>>二磁道密文:" + ISOUtils.hexString(swipResult.getSecondTrackData()) );
			String secondTrackStr=(swipResult.getSecondTrackData() == null ? "" : new String(swipResult.getSecondTrackData(), "UTF-8"));//二磁道
			String thirdTrackStr=(swipResult.getThirdTrackData() == null ? "" : new String(swipResult.getThirdTrackData(), "UTF-8"));//三磁道
			PosData.getPosData().setTrack(secondTrackStr + "|"+thirdTrackStr);
			
		}
		PosData.getPosData().setSwipResult(swipResult);


		// todo !!!!!!!!!!从该处context中获取ic卡卡片信息后，发送银联8583交易

		SecondIssuanceRequest request = new SecondIssuanceRequest();
		request.setAuthorisationResponseCode("00");// 取自银联8583规范 39域值,该参数按交易实际值填充
		// request.setIssuerAuthenticationData(arg0);//取自银联8583规范 55域 0x91值,该参数按交易实际值填充
		// request.setIssuerScriptTemplate1(arg0);//取自银联8583规范 55域 0x71值,该参数按交易实际值填充
		// request.setIssuerScriptTemplate2(arg0);//取自银联8583规范 55域 0x72值,该参数按交易实际值填充
//			request.setAuthorisationCode(authorisationCode);//取自银联8583规范 38域值,该参数按交易实际值填充
		controller.secondIssuance(request);
	}

	
	@Override
	public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
		System.err.println("错误的事件返回，不可能要求密码输入！" );
		onFailed("错误的事件返回，不可能要求密码输入");
		arg0.cancelEmv();

	}

	@Override
	public void onRequestSelectApplication(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
		System.err.println("错误的事件返回，不可能要求应用选择！" );
		onFailed("错误的事件返回，不可能要求应用选择！");
		arg0.cancelEmv();

	}

	@Override
	public void onRequestTransferConfirm(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
		System.err.println("错误的事件返回，不可能要求交易确认！" );
		onFailed("错误的事件返回，不可能要求交易确认");
		arg0.cancelEmv();

	}

	@Override
	public void onOpenCardreaderCanceled() {
		System.err.println("用户撤销刷卡操作！" );
		onFailed("用户撤销刷卡操作");
		swingcardbyxdlbluetootchactivity.processingUnLock();
	}
	

}
