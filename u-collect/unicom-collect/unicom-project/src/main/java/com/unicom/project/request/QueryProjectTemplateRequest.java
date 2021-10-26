package com.unicom.project.request;

import com.unicom.common.entity.PageRequest;
import lombok.Data;

/**
 * @author : yangpeng
 * @description : 查询项目模板
 * @create : 2021-10-10 15:04
 **/
@Data
public class QueryProjectTemplateRequest {


    /**
     * 分页查询
     */
    @Data
    public static class Page extends PageRequest {

        private String name;

        private Long type;
    }

}
