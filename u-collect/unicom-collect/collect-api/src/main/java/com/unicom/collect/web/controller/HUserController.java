package com.unicom.collect.web.controller;

import com.unicom.account.service.HUserService;
import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.service.OrgService;
import com.unicom.utils.ResponseUtils;
import javaslang.Tuple2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/collect/huser")
public class HUserController {

    @Autowired
    private HUserService hUserService;

    @Autowired
    private OrgService orgService;

    @RequiresPermissions(value = {"staffs:list"})
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> list(@RequestParam Map<String, Object> params) {
        Tuple2<Boolean,String> validate=this.validate(params);
        if(!validate._1)
          return  ResponseUtils.responseError(validate._2);



        return hUserService.list(params);
    }

    @RequiresPermissions(value = {"staffs:update"})
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> update(@RequestBody Map<String, Object> params) {
        Tuple2<Boolean,String> validate=this.validate(params);
        if(!validate._1)
            return  ResponseUtils.responseError(validate._2);


        return hUserService.update(params);
    }



    @RequiresPermissions(value = {"staffs:add"})
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> add(@RequestBody Map<String, Object> params) {
        Tuple2<Boolean,String> validate=this.validate(params);
        if(!validate._1)
            return  ResponseUtils.responseError(validate._2);


        return hUserService.update(params);
    }


    @RequiresPermissions(value = {"staffs:delete"})
    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> delete(@RequestParam Map<String, Object> params) {
        Tuple2<Boolean,String> validate=this.validate(params);
        if(!validate._1)
            return  ResponseUtils.responseError(validate._2);


        return hUserService.delete(params);
    }


    @RequiresPermissions(value = {"staffs:clear"})
    @RequestMapping(value = "/clear", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> clear(@RequestParam Map<String, Object> params) {
        if(params.get("orgId")==null){
            return ResponseUtils.responseError("请选择一个组织机构！");
        }
        Tuple2<Boolean,String> validate=this.validate(params);
        if(!validate._1)
            return  ResponseUtils.responseError(validate._2);


        return hUserService.clearOrgs(params);
    }


    private  Tuple2<Boolean,String> validate(Map<String,Object> params){
        Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
        if(!Objects.equals(user.get("isAdmin"),Integer.parseInt("1"))){
            if(params.get("orgId")==null) {
                params.put("orgId", user.get("orgId"));
            }else{
                params.put("rootId",user.get("orgId"));
                if(orgService.selectInId(params)==null)
                    return new Tuple2<>(false,"权限不足，无法完成操作！");

            }

        }
        return new Tuple2<>(true,null);
    }


}
