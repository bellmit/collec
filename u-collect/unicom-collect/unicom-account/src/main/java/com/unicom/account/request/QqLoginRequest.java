package com.unicom.account.request;

import lombok.Data;

/**
 * @author : yangpeng
 * @description : qq登录
 * @create : 2021-10-09 18:30
 **/
@Data
public class QqLoginRequest {


    private String redirectUri;

    private String authorizeCode;

    /**
     * 请求Ip地址
     */
    private String requestIp;
}
