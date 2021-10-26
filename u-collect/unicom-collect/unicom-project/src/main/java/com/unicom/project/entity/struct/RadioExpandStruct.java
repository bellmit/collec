package com.unicom.project.entity.struct;

import lombok.Data;

import java.util.List;

/**
 * @author : yangpeng
 * @description : d
 * @create : 2021-10-19 11:42
 **/
@Data
public class RadioExpandStruct {

    /**
     * 选项样式
     */
    private String optionType;


    /**
     * 选项
     */
    private List<Option> options;


    public static class Option {
        public String label;
        public Integer value;
    }
}
