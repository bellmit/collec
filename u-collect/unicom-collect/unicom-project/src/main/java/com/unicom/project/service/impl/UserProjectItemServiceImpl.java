package com.unicom.project.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.UserProjectItemEntity;
import com.unicom.project.mapper.UserProjectItemMapper;
import com.unicom.project.service.UserProjectItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目表单项(ProjectItem)表服务实现类
 *
 * @author yangpeng
 * @since 2020-11-19 10:49:18
 */
@Service("userProjectItemService")
public class UserProjectItemServiceImpl extends ServiceImpl<UserProjectItemMapper, UserProjectItemEntity> implements UserProjectItemService {

    @Override
    public List<UserProjectItemEntity> listByProjectKey(String key) {
        return this.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, key).orderByAsc(UserProjectItemEntity::getSort));
    }
}