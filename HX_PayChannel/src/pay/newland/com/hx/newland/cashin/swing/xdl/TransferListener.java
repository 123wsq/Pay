package com.hx.newland.cashin.swing.xdl;

import com.newland.mtype.module.common.emv.EmvControllerListener;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.swiper.SwipResult;

public interface TransferListener extends EmvControllerListener{
	public void onSwipMagneticCard(SwipResult swipRslt);
	public void onOpenCardreaderCanceled();
	public void onQpbocFinished(EmvTransInfo emvTransInfo);
}
