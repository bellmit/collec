package com.unicom.wx.mp.service;

/**
 * 公众号用户消息
 *
 * @author : yangpeng
 * @description :
 * @create : 2021-10-02 10:34
 **/
public interface WxMpUserMsgService {

    /**
     * 发送文本客服消息
     *
     * @param appId
     * @param openId
     * @param content
     */
    void sendKfTextMsg(String appId, String openId, String content);


}
