package com.unicom.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.ProjectThemeEntity;
import com.unicom.project.mapper.ProjectThemeMapper;
import com.unicom.project.service.ProjectThemeService;
import org.springframework.stereotype.Service;

/**
 * 项目主题外观模板(ProjectTheme)表服务实现类
 *
 * @author yangpeng
 * @since 2020-11-23 18:33:56
 */
@Service("projectThemeService")
public class ProjectThemeServiceImpl extends ServiceImpl<ProjectThemeMapper, ProjectThemeEntity> implements ProjectThemeService {

}