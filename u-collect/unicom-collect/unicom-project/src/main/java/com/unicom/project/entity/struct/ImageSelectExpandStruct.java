package com.unicom.project.entity.struct;

import lombok.Data;

import java.util.List;

/**
 * @author : yangpeng
 * @description : 颜色选择
 * @create : 2021-10-19 11:42
 **/
@Data
public class ImageSelectExpandStruct {

    /**
     * 选项
     */
    private List<Option> options;

    /**
     * 是否多选
     */
    private Boolean multiple;

    public static class Option {
        public String image;
        public String label;
        public Integer value;
    }

}
