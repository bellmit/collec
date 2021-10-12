package com.unicom.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unicom.account.entity.UserEntity;
import com.unicom.account.entity.enums.AccountChannelEnum;
import com.unicom.account.request.AccountLoginRequest;
import com.unicom.account.request.QqLoginRequest;
import com.unicom.account.request.RegisterAccountRequest;
import com.unicom.account.vo.LoginUserVO;
import com.unicom.common.util.Result;

/**
 * 用户(AcUser)表服务接口
 *
 * @author smalljop
 * @since 2020-11-10 18:10:42
 */
public interface UserService extends IService<UserEntity> {


    /**
     * 邮箱注册
     *
     * @param request
     * @return
     */
    Result emailRegister(RegisterAccountRequest request);

    /**
     * 手机号注册
     *
     * @param request
     * @return
     */
    Result phoneRegister(RegisterAccountRequest request);

    /**
     * 账号登录
     *
     * @param request
     * @return
     */
    Result accountLogin(AccountLoginRequest request);


    /**
     * 获取登录结果
     *
     * @param userEntity
     * @param channel
     * @param requestIp
     * @return
     */
    LoginUserVO getLoginResult(UserEntity userEntity, AccountChannelEnum channel, String requestIp);


    /**
     * qq登录
     *
     * @return
     */
    LoginUserVO qqLogin(QqLoginRequest request);


    /**
     * 根据邮箱获取
     *
     * @param email
     * @return
     */
    UserEntity getUserByEmail(String email);

    /**
     * 根据手机号获取
     *
     * @param phoneNumber
     * @return
     */
    UserEntity getUserByPhoneNumber(String phoneNumber);

    /***
     * 更新密码
     * @param userId
     * @param password
     * @return
     */
    Boolean updatePassword(Long userId, String password);
}
