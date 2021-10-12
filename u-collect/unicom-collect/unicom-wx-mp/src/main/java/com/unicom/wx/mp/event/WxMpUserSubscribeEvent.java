package com.unicom.wx.mp.event;

import com.unicom.wx.mp.entity.WxMpUserEntity;
import com.unicom.wx.mp.request.WxMpQrCodeGenRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 微信用户订阅事件
 *
 * @author smalljop
 */
public class WxMpUserSubscribeEvent extends ApplicationEvent {

    @Getter
    private WxMpUserEntity wxMpUserEntity;


    @Getter
    private WxMpQrCodeGenRequest params;

    public WxMpUserSubscribeEvent(Object source, WxMpUserEntity wxMpUserEntity, WxMpQrCodeGenRequest params) {
        super(source);
        this.wxMpUserEntity = wxMpUserEntity;
        this.params = params;
    }
}