package com.unicom.collect.web.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.unicom.collect.annotation.Login;
import com.unicom.collect.annotation.NoRepeatSubmit;
import com.unicom.collect.util.HttpUtils;
import com.unicom.common.constant.CommonConstants;
import com.unicom.common.email.MailService;
import com.unicom.common.util.RedisUtils;
import com.unicom.common.util.Result;
import com.unicom.common.validator.ValidatorUtils;
import com.unicom.project.entity.UserProjectEntity;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.entity.UserProjectSettingEntity;
import com.unicom.project.request.QueryProjectResultRequest;
import com.unicom.project.service.UserProjectResultService;
import com.unicom.project.service.UserProjectService;
import com.unicom.project.service.UserProjectSettingService;
import com.unicom.project.vo.ExportProjectResultVO;
import com.unicom.wx.mp.service.WxMpUserMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.unicom.project.constant.ProjectRedisKeyConstants.PROJECT_VIEW_IP_LIST;

/**
 * @author : yangpeng
 * @description : 项目
 * @create : 2021-09-18 18:17
 **/

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/project/result")
public class UserProjectResultController {

    private final UserProjectService projectService;
    private final UserProjectResultService projectResultService;
    private final UserProjectSettingService userProjectSettingService;
    private final MailService mailService;
    private final WxMpUserMsgService userMsgService;
    private final RedisUtils redisUtils;

    @RequiresPermissions(value = {"project:result"})
    /***
     * 查看项目
     *  记录查看的IP 统计查看用户数
     * @return
     */
    @PostMapping("view/{projectKey}")
    public Result viewProject(HttpServletRequest request, @PathVariable("projectKey") String projectKey) {
        String ip = HttpUtils.getIpAddr(request);
        Integer count = Convert.toInt(redisUtils.hmGet(StrUtil.format(PROJECT_VIEW_IP_LIST, projectKey), ip), CommonConstants.ConstantNumber.ZERO);
        redisUtils.hmSet(StrUtil.format(PROJECT_VIEW_IP_LIST, projectKey), ip, count + CommonConstants.ConstantNumber.ONE);
        return Result.success();
    }


    /**
     * 填写
     *
     * @param entity
     * @param request
     * @return
     */
    @NoRepeatSubmit
    @PostMapping("/create")
    public Result createProjectResult(@RequestBody UserProjectResultEntity entity, HttpServletRequest request) {
        ValidatorUtils.validateEntity(entity);
        entity.setSubmitRequestIp(HttpUtils.getIpAddr(request));
        Result<UserProjectSettingEntity> userProjectSettingStatus = userProjectSettingService.getUserProjectSettingStatus(entity.getProjectKey(), entity.getSubmitRequestIp(), entity.getWxOpenId());
        if (StrUtil.isNotBlank(userProjectSettingStatus.getMsg())) {
            return Result.failed(userProjectSettingStatus.getMsg());
        }
        projectResultService.saveProjectResult(entity);

        ThreadUtil.execAsync(() -> {
            UserProjectSettingEntity settingEntity = userProjectSettingStatus.isDataNull() ? null : userProjectSettingStatus.getData();
            this.sendWriteResultNotify(settingEntity, entity);
        });
        return Result.success();
    }



    /**
     * 填写
     *
     * @param entity
     * @param request
     * @return
     */
    @NoRepeatSubmit
    @PostMapping("/update")
    public Result updateProjectResult(@RequestBody UserProjectResultEntity entity, HttpServletRequest request) {
        ValidatorUtils.validateEntity(entity);
        entity.setSubmitRequestIp(HttpUtils.getIpAddr(request));
        Result<UserProjectSettingEntity> userProjectSettingStatus = userProjectSettingService.getUserProjectSettingStatus(entity.getProjectKey(), entity.getSubmitRequestIp(), entity.getWxOpenId());
        if (StrUtil.isNotBlank(userProjectSettingStatus.getMsg())) {
            return Result.failed(userProjectSettingStatus.getMsg());
        }
        projectResultService.saveProjectResult(entity);

        ThreadUtil.execAsync(() -> {
            UserProjectSettingEntity settingEntity = userProjectSettingStatus.isDataNull() ? null : userProjectSettingStatus.getData();
            this.sendWriteResultNotify(settingEntity, entity);
        });
        return Result.success();
    }

    @RequiresPermissions(value = {"project:result"})
    /**
     * 填写结果excel导出
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public void exportProjectResult(QueryProjectResultRequest request, HttpServletResponse response) throws IOException {
        ExportProjectResultVO exportProjectResultVO = projectResultService.exportProjectResult(request);
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        //自定义标题别名
        exportProjectResultVO.getTitleList().forEach(item -> {
            writer.addHeaderAlias(item.getFieldKey(), item.getTitle());
        });
        //设置每列默认宽度
        writer.setColumnWidth(-1, 20);


        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(exportProjectResultVO.getResultList(), true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 填写附件导出
     *
     * @param request
     * @return
     */
    @Login
    @GetMapping("/download/file")
    public Result downloadProjectResultFile(QueryProjectResultRequest request) {
        return projectResultService.downloadProjectResultFile(request);
    }

    @RequiresPermissions(value = {"project:result"})
    /**
     * 结果分页
     *
     * @param request
     * @return
     */
    @Login
    @GetMapping("/page")
    public Result queryProjectResults(QueryProjectResultRequest request) {
       // return Result.success(projectResultService.listByQueryConditions(request));
        return Result.success(projectResultService.listByQueryConditions2(request));
    }


    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public Result queryData(@RequestBody Map<String,Object> request) {
        // return Result.success(projectResultService.listByQueryConditions(request));
        return Result.success(projectResultService.getData(request));
    }


    /**
     * 查询公开结果
     *
     * @param request
     * @return
     */
    @GetMapping("/public/page")
    public Result queryProjectPublicResults(QueryProjectResultRequest request) {
        UserProjectSettingEntity settingEntity = userProjectSettingService.getByProjectKey(request.getProjectKey());
        if (!settingEntity.getPublicResult()) {
            return Result.success();
        }
        return Result.success(projectResultService.listByQueryConditions2(request));
    }

    private void sendWriteResultNotify(UserProjectSettingEntity settingEntity, UserProjectResultEntity entity) {
        if (ObjectUtil.isNull(settingEntity)) {
            return;
        }
        String projectKey = entity.getProjectKey();
        UserProjectEntity project = projectService.getByKey(projectKey);
        if (StrUtil.isNotBlank(settingEntity.getNewWriteNotifyEmail())) {
            mailService.sendTemplateHtmlMail(settingEntity.getNewWriteNotifyEmail(), "新回复通知", "mail/project-write-notify", MapUtil.of("projectName", project.getName()));
        }

        if (StrUtil.isNotBlank(settingEntity.getNewWriteNotifyWx())) {
            List<String> openIdList = StrUtil.splitTrim(settingEntity.getNewWriteNotifyWx(), ";");
            openIdList.stream().forEach(openId -> {
                userMsgService.sendKfTextMsg("", openId, "收到新的反馈，请去Pc端查看");
            });
        }
    }


}