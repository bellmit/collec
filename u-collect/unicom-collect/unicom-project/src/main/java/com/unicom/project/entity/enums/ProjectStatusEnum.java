package com.unicom.project.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : yangpeng
 * @description : 项目状态
 * @create : 2021-9-17 14:25
 **/
@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum ProjectStatusEnum {

    CREATE(1, "未发布"),
    RELEASE(2, "收集中"),
    STOP(3, "停止发布");


    @EnumValue
    @JsonValue
    private int value;

    private String desc;


    /**
     * 枚举入参注解
     *
     * @param value
     * @return
     */
    @JsonCreator
    public static ProjectStatusEnum getByValue(int value) {
        for (ProjectStatusEnum statusEnum : values()) {
            if (statusEnum.getValue() == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
