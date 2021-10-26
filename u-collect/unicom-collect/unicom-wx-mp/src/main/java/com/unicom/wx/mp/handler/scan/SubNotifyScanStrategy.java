package com.unicom.wx.mp.handler.scan;

import cn.hutool.core.util.StrUtil;
import com.unicom.common.util.RedisUtils;
import com.unicom.wx.mp.constant.WxMpRedisKeyConstants;
import com.unicom.wx.mp.request.WxMpQrCodeGenRequest;
import com.unicom.wx.mp.service.WxMpUserMsgService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

/**
 * @author : yangpeng
 * @description : 订阅项目通知
 * @create : 2021-10-01 17:44
 **/
@Component
@RequiredArgsConstructor
public class SubNotifyScanStrategy implements ScanStrategy {

    private final RedisUtils redisUtils;
    private final WxMpUserMsgService wxMpUserMsgService;

    @Override
    public WxMpXmlOutMessage handle(String appId, String openId, WxMpQrCodeGenRequest request) {
        redisUtils.add(StrUtil.format(WxMpRedisKeyConstants.WX_MP_SUB_NOTIFY, request.getData()), openId);
        wxMpUserMsgService.sendKfTextMsg(appId, openId, "订阅通知成功");
        return null;
    }
}
