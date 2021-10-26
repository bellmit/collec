package com.unicom.project.entity.struct;

import lombok.Data;

import java.util.List;

/**
 * @author : yangpeng
 * @description : 下拉框
 * @create : 2021-10-19 15:13
 **/
@Data
public class SelectExpandStruct {
    /**
     * 选项
     */
    private List<Option> options;
    /**
     * 最大
     */
    private Boolean multiple;

    /**
     * 塞选
     */
    private Boolean filterable;
    /**
     * 是否只能输入 step 的倍数
     */
    private Boolean stepStrictly;

    /**
     * 经度
     */
    private Integer precision;

    /**
     * 按钮控制位置
     */
    private String controlsPosition;


    public static class Option {
        public String label;
        public Integer value;
    }
}
