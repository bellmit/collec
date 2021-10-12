package com.unicom.wx.mp.handler.scan;

import cn.hutool.core.util.ObjectUtil;
import com.unicom.wx.mp.entity.WxMpUserEntity;
import com.unicom.wx.mp.request.WxMpQrCodeGenRequest;
import com.unicom.wx.mp.service.WxMpUserMsgService;
import com.unicom.wx.mp.service.WxMpUserService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

/**
 * @author : smalljop
 * @description : 订阅项目通知
 * @create : 2020-12-01 17:44
 **/
@Component
@RequiredArgsConstructor
public class BindAccountScanStrategy implements ScanStrategy {

    private final WxMpUserService wxMpUserService;
    private final WxMpUserMsgService wxMpUserMsgService;

    @Override
    public WxMpXmlOutMessage handle(String appId, String openId, WxMpQrCodeGenRequest request) {
        Long userId = Long.parseLong(request.getData());
        WxMpUserEntity wxMpUserEntity = wxMpUserService.getByAppIdAndOpenId(appId, openId);
        if (ObjectUtil.isNotNull(wxMpUserEntity)) {
            Long wxUserId = wxMpUserEntity.getUserId();
            if (ObjectUtil.isNull(wxUserId) || userId.equals(wxUserId)) {
                wxMpUserEntity.setUserId(userId);
                wxMpUserService.updateById(wxMpUserEntity);
                wxMpUserMsgService.sendKfTextMsg(appId, openId, "绑定账号成功");
            } else {
                wxMpUserMsgService.sendKfTextMsg(appId, openId, "绑定失败，该微信已经绑定其他账号，请用微信扫码登录");
            }
        }

        return null;
    }
}
