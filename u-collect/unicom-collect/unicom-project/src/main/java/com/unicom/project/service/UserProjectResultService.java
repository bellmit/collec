package com.unicom.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicom.common.util.Result;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.request.QueryProjectResultRequest;
import com.unicom.project.vo.ExportProjectResultVO;

import java.util.Map;

/**
 * 项目表单项(ProjectResult)表服务接口
 *
 * @author yangpeng
 * @since 2020-11-23 14:09:22
 */
public interface UserProjectResultService extends IService<UserProjectResultEntity> {


    /**
     * 保存结果
     *
     * @param entity
     */
    void saveProjectResult(UserProjectResultEntity entity);



    public Object getData(Map<String,Object> request);

    /**
     * 根据查询参数
     *
     * @param request
     * @return
     */
    Page listByQueryConditions(QueryProjectResultRequest request);


    Object listByQueryConditions2(QueryProjectResultRequest request);

    /**
     * 导出excel
     *
     * @param request
     * @return
     */
    ExportProjectResultVO exportProjectResult(QueryProjectResultRequest request);

    /**
     * 下载项目结果中的附件
     *
     * @param request
     * @return
     */
    Result downloadProjectResultFile(QueryProjectResultRequest request);
}