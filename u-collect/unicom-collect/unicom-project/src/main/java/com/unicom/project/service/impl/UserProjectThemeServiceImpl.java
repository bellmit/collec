package com.unicom.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.ProjectThemeEntity;
import com.unicom.project.entity.UserProjectThemeEntity;
import com.unicom.project.mapper.UserProjectThemeMapper;
import com.unicom.project.service.ProjectThemeService;
import com.unicom.project.service.UserProjectThemeService;
import com.unicom.project.vo.UserProjectThemeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目表单项(UserProjectTheme)表服务实现类
 *
 * @author smalljop
 * @since 2020-11-25 13:36:32
 */
@Service("userProjectThemeService")
@RequiredArgsConstructor
public class UserProjectThemeServiceImpl extends ServiceImpl<UserProjectThemeMapper, UserProjectThemeEntity> implements UserProjectThemeService {

    private final ProjectThemeService projectThemeService;

    @Override
    public UserProjectThemeVo getUserProjectDetails(String key) {
        UserProjectThemeEntity userProjectThemeEntity = this.getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, key));
        if (ObjectUtil.isNotNull(userProjectThemeEntity)) {
            UserProjectThemeVo vo = new UserProjectThemeVo();
            BeanUtil.copyProperties(userProjectThemeEntity, vo);
            ProjectThemeEntity themeEntity = projectThemeService.getById(userProjectThemeEntity.getThemeId());
            BeanUtil.copyProperties(themeEntity, vo);
            return vo;
        }
        return null;
    }
}