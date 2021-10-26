package com.unicom.account.service.impl;

import com.github.pagehelper.PageInfo;

import com.unicom.account.mapper.HUserMapper;
import com.unicom.account.mapper.ImportDataMapper;
import com.unicom.account.service.HUserService;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.service.UserProjectResultService;
import com.unicom.roleRightShiro.mapper.OrgMapper;
import com.unicom.utils.PageUtils;
import com.unicom.utils.RSAEncrypt;
import com.unicom.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class HUserServiceImpl implements HUserService {
    @Autowired
    private HUserMapper hUserMapper;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private ImportDataMapper importDataMapper;

    @Autowired
    private UserProjectResultService projectResultService;



    @Override
    public Map<String, Object> list(Map<String, Object> parm) {

        PageUtils.page(parm);
        return ResponseUtils.responseSuccessData(new PageInfo(this.hUserMapper.list(parm)));
    }

    @Override
    public Map<String, Object> update(Map<String, Object> parm) {
        //解码
        this.enCode(parm,"idNumber");
        this.enCode(parm,"tel");
        this.enCode(parm,"remark");
        //如果
        Map<String,Object> userIds=new HashMap<>();
        userIds.put("userIds",parm.get("id"));
        this.hUserMapper.update(parm);

        return ResponseUtils.responseSuccess();
    }

    @Override
    public Map<String,Object> add(Map<String,Object> parm){
        //解码
        this.enCode(parm,"idNumber");
        this.enCode(parm,"tel");
        this.enCode(parm,"remark");

        String rootId=parm.get("rootId")+"";
        if(StringUtils.isBlank(rootId)){
            rootId=orgMapper.selectRoot(parm.get("orgId")).get("id")+"";
        }

        String key=this.importDataMapper.getProjectKey(rootId);
        //如果
        Map<String,Object> userIds=new HashMap<>();
        userIds.put("userIds",parm.get("id"));
        this.hUserMapper.insert(parm);

        UserProjectResultEntity ur=new UserProjectResultEntity();

        ur.setHUserId(Long.parseLong(parm.get("id")+"")); //设置用户id
        ur.setOrgId(Long.parseLong(parm.get("orgId")+""));
        ur.setProjectKey(key);
        projectResultService.saveProjectResult(ur);


        return ResponseUtils.responseSuccess();
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> parm) {
        Map<String,Object> idNumbers=new HashMap<>();
        idNumbers.put("idNumbers",hUserMapper.getOneIdNumbers(parm));
        hUserMapper.delete(parm);



        return ResponseUtils.responseSuccess();
    }

    @Override
    public Map<String, Object> clearOrgs(Map<String, Object> parm) {
        this.hUserMapper.deleteUserByorgs(parm);

        //调用人员清除接口
        parm.put("orgIds",parm.get("orgId"));

        return ResponseUtils.responseSuccess();
    }

    private void enCode(Map<String, Object> parm, String key) {
        String value = parm.get(key) + "";

        if (StringUtils.isNotBlank(value)) {
            value = RSAEncrypt.decrypt(value, RSAEncrypt.privateKey).split(";")[0];
//            try {
//                value = AESUtils.encode(DataKeyConfig.getAESKEY(), value, DataKeyConfig.getAESIV());
//            } catch (Exception e) {
//                throw new RuntimeException("数据转换异常！");
//            }
            parm.put(key, value);
        }

    }
}
