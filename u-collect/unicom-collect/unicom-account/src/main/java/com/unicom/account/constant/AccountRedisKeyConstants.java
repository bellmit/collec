package com.unicom.account.constant;

/**
 * @description: redis key常量
 * @author: yangpeng
 * @create: 2021-10-12 22:34
 **/
public interface AccountRedisKeyConstants {
    /**
     * 邮箱验证码
     */
    String EMAIL_CODE = "user:email:code:{}";

    /**
     * 手机号验证码
     */
    String PHONE_NUMBER_CODE = "user:mobile:code:{}";

    /**
     * 手机找回密码验证码
     */
    String PHONE_RETRIEVE_PWD_CODE = "user:mobile:retrieve:pwd:code:{}";


    /**
     * 找回密码验证完成用户身份code
     */
    String RETRIEVE_PWD_USER_CODE = "user:retrieve:pwd:code:{}";

    /**
     * 修改邮箱验证
     */
    String UPDATE_USER_EMAIL_CODE = "user:email:update:code:{}:{}";
}
