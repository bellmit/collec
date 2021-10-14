package com.unicom.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicom.common.entity.BaseEntity;
import com.unicom.common.mybatis.handler.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 项目表单项(ProjectResult)表实体类
 *
 * @author yangpeng
 * @since 2021-09-23 14:09:20
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName(value = "pr_user_project_result", autoResultMap = true)
//此处注释，不允许被序列化 允许被反序列化
@JsonIgnoreProperties(value = {UserProjectResultEntity.Fields.originalData}, allowSetters = true)
public class UserProjectResultEntity extends BaseEntity<UserProjectResultEntity> {
    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 项目key
     */
    @NotBlank(message = "错误请求")
    private String projectKey;


    private Long serialNumber;

    private Long hUserId;

    private String name;


    private Long orgId;


    /**
     * 填写结果原始数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> originalData;


    /**
     * 填写结果处理后数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> processData;

    /**
     * 填写用户Ua
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> submitUa;

    /**
     * 提交系统
     */
    private String submitOs;


    /**
     * 提交浏览器
     */
    private String submitBrowser;

    /**
     * 提交ip
     */
    private String submitRequestIp;


    /**
     * 提交ip
     */
    private String submitAddress;

    /**
     * 完成时间
     */
    private Long completeTime;

    /**
     * 微信openID
     */
    private String wxOpenId;

    /**
     * 微信用户信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> wxUserInfo;
}