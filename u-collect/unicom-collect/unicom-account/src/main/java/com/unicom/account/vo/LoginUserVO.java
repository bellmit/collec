package com.unicom.account.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : yangpeng
 * @description : 登录用户VO
 * @create : 2021-10-12 14:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVO {
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private String name;

    /**
     * token
     */
    private String token;
}
