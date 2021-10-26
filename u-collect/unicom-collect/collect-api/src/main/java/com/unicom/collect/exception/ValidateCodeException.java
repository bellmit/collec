package com.unicom.collect.exception;

import com.unicom.common.exception.BaseException;

/**
 * @author : yangpeng
 * @description : 验证码错误
 * @create : 2021-10-14 16:00
 **/
public class ValidateCodeException extends BaseException {
    public ValidateCodeException(String msg) {
        super(msg);
    }
}
