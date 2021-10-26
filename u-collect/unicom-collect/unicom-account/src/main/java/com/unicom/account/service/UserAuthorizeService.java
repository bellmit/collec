package com.unicom.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unicom.account.entity.UserAuthorizeEntity;
import com.unicom.account.entity.UserEntity;
import com.unicom.account.entity.enums.UserAuthorizeTypeEnum;

/**
 * 第三方用户授权信息(AcUserAuthorize)表服务接口
 *
 * @author yangpeng
 * @since 2020-12-09 17:43:16
 */
public interface UserAuthorizeService extends IService<UserAuthorizeEntity> {


    /**
     * 获取qq 授权信息
     *
     * @param authorizeCode
     * @param redirectUri
     * @return
     */
    UserAuthorizeEntity getQqAuthorization(String authorizeCode, String redirectUri, UserEntity userEntity);


    /**
     * 获取
     *
     * @param type   类型
     * @param appId
     * @param openId
     * @return
     */
    UserAuthorizeEntity get(UserAuthorizeTypeEnum type, String appId, String openId);


    /**
     * 获取
     *
     * @param type   类型
     * @param userId 用户Id
     * @return
     */
    UserAuthorizeEntity getByUserId(UserAuthorizeTypeEnum type, Long userId);
}