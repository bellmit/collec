package com.unicom.project.entity.struct;

import lombok.Data;

/**
 * @author : yangpeng
 * @description : 日期选择
 * @create : 2021-10-19 11:42
 **/
@Data
public class DateExpandStruct {

    // 日期类型
    private String type;
    // 日期格式
    private String format;
    // 日期格式
    private String valueFormat;

}
