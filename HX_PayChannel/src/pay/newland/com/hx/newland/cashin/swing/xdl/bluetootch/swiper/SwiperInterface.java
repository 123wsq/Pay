package com.hx.newland.cashin.swing.xdl.bluetootch.swiper;

import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwiperReadModel;


/**
 * Created by HJP on 2015/8/12.
 */
public interface SwiperInterface {
	public SwipResult readEncryptResult(SwiperReadModel[] readModel, WorkingKey wk, String paramsString, byte[] acctMask, String paramsString2);// 读取加密的磁道信息

	public SwipResult readEncryptResult(SwiperReadModel[] readModel, WorkingKey wk, String paramString);

	public SwipResult readPlainResult(SwiperReadModel[] readModel);// 通过安全认证后，使用明文方式返回刷卡结果

}
