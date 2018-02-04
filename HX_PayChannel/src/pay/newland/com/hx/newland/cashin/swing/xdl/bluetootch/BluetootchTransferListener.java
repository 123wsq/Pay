package com.hx.newland.cashin.swing.xdl.bluetootch;

import com.newland.mtype.module.common.emv.EmvControllerListener;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.swiper.SwipResult;

import java.math.BigDecimal;

/**
 * 交易过程监听接口
 *
 * @author evil
 */
public interface BluetootchTransferListener extends EmvControllerListener {

    /**
     * 当撤销刷卡时触发
     */
    public void onOpenCardreaderCanceled();

   

    /**
     * 当qpboc流程结束时触发
     *
     * @param emvTransInfo emv数据对象
     */
    public void onQpbocFinished(EmvTransInfo emvTransInfo) throws Exception;
    
    
    public void onSuccess();
    
    public void onFailed( String error);
    
    
    public void onStatus();
    
}
