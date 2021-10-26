package com.unicom.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.ProjectTemplateCategoryEntity;
import com.unicom.project.mapper.ProjectTemplateCategoryMapper;
import com.unicom.project.service.ProjectTemplateCategoryService;
import org.springframework.stereotype.Service;

/**
 * 项目模板分类(ProjectTemplateType)表服务实现类
 *
 * @author yangpeng
 * @since 2021-01-06 10:51:06
 */
@Service("projectTemplateCategoryService")
public class ProjectTemplateCategoryServiceImpl extends ServiceImpl<ProjectTemplateCategoryMapper, ProjectTemplateCategoryEntity> implements ProjectTemplateCategoryService {

}