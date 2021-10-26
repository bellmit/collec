package com.unicom.project.entity.struct;

import lombok.Data;

/**
 * @author : yangpeng
 * @description : 滑块
 * @create : 2021-10-19 15:13
 **/
@Data
public class SliderExpandStruct {
    /**
     * 最小
     */
    private Integer min;
    /**
     * 最大
     */
    private Integer max;
    /**
     * 计数器步长 每次+几
     */
    private Integer step;


    /**
     * 是否是范围选择
     */
    private Boolean range;

    /**
     * 按钮控制位置
     */
    private String controlsPosition;
}
