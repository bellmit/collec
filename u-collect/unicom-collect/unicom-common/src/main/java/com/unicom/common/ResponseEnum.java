package com.unicom.common;

/**
 * @description:
 * @author: 周恒晟
 * @date: 2021-04-12 16:50
 */
public enum ResponseEnum {
    ;

    private final Integer code;
    private final String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
