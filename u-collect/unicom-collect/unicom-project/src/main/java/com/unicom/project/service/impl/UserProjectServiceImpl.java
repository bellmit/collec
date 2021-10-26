package com.unicom.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.UserProjectEntity;
import com.unicom.project.mapper.UserProjectMapper;
import com.unicom.project.service.UserProjectService;
import com.unicom.utils.ResponseUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 项目表(Project)表服务实现类
 *
 * @author yangpeng
 * @since 2020-11-18 18:16:18
 */
@Service("userProjectService")
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProjectEntity> implements UserProjectService {

    @Override
    public UserProjectEntity getByKey(final String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        UserProjectEntity entity = this.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, key));
        return entity;
    }

    @Override
    public Object page2(Page page, Wrapper queryWrapper,Object orgId) {

        IPage<Map<String, Object>>  p=this.getBaseMapper().pageProject(page,queryWrapper,orgId);



        return p;
    }

}