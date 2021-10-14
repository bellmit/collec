package com.unicom.collect.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.unicom.common.util.Result;
import com.unicom.common.validator.ValidatorUtils;
import com.unicom.common.validator.group.AddGroup;
import com.unicom.project.constant.ProjectRedisKeyConstants;
import com.unicom.project.entity.ProjectTemplateCategoryEntity;
import com.unicom.project.entity.ProjectTemplateEntity;
import com.unicom.project.entity.ProjectTemplateItemEntity;
import com.unicom.project.entity.UserProjectItemEntity;
import com.unicom.project.request.OperateProjectItemRequest;
import com.unicom.project.request.QueryProjectTemplateRequest;
import com.unicom.project.request.QueryProjectTemplateTypeRequest;
import com.unicom.project.service.ProjectTemplateCategoryService;
import com.unicom.project.service.ProjectTemplateItemService;
import com.unicom.project.service.ProjectTemplateService;
import com.unicom.project.util.SortUtils;
import com.unicom.project.vo.OperateProjectItemVO;
import com.unicom.project.vo.ProjectTemplateDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * yangpeng
 * @description :
 * @create :  2021/9/17 10:54
 **/
@RequiredArgsConstructor
@RestController
@Slf4j
public class ProjectTemplateController {

    private final ProjectTemplateService projectTemplateService;
    private final ProjectTemplateCategoryService projectTemplateCategoryService;
    private final ProjectTemplateItemService projectTemplateItemService;
    private final SortUtils sortUtils;

    /**
     * 创建项目模板
     *
     * @return
     */
    @RequiresPermissions(value = {"template:add"})
    @PostMapping("/project/template/create")
    public Result createProjectTemplate(@RequestBody ProjectTemplateEntity templateEntity) {
        ValidatorUtils.validateEntity(templateEntity, AddGroup.class);
        templateEntity.setKey(IdUtil.simpleUUID());
        projectTemplateService.save(templateEntity);
        return Result.success(templateEntity.getKey());
    }


    /**
     * 分页查询项目分类
     *
     * @return
     */
    @RequiresPermissions(value = {"template:list"})
    @GetMapping("/project/template/type/list")
    public Result queryProjectTemplateTypes(QueryProjectTemplateTypeRequest.List request) {
        return Result.success(projectTemplateCategoryService.list(Wrappers.<ProjectTemplateCategoryEntity>lambdaQuery()
                .orderByDesc(ProjectTemplateCategoryEntity::getSort)));
    }

    /**
     * 分页查询模板
     *
     * @return
     */
    @RequiresPermissions(value = {"template:list"})
    @GetMapping("/project/template/page")
    public Result queryProjectTemplates(QueryProjectTemplateRequest.Page request) {
        return Result.success(projectTemplateService.page(request.toMybatisPage(),
                Wrappers.<ProjectTemplateEntity>lambdaQuery()
                        .eq(ObjectUtil.isNotNull(request.getType()), ProjectTemplateEntity::getCategoryId, request.getType())
                        .like(StrUtil.isNotBlank(request.getName()), ProjectTemplateEntity::getName, request.getName())
                        .orderByDesc(ProjectTemplateEntity::getUpdateTime)));
    }


    /**
     * 查询项目详情
     * 包含项目信息 项目保单项信息
     *
     * @param key
     * @return
     */
    @GetMapping("/project/template/details/{key}")
    public Result queryProjectTemplateDetails(@PathVariable @NotBlank String key) {
        ProjectTemplateEntity templateEntity = projectTemplateService.getByKey(key);
        List<ProjectTemplateItemEntity> projectItemList = projectTemplateItemService.listByTemplateKey(key);
        return Result.success(new ProjectTemplateDetailVO(templateEntity, projectItemList));
    }

    /**
     * 项目模板表单项创建
     *
     * @param request
     * @return
     */
    @RequiresPermissions(value = {"template:add"})
    @PostMapping("/project/template/item/create")
    public Result createProjectTemplateItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, AddGroup.class);
        //把Map转换成Bean 在转换成Map 去除不在bean字段列表的多字段
        Object bean = BeanUtil.toBeanIgnoreCase(request.getExpand(), request.getType().getExpandClass(), true);
        ProjectTemplateItemEntity entity = new ProjectTemplateItemEntity();
        BeanUtil.copyProperties(request, entity, UserProjectItemEntity.Fields.defaultValue);
        entity.setExpand(BeanUtil.beanToMap(bean));
        //排序下标计算
        entity.setSort(sortUtils.getInitialSortPosition(
                StrUtil.format(ProjectRedisKeyConstants.PROJECT_ITEM_POS_DELTA, request.getProjectKey())));
        boolean save = projectTemplateItemService.save(entity);
        return Result.success(new OperateProjectItemVO(entity.getSort(), entity.getId(), save));
    }
}
