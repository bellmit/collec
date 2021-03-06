package com.unicom.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicom.account.entity.enums.AccountChannelEnum;
import com.unicom.common.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户(AcUser)表实体类
 *
 * @author smalljop
 * @since 2020-11-10 18:10:40
 */
@Data
@TableName("ac_user")
public class UserEntity extends BaseEntity<UserEntity> {
    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;

    /**
     * 注册渠道
     */
    private AccountChannelEnum regChannel;
    /**
     * 最后登录渠道
     */
    private AccountChannelEnum lastLoginChannel;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 最后登录Ip
     */
    private String lastLoginIp;
    /**
     * 状态
     */
    private Boolean deleted;


}