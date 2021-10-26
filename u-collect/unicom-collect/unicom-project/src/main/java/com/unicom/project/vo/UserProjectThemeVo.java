package com.unicom.project.vo;

import com.unicom.project.entity.UserProjectThemeEntity;
import lombok.Data;

/**
 * @author : yangpeng
 * @description : 项目主题
 * @create : 2021-10-25 17:33
 **/
@Data
public class UserProjectThemeVo extends UserProjectThemeEntity {

    /**
     * 头部图片
     */
    private String headImgUrl;


    /**
     * 按钮颜色
     */
    private String btnsColor;


}
