package com.unicom.project.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.unicom.project.entity.UserProjectResultEntity;
import com.unicom.project.handler.EncryptTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 项目表单项(ProjectResult)表数据库访问层
 *
 * @author yangpeng
 * @since 2021-10-14 14:09:21
 */
public interface UserProjectResultMapper extends BaseMapper<UserProjectResultEntity> {

    @Select("<script> " +
            "select * from (" +
            "select " +
            "CONCAT((select name from h_user where id_number=a.remark),'户') as fname, if(a.id_number=a.remark,1,0) as  isHead," +
            "a.name,a.id_number ,a.tel,a.remark,o.name as orgName,b.project_key as projectKey,b.serial_number as serialNumber,b.process_data as processData,b.submit_ua as submitUa,b.submit_os as submitOs," +
            " b.submit_browser as submitBrowser,b.submit_request_ip as submitRequestIp,b.submit_address as submitAddress,b.complete_time as completeTime,b.wx_open_id as wxOpenId,b.wx_user_info as wxUserInfo,b.create_time as createTime," +
            " b.update_time as updateTime,  " +
            " b.*"+
            " from h_user  a inner join sys_organization  o on a.org_id=o.id " +
            " <if  test=\"keyword !='' and keyword !=null\"> and ( a.tel=#{keyword,typeHandler=com.unicom.account.handler.EncryptTypeHandler}  or  a.id_Number=#{keyword,typeHandler=com.unicom.account.handler.EncryptTypeHandler} " +
            " or a.name like CONCAT(#{keyword},'%') )</if>"+
             "<if  test=\"orgId !='' and orgId !=null\">   " +
             " and  a.org_id  in(" +
            "  WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization   where id=#{orgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            " ) SELECT * FROM td ORDER BY td.id )  "+
            "  </if>" +
            " left join pr_user_project_result b  on a.id=b.h_user_id ) k" +
            "  ${ew.customSqlSegment} order by fname desc,isHead desc,createTime desc " +
            "</script>"

    )
    @Results({//
            @Result(property = "idNumber", column = "id_number", typeHandler = EncryptTypeHandler.class),//
            @Result(property = "tel", column = "tel", typeHandler = EncryptTypeHandler.class),//
            @Result(property = "remark", column = "remark", typeHandler = EncryptTypeHandler.class),//
    })
    public IPage<Map<String,Object>> pageProject(IPage<Map<String,Object>>  page, @Param(Constants.WRAPPER) Wrapper queryWrapper,@Param("orgId") Object OrgId,@Param("keyword") String keyword);



    @Select("<script> " +
            "select * from (" +
            "select a.name,a.id_number ,a.tel,a.remark,o.name as orgName,b.project_key as projectKey,b.serial_number as serialNumber,b.process_data as processData,b.submit_ua as submitUa,b.submit_os as submitOs," +
            " b.submit_browser as submitBrowser,b.submit_request_ip as submitRequestIp,b.submit_address as submitAddress,b.complete_time as completeTime,b.wx_open_id as wxOpenId,b.wx_user_info as wxUserInfo,b.create_time as createTime," +
            " b.update_time as updateTime,  " +
            " b.*"+
            " from h_user  a left join sys_organization  o on a.org_id=o.id " +
            "<if  test=\"orgId !='' and orgId !=null\">   " +
            " and  a.org_id  in(" +
            "  WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization   where id=#{orgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            " ) SELECT * FROM td ORDER BY td.id )  "+
            "  </if>" +
            " left join pr_user_project_result b  on a.id=b.h_user_id ) k" +
            "  ${ew.customSqlSegment}" +
            "</script>"

    )
    @Results({//
            @Result(property = "idNumber", column = "id_number", typeHandler = EncryptTypeHandler.class),//
            @Result(property = "tel", column = "tel", typeHandler = EncryptTypeHandler.class),//
            @Result(property = "remark", column = "remark", typeHandler = EncryptTypeHandler.class),//
    })
    public Collection<Map<String,Object>> list( @Param(Constants.WRAPPER) Wrapper queryWrapper, @Param("orgId") Object OrgId);




    @Select("<script> " +

            "select a.name,a.id_number ,b.project_key as projectKey,b.process_data as processData" +
            " from h_user  a " +
            " left join pr_user_project_result b  on a.id=b.h_user_id " +
            "where id_number in "+
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>"+
                "#{id,typeHandler=com.unicom.account.handler.EncryptTypeHandler}"+
            "</foreach>"+
            "</script>"

    )
    @Results({//
            @Result(property = "idNumber", column = "id_number", typeHandler = EncryptTypeHandler.class)
    })
    public Collection<Map<String,Object>> getData(@Param("ids") List ids);



}