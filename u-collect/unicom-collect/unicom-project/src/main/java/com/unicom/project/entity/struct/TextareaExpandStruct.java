package com.unicom.project.entity.struct;

import lombok.Data;

/**
 * @author : yangpeng
 * @description : 多行文本
 * @create : 2021-10-19 11:42
 **/
@Data
public class TextareaExpandStruct {

    /**
     * 最大行数
     */
    public Integer maxRows;
    /**
     * 最小行数
     */
    public Integer minRows;

    /**
     * 最大长度
     */
    private Long maxlength;

}
