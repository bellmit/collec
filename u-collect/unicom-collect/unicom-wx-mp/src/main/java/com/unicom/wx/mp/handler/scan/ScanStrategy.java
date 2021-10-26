package com.unicom.wx.mp.handler.scan;

import com.unicom.wx.mp.request.WxMpQrCodeGenRequest;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author : yangpeng
 * @description : 扫码处理基本
 * @create : 2021-10-01 17:45
 **/
public interface ScanStrategy {


    /***
     * 处理
     * @param request
     * @return
     */
    WxMpXmlOutMessage handle(String appId, String openId, WxMpQrCodeGenRequest request);

}
