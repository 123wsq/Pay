package com.hx.newland.cashin.swing.xdl.bluetootch.swiper;

import com.hx.newland.cashin.swing.xdl.bluetootch.ModuleBase;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;

/**
 * Created by YJF 刷卡模块接口实现
 */
public class SwiperInterfaceImpl extends ModuleBase implements SwiperInterface {
	private Swiper swiper;
	SwipResult swipResult;

	public SwiperInterfaceImpl() {
		swiper = (Swiper) factory.getModule(ModuleType.COMMON_SWIPER);
	}

	//以掩码方式获取加密磁道信息
	@Override
	public SwipResult readEncryptResult(SwiperReadModel[] readModel, WorkingKey wk, String params1, byte[] acctMask, String params2) {
		swipResult = swiper.readEncryptResult(readModel, wk, params1, acctMask, params2);
		return swipResult;
	}

	//获取加密的磁道信息
	@Override
	public SwipResult readEncryptResult(SwiperReadModel[] readModel, WorkingKey wk, String params) {
		swipResult = swiper.readEncryptResult(readModel, wk, params);
		return swipResult;
	}

	//获取明文磁道信息
	@Override
	public SwipResult readPlainResult(SwiperReadModel[] readModel) {
		swipResult = swiper.readPlainResult(readModel);
		if (null != swipResult && swipResult.getRsltType() == SwipResultType.SUCCESS) {
			return swipResult;
		}
		return null;
	}

}
