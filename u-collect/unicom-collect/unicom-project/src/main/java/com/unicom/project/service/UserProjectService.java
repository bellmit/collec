package com.unicom.project.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicom.project.entity.UserProjectEntity;

import java.util.Map;

/**
 * 项目表(Project)表服务接口
 *
 * @author yangpeng
 * @since 2020-11-18 18:16:18
 */
public interface UserProjectService extends IService<UserProjectEntity> {


    /**
     * 根据key获取
     *
     * @param key
     * @return
     */
    UserProjectEntity getByKey(final String key);


    Object  page2(Page  page, Wrapper queryWrapper,Object orgId);








}