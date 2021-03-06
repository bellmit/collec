package com.unicom.collect.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import com.unicom.collect.annotation.Login;
import com.unicom.collect.util.HttpUtils;
import com.unicom.common.IConstants;
import com.unicom.common.constant.CommonConstants;
import com.unicom.common.entity.BaseEntity;
import com.unicom.common.util.JsonUtils;
import com.unicom.common.util.RedisUtils;
import com.unicom.common.util.Result;
import com.unicom.common.validator.ValidatorUtils;
import com.unicom.common.validator.group.AddGroup;
import com.unicom.common.validator.group.UpdateGroup;
import com.unicom.project.constant.ProjectRedisKeyConstants;
import com.unicom.project.entity.*;
import com.unicom.project.entity.enums.ProjectSourceTypeEnum;
import com.unicom.project.entity.enums.ProjectStatusEnum;
import com.unicom.project.entity.struct.ItemDefaultValueStruct;
import com.unicom.project.request.OperateProjectItemRequest;
import com.unicom.project.request.QueryProjectItemRequest;
import com.unicom.project.request.QueryProjectRequest;
import com.unicom.project.request.SortProjectItemRequest;
import com.unicom.project.service.*;
import com.unicom.project.util.SortUtils;
import com.unicom.project.vo.OperateProjectItemVO;
import com.unicom.project.vo.RecycleProjectVO;
import com.unicom.project.vo.UserProjectDetailVO;
import com.unicom.project.vo.UserProjectThemeVo;
import com.unicom.wx.mp.constant.WxMpRedisKeyConstants;
import com.unicom.wx.mp.request.WxMpQrCodeGenRequest;
import com.unicom.wx.mp.service.WxMpUserService;
import com.unicom.wx.mp.vo.WxMpUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : yangpeng
 * @description : ?????????
 * @create : 2021-9-18 18:17
 **/
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserProjectController {

    private final UserProjectService projectService;
    private final UserProjectItemService projectItemService;
    private final UserProjectResultService projectResultService;
    private final SortUtils sortUtils;
    private final UserProjectThemeService userProjectThemeService;
    private final UserProjectSettingService userProjectSettingService;
    private final ProjectTemplateService projectTemplateService;
    private final ProjectTemplateItemService projectTemplateItemService;
    private final WxMpUserService wxMpUserService;
    private final RedisUtils redisUtils;
    private final WxMpService wxMpService;


    /**
     * ????????????
     */
    //@Login
    @RequiresPermissions(value = {"project:add"})
    @PostMapping("/user/project/create")
    public Result createProject(@RequestBody UserProjectEntity project) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        ValidatorUtils.validateEntity(project, AddGroup.class);
        project.setKey(IdUtil.fastSimpleUUID());
        project.setOrgId(user.get("orgId"));
        project.setUserId(Long.parseLong(user.get("id").toString()));
        project.setStatus(ProjectStatusEnum.CREATE);
        project.setSourceType(ProjectSourceTypeEnum.BLANK);
        projectService.save(project);
        return Result.success(project.getKey());
    }


    /**
     * ?????????????????????
     */
    @RequiresPermissions(value = {"project:add"})
    @Login
    @PostMapping("/user/project/use-template/create")
    public Result createProjectByTemplate(@RequestBody ProjectTemplateEntity request, @RequestAttribute Long userId) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        String templateKey = request.getKey();
        ProjectTemplateEntity projectTemplateEntity = projectTemplateService.getByKey(templateKey);
        List<ProjectTemplateItemEntity> projectTemplateItemEntities = projectTemplateItemService.listByTemplateKey(templateKey);
        UserProjectEntity userProjectEntity = new UserProjectEntity();
        BeanUtil.copyProperties(projectTemplateEntity, userProjectEntity, UserProjectEntity.Fields.status);
        userProjectEntity.setSourceType(ProjectSourceTypeEnum.TEMPLATE);
        userProjectEntity.setSourceId(projectTemplateEntity.getId().toString());
        userProjectEntity.setKey(IdUtil.fastSimpleUUID());
        userProjectEntity.setUserId(userId);
        userProjectEntity.setOrgId(user.get("orgId"));
        userProjectEntity.setStatus(ProjectStatusEnum.CREATE);
        projectService.save(userProjectEntity);
        List<UserProjectItemEntity> userProjectItemEntityList = JsonUtils.jsonToList(JsonUtils.objToJson(projectTemplateItemEntities), UserProjectItemEntity.class);
        userProjectItemEntityList.forEach(item -> item.setProjectKey(userProjectEntity.getKey()));
        projectItemService.saveBatch(userProjectItemEntityList);
        return Result.success(userProjectEntity.getKey());
    }


    /**
     * ????????????????????????
     *
     * @param request
     * @param userId
     * @return
     */
    @RequiresPermissions(value = {"template:add"})
    @Login
    @PostMapping("/user/project/template/save")
    public Result saveAsProjectTemplate(@RequestBody UserProjectEntity request, @RequestAttribute Long userId) {
        UserProjectEntity projectEntity = projectService.getByKey(request.getKey());
        List<UserProjectItemEntity> itemEntityList = projectItemService.listByProjectKey(request.getKey());
        ProjectTemplateEntity projectTemplateEntity = new ProjectTemplateEntity();
        BeanUtil.copyProperties(projectEntity, projectTemplateEntity, UserProjectEntity.Fields.status);
        projectTemplateEntity.setKey(IdUtil.fastSimpleUUID());
        projectTemplateEntity.setCategoryId(CommonConstants.ConstantNumber.FOUR.longValue());
        projectTemplateService.save(projectTemplateEntity);
        List<ProjectTemplateItemEntity> projectTemplateItemList = JsonUtils.jsonToList(JsonUtils.objToJson(itemEntityList), ProjectTemplateItemEntity.class);
        projectTemplateItemList.forEach(item -> item.setProjectKey(projectTemplateEntity.getKey()));
        projectTemplateItemService.saveBatch(projectTemplateItemList);
        return Result.success(projectTemplateEntity.getKey());
    }


    /**
     * ??????????????????????????????
     */
    @Login
    @RequiresPermissions(value = {"project:list"})
    @GetMapping("/user/project/list")
    public Result listProjects(QueryProjectRequest.List request, @RequestAttribute Long userId) {
        List<UserProjectEntity> entityList = projectService.list(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getUserId, userId)
                .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
                .orderByDesc(BaseEntity::getUpdateTime));
        return Result.success(entityList);
    }

    /**
     * ????????????????????????
     */
    //@Login
    @RequiresPermissions(value = {"project:list"})
    @GetMapping("/user/project/page")
    public Result queryMyProjects( QueryProjectRequest.Page request) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);

//        return Result.success(projectService.page(request.toMybatisPage(),
//                Wrappers.<UserProjectEntity>lambdaQuery()
//                        .eq(ObjectUtils.isNotEmpty(user.get("orgId")),UserProjectEntity::getOrgId,user.get("orgId"))
//                        .eq(UserProjectEntity::getDeleted, false)
//                        .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
//                        .like(StrUtil.isNotBlank(request.getName()), UserProjectEntity::getName, request.getName())
//                        .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectEntity::getUpdateTime, request.getEndDateTime())
//                        .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectEntity::getUpdateTime, request.getBeginDateTime())
//                        .orderByDesc(BaseEntity::getCreateTime)));

        return Result.success(projectService.page2(request.toMybatisPage(),
                Wrappers.<UserProjectEntity>lambdaQuery()
                        .eq(ObjectUtils.isNotEmpty(request.getOrgId()),UserProjectEntity::getOrgId,request.getOrgId())
                        .eq(UserProjectEntity::getDeleted, false)
                        .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
                        .like(StrUtil.isNotBlank(request.getName()), UserProjectEntity::getName, request.getName())
                        .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectEntity::getUpdateTime, request.getEndDateTime())
                        .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectEntity::getUpdateTime, request.getBeginDateTime())
                        .orderByDesc(BaseEntity::getCreateTime),user.get("orgId")));
    }



    /**
     * ????????????????????????
     */
    //@Login
    @RequiresPermissions(value = {"project:list"})
    @GetMapping("/user/project/page2")
    public Result queryMyProjects2( QueryProjectRequest.Page request) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        if(user==null)
            user=new HashMap<>();
        return Result.success(projectService.page2(request.toMybatisPage(),
                Wrappers.<UserProjectEntity>lambdaQuery()
                        .eq(ObjectUtils.isNotEmpty(request.getOrgId()),UserProjectEntity::getOrgId,request.getOrgId())
                        .or(i->i.eq(UserProjectEntity::getStatus,2).or().eq(UserProjectEntity::getStatus,3))
                        .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
                        .eq(UserProjectEntity::getDeleted, false)
                        .like(StrUtil.isNotBlank(request.getName()), UserProjectEntity::getName, request.getName())
                        .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectEntity::getUpdateTime, request.getEndDateTime())
                        .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectEntity::getUpdateTime, request.getBeginDateTime())
                        .orderByAsc(UserProjectEntity::getStatus).orderByDesc(BaseEntity::getCreateTime),user.get("orgId")));
    }


    /**
     * ????????????
     */
    @GetMapping("/user/project/{key}")
    public Result queryProjectByKey(@PathVariable @NotBlank String key) {
        return Result.success(projectService.getByKey(key));
    }


    /**
     * ????????????
     */
    @RequiresPermissions(value = {"project:publish"})
    @Login
    @PostMapping("/user/project/publish")
    public Result publishProject(@RequestBody UserProjectEntity request) {
        int count = projectItemService
                .count(Wrappers.<UserProjectItemEntity>lambdaQuery().eq(UserProjectItemEntity::getProjectKey, request.getKey()));
        if (count == CommonConstants.ConstantNumber.ZERO) {
            return Result.failed("?????????????????????????????????");
        }
        UserProjectEntity entity = projectService.getByKey(request.getKey());
        entity.setStatus(ProjectStatusEnum.RELEASE);
        return Result.success(projectService.updateById(entity));
    }

    /**
     * ????????????
     *
     * @param request
     */
    @RequiresPermissions(value = {"project:stop"})
    @Login
    @PostMapping("/user/project/stop")
    public Result stopProject(@RequestBody UserProjectEntity request) {
        UserProjectEntity entity = projectService.getByKey(request.getKey());
        entity.setStatus(ProjectStatusEnum.STOP);
        return Result.success(projectService.updateById(entity));
    }

    /**
     * ????????????
     *
     * @param request
     */
    @RequiresPermissions(value = {"project:delete"})
    @Login
    @PostMapping("/user/project/delete")
    public Result deleteProject(@RequestBody UserProjectEntity request) {
        boolean del = projectService.update(
                new UserProjectEntity() {{
                    setDeleted(Boolean.TRUE);
                }},
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, request.getKey()));
        return Result.success(del);
    }


    /**
     * ??????????????????
     * ?????????????????? ????????????????????? ????????????
     *
     * @param key
     */
    @RequiresPermissions(value = {"project:preview","project:list"},logical = Logical.AND)
    @GetMapping("/user/project/details/{key}")
    public Result queryProjectDetails(@PathVariable @NotBlank String key) {
        UserProjectEntity project = projectService.getByKey(key);
        List<UserProjectItemEntity> projectItemList = projectItemService.listByProjectKey(key);
        UserProjectThemeVo themeVo = userProjectThemeService.getUserProjectDetails(key);
        return Result.success(new UserProjectDetailVO(project, projectItemList, themeVo));
    }


    /**
     * ????????????
     *
     * @param project
     *
     */
    @RequiresPermissions(value = {"project:update"})
    //@Login
    @PostMapping("/user/project/update")
    public Result updateProject(@RequestBody UserProjectEntity project) {
        ValidatorUtils.validateEntity(project, AddGroup.class);
        UserProjectEntity oldProject = projectService.getByKey(project.getKey());
        if (ObjectUtil.isNotNull(oldProject) ) {
            project.setId(oldProject.getId());
            projectService.updateById(project);
        }
        return Result.success();
    }

    /**
     * ???????????????Id
     */

    @GetMapping("/user/project/item/max-form-id")
    public Result queryProjectMaxFormItemId(@RequestParam @NotBlank String key) {
        UserProjectItemEntity entity = projectItemService.getOne(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, key).select().orderByDesc(UserProjectItemEntity::getFormItemId).last("limit 1"));
        return Result.success(ObjectUtil.isNotNull(entity) ? entity.getFormItemId() : null);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/user/project/item/list")
    public Result queryProjectItem(QueryProjectItemRequest request) {
        ValidatorUtils.validateEntity(request);
        List<UserProjectItemEntity> itemEntityList = projectItemService.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getKey())
                .eq(ObjectUtil.isNotNull(request.getDisplayType()), UserProjectItemEntity::getDisplayType, request.getDisplayType())
                .orderByAsc(UserProjectItemEntity::getSort)
        );
        return Result.success(itemEntityList);
    }


    /**
     * ?????????????????????
     *
     * @param request
     */

    @RequiresPermissions(value = {"project:add","project:update"},logical = Logical.OR)
    @PostMapping("/user/project/item/create")
    public Result createProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, AddGroup.class);
        UserProjectItemEntity entity = formatProjectItem(request);
        //??????????????????
        entity.setSort(sortUtils.getInitialSortPosition(
                StrUtil.format(ProjectRedisKeyConstants.PROJECT_ITEM_POS_DELTA, request.getProjectKey())));
        boolean save = projectItemService.save(entity);
        return Result.success(new OperateProjectItemVO(entity.getSort(), entity.getId(), save));
    }


    /**
     * ???????????????Item?????????
     */
    private UserProjectItemEntity formatProjectItem(OperateProjectItemRequest request) {
        //???Map?????????Bean ????????????Map ????????????bean????????????????????????
        Object bean = BeanUtil.toBeanIgnoreCase(request.getExpand(), request.getType().getExpandClass(), false);
        UserProjectItemEntity entity = new UserProjectItemEntity();
        BeanUtil.copyProperties(request, entity, UserProjectItemEntity.Fields.defaultValue);
        entity.setExpand(BeanUtil.beanToMap(bean));
        //?????????????????? 1???????????????Json
        Object defaultValue = request.getDefaultValue();
        if (ObjectUtil.isNotEmpty(defaultValue)) {
            boolean json = JSONUtil.isJson(JsonUtils.objToJson(request.getDefaultValue()));
            if (json) {
                entity.setDefaultValue(new ItemDefaultValueStruct(true, JsonUtils.objToJson(request.getDefaultValue())));
            }
        }
        entity.setDefaultValue(new ItemDefaultValueStruct(false, defaultValue));
        return entity;
    }


    /**
     * ???????????????
     *
     * @param request
     */
    @RequiresPermissions(value = {"project:add","project:update"},logical = Logical.OR)
    @PostMapping("/user/project/item/update")
    public Result updateProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, UpdateGroup.class);
        boolean update = projectItemService.update(formatProjectItem(request), Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId()));
        return Result.success(update);
    }


    /**
     * ???????????????
     */
    @RequiresPermissions(value = {"project:add","project:update"},logical = Logical.OR)
    @PostMapping("/user/project/item/delete")
    public Result deleteProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, OperateProjectItemRequest.DeleteGroup.class);
        boolean delete = projectItemService.remove(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId())
                .eq(UserProjectItemEntity::getType, request.getType()));
        return Result.success(delete);
    }

    /**
     * ???????????????
     *
     * @param request
     */

    @PostMapping("/user/project/item/sort")
    public Result sortProjectItem(@RequestBody SortProjectItemRequest request) {
        ValidatorUtils.validateEntity(request);
        if (ObjectUtil.isNull(request.getAfterPosition())
                && ObjectUtil.isNull(request.getBeforePosition())) {
            return Result.success();
        }
        UserProjectItemEntity itemEntity = projectItemService.getOne(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId()));
        Long sort = sortUtils.calcSortPosition(request.getBeforePosition(), request.getAfterPosition());
        itemEntity.setSort(sort);
        boolean b = projectItemService.updateById(itemEntity);
        return Result.success(new OperateProjectItemVO(itemEntity.getSort(), itemEntity.getId(), b));
    }


    /**
     * ??????????????????
     *
     * @param themeEntity
     */
    @RequiresPermissions(value = {"project:add","project:update"},logical = Logical.OR)
    @PostMapping("/user/project/theme/save")
    public Result saveProjectTheme(@RequestBody UserProjectThemeEntity themeEntity) {
        ValidatorUtils.validateEntity(themeEntity);
        UserProjectThemeEntity entity = userProjectThemeService
                .getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, themeEntity.getProjectKey()));
        if (ObjectUtil.isNotNull(entity)) {
            themeEntity.setId(entity.getId());
        }
        return Result.success(userProjectThemeService.saveOrUpdate(themeEntity));
    }


    /**
     * ??????????????????
     *
     * @param projectKey
     */
    @RequiresPermissions(value = {"project:add"})
    @GetMapping("/user/project/theme/{key}")
    public Result queryThemeByKey(@PathVariable("key") String projectKey) {
        UserProjectThemeEntity entity = userProjectThemeService
                .getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, projectKey));
        return Result.success(entity);
    }

    /**
     * ??????????????????
     *
     * @param settingEntity
     */

    @PostMapping("/user/project/setting/save")
    public Result saveProjectSetting(@RequestBody UserProjectSettingEntity settingEntity) {
        ValidatorUtils.validateEntity(settingEntity);
        UserProjectSettingEntity entity = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, settingEntity.getProjectKey()));
        if (ObjectUtil.isNotNull(entity)) {
            settingEntity.setId(entity.getId());
        }
        return Result.success(userProjectSettingService.saveOrUpdate(settingEntity));
    }


    /**
     * ??????????????????
     *
     * @param projectKey
     */
    @GetMapping("/user/project/setting/{key}")
    public Result querySettingByKey(@PathVariable("key") String projectKey) {
        UserProjectSettingEntity entity = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, projectKey));
        return Result.success(entity);
    }


    /**
     * ?????????????????????
     */
    @GetMapping("/user/project/setting-status")
    public Result querySettingStatus(@RequestParam String projectKey, @RequestParam(required = false) String wxOpenId, HttpServletRequest request) {
        return userProjectSettingService.getUserProjectSettingStatus(projectKey, HttpUtils.getIpAddr(request), wxOpenId);
    }


    /**
     * ???????????????????????????
     */
    @GetMapping("/user/project/wx/notify-qrcode")
    public Result getWxNotifyQrCode(@RequestParam("key") String projectKey) throws WxErrorException {
        String loginSceneStr = JsonUtils.objToJson(new WxMpQrCodeGenRequest(WxMpQrCodeGenRequest.QrCodeType.SUB_NOTIFY, projectKey));
        //5????????????
        WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(loginSceneStr, 10 * 60);
        String subNotifyQrcodeUrl = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
        return Result.success(subNotifyQrcodeUrl);
    }


    /**
     * ???????????????????????????
     */
    @PostMapping("/user/project/wx/delete/notify-user")
    public Result deleteWxNotifyQrCode(@RequestParam("key") String key, @RequestParam("openId") String openId) {
        redisUtils.setRemove(StrUtil.format(WxMpRedisKeyConstants.WX_MP_SUB_NOTIFY, key), openId);
        return Result.success();
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/user/project/wx/notify-user")
    public Result getWxNotifyUser(@RequestParam("key") String projectKey, @RequestParam(required = false) String openIdStr) {
        Set<Object> subNotifyUsers = null;
        if (StrUtil.isNotBlank(openIdStr)) {
            subNotifyUsers = Sets.newHashSet(StrUtil.splitTrim(openIdStr, ";"));
        } else {
            subNotifyUsers = redisUtils.setMembers(StrUtil.format(WxMpRedisKeyConstants.WX_MP_SUB_NOTIFY, projectKey));
        }
        return Result.success(wxMpUserService.listWxMpUserByOpenId(subNotifyUsers)
                .stream().map(item -> new WxMpUserVO(item.getNickname(), item.getHeadImgUrl(), item.getOpenId())).collect(Collectors.toList()));
    }

    /**
     * ?????????????????????
     */

    @GetMapping("/user/project/recycle/page")
    public Result queryRecycleProjects(@RequestAttribute Long userId, QueryProjectRequest.Page request) {
        Page page = projectService.page(request.toMybatisPage(),
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getUserId, userId)
                        .eq(UserProjectEntity::getDeleted, true)
                        .orderByDesc(BaseEntity::getUpdateTime));
        List<UserProjectEntity> records = page.getRecords();
        List<RecycleProjectVO> projectVOList = records.stream().map(item -> {
            int count = projectResultService.count(Wrappers.<UserProjectResultEntity>lambdaQuery().eq(UserProjectResultEntity::getProjectKey, item.getKey()));
            return new RecycleProjectVO(item.getKey(), count, item.getName(), item.getCreateTime(), item.getUpdateTime());
        }).collect(Collectors.toList());
        page.setRecords(projectVOList);
        return Result.success(page);
    }

    /**
     * ???????????????????????????
     */

    @PostMapping("/user/project/recycle/restore")
    public Result restoreRecycleProject(@RequestBody UserProjectEntity request) {
        boolean flag = projectService.update(
                new UserProjectEntity() {{
                    setDeleted(Boolean.FALSE);
                }},
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, request.getKey()));
        return Result.success(flag);
    }

    /**
     * ???????????????????????????
     */

    @PostMapping("/user/project/recycle/delete")
    public Result deleteRecycleProject(//@RequestAttribute Long userId,
                                        @RequestBody UserProjectEntity projectEntity) {
        boolean remove = projectService.remove(Wrappers.<UserProjectEntity>lambdaQuery()/*.eq(UserProjectEntity::getUserId, userId)*/
                .eq(UserProjectEntity::getKey, projectEntity.getKey()));
        if (remove) {
            userProjectThemeService.remove(Wrappers.<UserProjectThemeEntity>lambdaQuery()
                    .eq(UserProjectThemeEntity::getProjectKey, projectEntity.getKey()));
            userProjectSettingService.remove(Wrappers.<UserProjectSettingEntity>lambdaQuery()
                    .eq(UserProjectSettingEntity::getProjectKey, projectEntity.getKey()));
        }
        return Result.success(remove);
    }


}