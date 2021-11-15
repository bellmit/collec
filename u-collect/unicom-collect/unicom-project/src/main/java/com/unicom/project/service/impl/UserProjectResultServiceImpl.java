package com.unicom.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.*;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.unicom.common.IConstants;
import com.unicom.common.constant.CommonConstants;
import com.unicom.common.entity.BaseEntity;
import com.unicom.common.exception.BaseException;
import com.unicom.common.util.AddressUtils;
import com.unicom.common.util.AsyncProcessUtils;
import com.unicom.common.util.RedisUtils;
import com.unicom.common.util.Result;
import com.unicom.project.entity.UserProjectEntity;
import com.unicom.project.entity.UserProjectItemEntity;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.entity.enums.ProjectItemTypeEnum;
import com.unicom.project.entity.struct.UploadResultStruct;
import com.unicom.project.mapper.UserProjectResultMapper;
import com.unicom.project.request.QueryProjectResultRequest;
import com.unicom.project.service.UserProjectItemService;
import com.unicom.project.service.UserProjectResultService;
import com.unicom.project.vo.ExportProjectResultVO;
import com.unicom.storage.cloud.OssStorageFactory;
import com.unicom.storage.util.StorageUtils;
import com.unicom.project.constant.ProjectRedisKeyConstants;
import com.unicom.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目表单项(ProjectResult)表服务实现类
 *
 * @author yangpeng
 * @since 2020-11-23 14:09:22
 */
@Service("projectResultService")
@RequiredArgsConstructor
public class UserProjectResultServiceImpl extends ServiceImpl<UserProjectResultMapper, UserProjectResultEntity> implements UserProjectResultService {

    private final UserProjectItemService userProjectItemService;
    private final RedisUtils redisUtils;

    /**
     * 需要处理类型
     */
    private final Set<ProjectItemTypeEnum> needProcessItemTypeSet =
            Sets.newHashSet(ProjectItemTypeEnum.SELECT, ProjectItemTypeEnum.RADIO, ProjectItemTypeEnum.CHECKBOX, ProjectItemTypeEnum.CASCADER);


    @Override
    public void saveProjectResult(UserProjectResultEntity entity) {
        String projectKey = entity.getProjectKey();
        entity.setSerialNumber(redisUtils.incr(StrUtil.format(ProjectRedisKeyConstants.PROJECT_RESULT_NUMBER, projectKey), CommonConstants.ConstantNumber.ONE));
        entity.setSubmitAddress(AddressUtils.getRealAddressByIP(entity.getSubmitRequestIp()));
        this.saveOrUpdate(entity);
    }




    @Override
    public Page listByQueryConditions(QueryProjectResultRequest request) {
        LambdaQueryWrapper<UserProjectResultEntity> lambdaQueryWrapper = Wrappers.<UserProjectResultEntity>lambdaQuery()
                .eq(UserProjectResultEntity::getProjectKey, request.getProjectKey())
                .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectResultEntity::getCreateTime, request.getEndDateTime())
                .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectResultEntity::getCreateTime, request.getBeginDateTime())
                .orderByDesc(BaseEntity::getCreateTime);
        if (ObjectUtil.isNotNull(request.getExtParamsMap())) {
            request.getExtParamsMap().keySet().forEach(item -> {
                String comparison = MapUtil.getStr(request.getExtComparisonsMap(), item);
                QueryProjectResultRequest.QueryComparison queryComparison = QueryProjectResultRequest.QueryComparison.get(comparison);
                Object value = request.getExtParamsMap().get(item);
                if (queryComparison == QueryProjectResultRequest.QueryComparison.LIKE) {
                    value = "'%" + value + "%'";
                }
                lambdaQueryWrapper.apply(StrUtil.format("original_data ->'$.{}' {} {} ", item, queryComparison.getKey(), value));
            });
        }
        //IPage<Map<String, Object>> p= this.getBaseMapper().pageProject(request.toMybatisPage(),lambdaQueryWrapper);

        return this.page(request.toMybatisPage(), lambdaQueryWrapper);
    }


    @Override
    public Object listByQueryConditions2(QueryProjectResultRequest request) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        if(user==null)
            user=new HashMap<>();
        LambdaQueryWrapper<UserProjectResultEntity> lambdaQueryWrapper = Wrappers.<UserProjectResultEntity>lambdaQuery()
                .eq(ObjectUtil.isNotNull(request.getProjectKey()),UserProjectResultEntity::getProjectKey, request.getProjectKey())
                .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectResultEntity::getUpdateTime, request.getEndDateTime())
                .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectResultEntity::getUpdateTime, request.getBeginDateTime());


        if (ObjectUtil.isNotNull(request.getExtParamsMap())) {
            request.getExtParamsMap().keySet().forEach(item -> {
                String comparison = MapUtil.getStr(request.getExtComparisonsMap(), item);
                QueryProjectResultRequest.QueryComparison queryComparison = QueryProjectResultRequest.QueryComparison.get(comparison);
                Object value = request.getExtParamsMap().get(item);
                if (queryComparison == QueryProjectResultRequest.QueryComparison.LIKE) {
                    value = "'%" + value + "%'";
                }
                lambdaQueryWrapper.apply(StrUtil.format("original_data ->'$.{}' {} {} ", item, queryComparison.getKey(), value));
            });
        }
        if(request.getOrgId()==null)
            request.setOrgId(user.get("orgId"));
        IPage<Map<String, Object>> p= this.getBaseMapper().pageProject(request.toMybatisPage(),lambdaQueryWrapper,request.getOrgId(),request.getKeyword());

        return p;
    }


    @Override
    public Object getData(Map<String,Object> request) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        if(user==null)
            user=new HashMap<>();

       Object is=request.get("idNumbers");

       if(is==null)
           return ResponseUtils.responseError("必须包含正确的身份数据");

       List idNumbers=(List)is;
       Collection<Map<String,Object>> cldata=this.getBaseMapper().getData(idNumbers);
       String projectKey=null;
       //获取projectKey
       for(Map<String,Object> data:cldata){
           projectKey=data.get("projectKey")+"";
           break;
       }
      //查出key
        List<UserProjectItemEntity> ues=userProjectItemService.listByProjectKey(projectKey);

      for(Map<String,Object> data:cldata){
          for(UserProjectItemEntity en:ues){
            data.put("processData",data.get("processData").toString().replaceAll("field"+en.getFormItemId(),en.getLabel()));
            data.put("idNumber", StringUtils.overlay(data.get("idNumber").toString(),"*******",5,12));
            data.remove("projectKey");
          }
      }


        // 替换
      return cldata;


    }




    @Override
    public ExportProjectResultVO exportProjectResult(QueryProjectResultRequest request) {
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        if(user==null)
            user=new HashMap<>();
        //问题列表
        String projectKey = request.getProjectKey();
        List<UserProjectItemEntity> userProjectItemEntityList = userProjectItemService.listByProjectKey(projectKey);
        // excel 标题列


        List<ExportProjectResultVO.ExcelHeader> titleList = userProjectItemEntityList.stream()
                .map(item -> new ExportProjectResultVO.ExcelHeader(item.getFormItemId().toString(), item.getLabel()))
                .collect(Collectors.toList());
        //结果
        List<UserProjectResultEntity> resultEntityList = this.list(Wrappers.<UserProjectResultEntity>lambdaQuery()
                .eq(UserProjectResultEntity::getProjectKey, request.getProjectKey())
                .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectResultEntity::getUpdateTime, request.getEndDateTime())
                .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectResultEntity::getUpdateTime, request.getBeginDateTime())
                .orderByDesc(BaseEntity::getCreateTime));

        Collection<Map<String,Object>> result=this.getBaseMapper().list(Wrappers.<UserProjectResultEntity>lambdaQuery()
                .eq(UserProjectResultEntity::getProjectKey, request.getProjectKey())
                .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectResultEntity::getUpdateTime, request.getEndDateTime())
                .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectResultEntity::getUpdateTime, request.getBeginDateTime())
                .orderByDesc(BaseEntity::getCreateTime),user.get("rootId"),request.getKeyword());
        if (CollectionUtil.isEmpty(resultEntityList)) {
            throw new BaseException("此表单无有效反馈，不能导出");
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> resultList = result.stream().map(item -> {
            Map<String, Object> processData = null;
            try {
                if(item.get("processData")!=null)
                    processData = mapper.readValue( (String)item.get("processData"), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if(processData==null)
                processData=new HashMap<>();
            Iterator<String> iterator = processData.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!titleList.stream()
                        .map(ExportProjectResultVO.ExcelHeader::getFieldKey).collect(Collectors.toList()).contains(key)) {
                    iterator.remove();
                }
            }

           // processData.put(BaseEntity.Fields.createTime, item.get("createTime"));
            //processData.put(UserProjectResultEntity.Fields.submitAddress, item.get("submitAddress"));
            processData.put("idNumber",item.get("idNumber"));
            processData.put("name",item.get("name"));
            processData.put("orgName",item.get("orgName"));

            for(ExportProjectResultVO.ExcelHeader head:titleList){
                if(processData.get(head.getFieldKey())==null){
                    processData.put(head.getFieldKey(),null);
                }
            }
            return processData;
        }).collect(Collectors.toList());
        List<ExportProjectResultVO.ExcelHeader> allHeaderList = new ArrayList<>();
        ExportProjectResultVO.ExcelHeader head2=new ExportProjectResultVO.ExcelHeader();
        head2.setFieldKey("name");
        head2.setTitle("姓名");

        ExportProjectResultVO.ExcelHeader head1=new ExportProjectResultVO.ExcelHeader();
        head1.setFieldKey("idNumber");
        head1.setTitle("身份证号码");

        ExportProjectResultVO.ExcelHeader head3=new ExportProjectResultVO.ExcelHeader();
        head3.setFieldKey("orgName");
        head3.setTitle("所属机构");
        allHeaderList.add(head2);
        allHeaderList.add(head1);
        allHeaderList.add(head3);
        //allHeaderList.addAll(ExportProjectResultVO.DEFAULT_HEADER_NAME);
        allHeaderList.addAll(titleList);


        return new ExportProjectResultVO(allHeaderList, resultList);
    }

    /**
     * 下载项目结果中的附件
     *
     * @param request
     * @return
     */
    @Override
    public Result downloadProjectResultFile(QueryProjectResultRequest request) {
        List<UserProjectItemEntity> userProjectItemEntityList = userProjectItemService.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getType, ProjectItemTypeEnum.UPLOAD));
        String filed = "field";
        // 临时下载文件位置
        ApplicationHome home = new ApplicationHome(getClass());
        File path = home.getSource();
        String uuid = IdUtil.fastSimpleUUID();
        StringBuffer downloadPath = new StringBuffer(path.getParentFile().toString()).append(File.separator).append(uuid).append(File.separator);
        System.out.println(downloadPath);
        //结果
        List<UserProjectResultEntity> resultEntityList = this.list(Wrappers.<UserProjectResultEntity>lambdaQuery()
                .eq(UserProjectResultEntity::getProjectKey, request.getProjectKey())
                .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectResultEntity::getUpdateTime, request.getEndDateTime())
                .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectResultEntity::getUpdateTime, request.getBeginDateTime())
                .orderByDesc(BaseEntity::getUpdateTime));
        if (CollectionUtil.isEmpty(resultEntityList) || CollectionUtil.isEmpty(userProjectItemEntityList)) {
            return Result.failed("暂无收集附件，无法下载");
        }

        ThreadUtil.execAsync(() -> {
            try {
                resultEntityList.forEach(result -> {
                    int index = 0;
                    userProjectItemEntityList.forEach(item -> {
                        StringBuffer tempDownloadPath = new StringBuffer(downloadPath).append(item.getFormItemId());
                        UploadResultStruct uploadResult = MapUtil.get(result.getProcessData(), filed + item.getFormItemId(), UploadResultStruct.class);
                        if (ObjectUtil.isNotNull(uploadResult) && CollectionUtil.isNotEmpty(uploadResult.getFiles())) {
                            uploadResult.getFiles().forEach(uFile -> {
                                if (StrUtil.isNotBlank(uFile.getUrl())) {
                                    File downFile = FileUtil.file(new StringBuffer(tempDownloadPath).append(File.separator)
                                            .append(result.getId()).append(CharUtil.DASHED).append(uFile.getFileName()).toString());
                                    HttpUtil.downloadFile(uFile.getUrl(), downFile);
                                }
                            });
                        }
                    });
                    AsyncProcessUtils.setProcess(uuid, ++index / resultEntityList.size() + 1);
                });
                // 压缩上传oss
                File zip = ZipUtil.zip(downloadPath.toString());
                String downloadUrl = OssStorageFactory.build().upload(new FileInputStream(zip), StorageUtils.generateFileName("download", ".zip"));
                AsyncProcessUtils.setProcess(uuid, downloadUrl);
                //删除临时文件
                FileUtil.del(zip);
                FileUtil.del(downloadPath.toString());
            } catch (Exception e) {
                log.error("download file", e);
            }
        });
        return Result.success(uuid);
    }


}