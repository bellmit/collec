package com.unicom.collect.exception;

import com.unicom.common.exception.BaseException;

/**
 * @author : yangpeng
 * @description : 授权异常
 * @create : 2020-11-27 14:37
 **/
public class AuthorizationException extends BaseException {
    public AuthorizationException(String msg) {
        super(msg);
    }
}
