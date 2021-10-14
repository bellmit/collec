package com.unicom.account.service.impl;

import com.alibaba.excel.EasyExcel;
import com.google.common.base.Joiner;
import com.unicom.account.excel.listener.HUserDataListener;
import com.unicom.account.mapper.HUserMapper;
import com.unicom.account.mapper.ImportDataMapper;
import com.unicom.account.service.ImportDataService;
import com.unicom.account.util.IDCardUtil;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.service.UserProjectResultService;
import com.unicom.roleRightShiro.config.FileConfig;
import com.unicom.roleRightShiro.mapper.OrgMapper;
import com.unicom.utils.PageUtils;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import javaslang.Tuple2;
import javaslang.Tuple4;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
@Slf4j
public class ImportDataServiceImpl implements ImportDataService {
    @Autowired
    private ImportDataMapper importDataMapper;



    @Autowired
    private HUserMapper hUserMapper;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private  UserProjectResultService projectResultService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importData(MultipartFile file, Map<String, Object> parm, int type) {
        HUserDataListener listern = new HUserDataListener();
        try {
            parm.put("path", this.transFile(parm, file, parm.get("head") + ""));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseUtils.responseErrorNoCoder("文件存储异常！");
        }

        //解析数据
        int i = -1;
        Collection<Map<String, Object>> batch = new ArrayList<Map<String, Object>>();
        listern.setBatch(batch);
        listern.setLogId(parm.get("id"));

        try {
            EasyExcel.read(parm.get("path") + "", listern).sheet().headRowNumber(-1).doRead();
        } catch (Exception e) {
            log.error("解析数据错误！");

            try {
                FileUtils.forceDelete(new File(parm.get("storeFilePath") + ""));
            } catch (IOException ex) {
                log.error("清除文件错误！");
            }
            return ResponseUtils.responseError("无法解析，文件格式异常！");
        }
        parm.put("uploadRootName",listern.getRootName());
        //验证
        Tuple4<Boolean, String, Integer,Set> tuple = validateAndParm( batch, parm);
        if (!tuple._1) {
            return ResponseUtils.responseError(tuple._2);
        }
        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        i = this.importDataMapper.insertTel(batch);


        //根据rootOrgId获取projectKey
        String key=this.importDataMapper.getProjectKey(tuple._2());
        if(key==null){
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            return ResponseUtils.responseError("导入错误，村采集表单创建错误，请先检查采集表单！");
        }
        for(Map<String,Object> k:batch){
            UserProjectResultEntity ur=new UserProjectResultEntity();
            ur.setHUserId(Long.parseLong(k.get("id")+"")); //设置用户id
            ur.setOrgId(Long.parseLong(k.get("orgId")+""));
            ur.setProjectKey(key);
            projectResultService.saveProjectResult(ur);
        }
        int count = this.hUserMapper.countOrg(tuple._2());
        if (count > tuple._3()) {
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            return ResponseUtils.responseError("导入错误，超出上限！");
        }
        //更新操作记录
        if (i >= 0) {
            parm.put("importResult", 1);
            i = batch.size();
        } else
            parm.put("importResult", 0);

        parm.put("importCount", i);
        //插入操作记录

        importDataMapper.insertLog(parm);

        //通知收集
        Map<String,Object> sendParm=new HashMap<>();
        sendParm.put("orgIds",Joiner.on(",").join(tuple._4()));


        return ResponseUtils.responseSuccess();
    }

    @Override
    public Map<String, Object> validateCard(MultipartFile file, Map<String, Object> parm) {
        HUserDataListener listern = new HUserDataListener();
        try {
            parm.put("path", this.transFile(parm, file, parm.get("head") + ""));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseUtils.responseErrorNoCoder("文件存储异常！");
        }

        //解析数据
        int i = -1;
        Collection<Map<String, Object>> batch = new ArrayList<Map<String, Object>>();
        listern.setBatch(batch);
        listern.setLogId(parm.get("id"));

        try {
            EasyExcel.read(parm.get("path") + "", listern).sheet().headRowNumber(-1).doRead();
        } catch (Exception e) {
            log.error("解析数据错误！");

            try {
                FileUtils.forceDelete(new File(parm.get("storeFilePath") + ""));
            } catch (IOException ex) {
                log.error("清除文件错误！");
            }
            return ResponseUtils.responseError("无法解析，文件格式异常！");
        }

        List list=new ArrayList<>();
        int number=2;
        for (Map<String, Object> org : batch) {
            String idNumber = org.get("idNumber") + "";
            String name=org.get("name")+"";
            String phone=org.get("tel")+"";
            Tuple2<Boolean,String> vaildate=this.validateImport(idNumber,phone,name);
            if (!vaildate._1){
                Map<String,Object> obj=new HashMap<>();
                obj.put("number",number);
                obj.put("name",name);
                obj.put("idNumber",idNumber);
                obj.put("result",vaildate._2);
                list.add(obj);
            }
            number++;
        }

        return ResponseUtils.responseSuccessData(list);

    }

    private Tuple4<Boolean, String, Integer,Set> validateAndParm( Collection<Map<String, Object>> datas, Map<String, Object> parm) {
        Map<String, String> cache = new HashMap<>();

        Object realRootOrgId=parm.get("rootId");
        boolean isAdmin=Objects.equals(parm.get("isAdmin"), Integer.parseInt("1"));

        Map<String,Object> uploadRoot=this.orgMapper.selectOrgByName(parm.get("uploadRootName"));
        if(uploadRoot==null||(!isAdmin&&!Objects.equals(realRootOrgId,uploadRoot.get("id")))){
            return new Tuple4<>(false, " 导入失败,请在表头填写正确的根组织名称！", 0,null);
        }

        Object rootOrgId=uploadRoot.get("id");

        Integer i = Integer.parseInt(uploadRoot.get("tNumber")+"");
        Map<String, Object> pc = new HashMap<>();
//        if(!isAdmin) {
//
//            pc.put("rootId", rootOrgId);
//            pc.put("orgId",parm.get("orgId"));
//            if(orgMapper.selectAllById(parm)==null)
//                return new Tuple4<>(false, " 不能导入非自己组织的信息！", 0,null);
//
//        }

        if(isAdmin){
            pc.put("orgId",rootOrgId);
        }else{
            pc.put("orgId",parm.get("orgId"));
        }

        Collection<Map<String, Object>>  col=this.orgMapper.selectAll(pc);


        for (Map<String, Object> org : col) {
            cache.put((org.get("name") + "").trim(), org.get("id") + "");
//            if (org.get("parentId") == null && !Objects.equals(parm.get("isAdmin"), Integer.parseInt("1"))) {
//                i = Integer.parseInt(org.get("tNumber") + "");
//                rootOrgId = org.get("id");
//            }
        }


        if (datas.size() > i) {
            return new Tuple4<>(false, " 导入失败,超出上传上限！", 0,null);
        }


        Set<String> orgSet=new HashSet<>();
        for (Map<String, Object> org : datas) {

            String tempOrgId = cache.get(org.get("orgName"));
            if (tempOrgId == null) {
                return new Tuple4<>(false, " 导入失败,传入了错误的组织机构信息！", 0,null);
            }
            orgSet.add(tempOrgId);
//            //查找rootOrgId
//            if (rootOrgId == null) {
//                Map<String, Object> root = this.orgMapper.selectRoot(tempOrgId);
//                rootOrgId = root.get("id");
//                i = Integer.parseInt(root.get("tNumber") + "");
//            }
            String idNumber = org.get("idNumber") + "";
            String name=org.get("name")+"";
            String phone=org.get("tel")+"";
            if (!IDCardUtil.idCardValidate(idNumber))
                return new Tuple4<>(false, StringUtils.join(" 导入失败, ",org.get("name")," 的身份证号码存在错误,清检查后重新导入！"), 0,null);
            org.put("idNumber",idNumber.toUpperCase());

            if(!isTelPhoneNumber(phone))
                return new Tuple4<>(false, StringUtils.join(" 导入失败, ",org.get("name")," 的手机号校验失败,清检查后重新导入！"), 0,null);

            if(!isLegalName(name))
                return new Tuple4<>(false, StringUtils.join(" 导入失败, ",org.get("name"), " 的姓名不合法,清检查后重新导入！"), 0,null);

            org.put("password", StringUtils.substring(idNumber, idNumber.length() - 6, idNumber.length()));

            org.put("orgId", cache.get(org.get("orgName")));
            org.put("rootOrgId", rootOrgId);
        }

        return new Tuple4<>(true, rootOrgId + "", i,orgSet);
    }


    @Override
    public Map<String, Object> listAll(Map<String, Object> parm) {
        int count = this.importDataMapper.countLogs();
        PageUtils.page(parm, count);
        Collection<Map<String, Object>> coll = this.importDataMapper.selectLogs(parm);
        return ResponseUtils.responseSuccessData(coll, count);
    }

    private String transFile(Map<String, Object> parm, MultipartFile file, String head) throws IOException {
        String FileName = head + "-" + System.nanoTime() + ".xlsx";
        String path = fileConfig.getUpLoadFilePath() + "/" + FileName;
        File newFile = new File(path);
        FileCopyUtils.copy(file.getBytes(), newFile);
        parm.put("storeFilePath", path);
        return path;
    }


    private Tuple2<Boolean,String> validateImport(String idNumber, String tel, String name){
        boolean flag=true;
        String result="";
        if(!IDCardUtil.idCardValidate(idNumber)){
            flag=false;
            result=StringUtils.join(result,"身份证号码校验错误;");
        }

        if(!isTelPhoneNumber(tel)){
            flag=false;
            result=StringUtils.join(result,"手机号码校验错误;");
        }

        if(!isLegalName(name)){
            flag=false;
            result=StringUtils.join(result,"姓名校验错误;");
        }

        if(!flag){
            return new Tuple2<>(flag,result);
        }

        return new Tuple2<>(flag,result);
    }


    private boolean isLegalName(String name){
        if (name.contains("·") || name.contains("•")){
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }
    }

    private boolean isTelPhoneNumber(String value) {
        if (value != null && value.length() == 11) {
            Pattern pattern = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]\\d{8}$");
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }


}
