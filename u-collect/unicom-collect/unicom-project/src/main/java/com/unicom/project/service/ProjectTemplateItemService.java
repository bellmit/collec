package com.unicom.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unicom.project.entity.ProjectTemplateItemEntity;

import java.util.List;

/**
 * 项目表单项(ProjectTemplateItem)表服务接口
 *
 * @author yangpeng
 * @since 2021-01-06 11:14:46
 */
public interface ProjectTemplateItemService extends IService<ProjectTemplateItemEntity> {

    /**
     * 根据key查询
     *
     * @param templateKey
     * @return
     */
    List<ProjectTemplateItemEntity> listByTemplateKey(String templateKey);

}