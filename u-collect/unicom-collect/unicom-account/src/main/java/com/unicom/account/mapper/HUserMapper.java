package com.unicom.account.mapper;


import com.unicom.account.handler.EncryptTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface HUserMapper {

    @Select("<script>" +
            "select " +
            " id,name,id_number,tel,org_Id as orgId, org_name as orgName,remark" +
            " from " +
            " h_user " +
            " where 1=1 " +
            " <if  test=\"keyword !='' and keyword !=null\"> and ( tel=#{keyword,typeHandler=com.unicom.account.handler.EncryptTypeHandler}  or  id_Number=#{keyword,typeHandler=com.unicom.account.handler.EncryptTypeHandler} " +
            " or name like CONCAT(#{keyword},'%') )</if>"+
            "<if  test=\"orgId !='' and orgId !=null\">  "
            + " and org_id in(" +
            " WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization where id=#{orgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id ) "
            + " </if>"+
            "</script>")
    @Results({//
            @Result(property = "idNumber", column = "id_number", typeHandler = EncryptTypeHandler.class),//
            @Result(property = "tel", column = "tel", typeHandler = EncryptTypeHandler.class),//
    })
    public List<Map<String,Object>> list(Map<String,Object> parm);

    /**
     * 更新用户信息
     * @param huser
     * @return
     */
    @Update("update h_user set tel=#{tel,typeHandler=com.unicom.account.handler.EncryptTypeHandler},name=#{name},id_number=#{idNumber,typeHandler=com.unicom.account.handler.EncryptTypeHandler},org_id=#{orgId},org_name=(select name from sys_organization where id=#{orgId}),remark=#{remark}" +
            " where id=#{id} ")
    public int update(Map<String,Object> huser);

    /**
     * 删除用户信息
     * @param huser
     * @return
     */
    @Delete("delete from h_user where id=#{id}")
    public int delete(Map<String,Object> huser);

    /**
     * 查询最大容纳人数
     * @param rootOrgId
     * @return
     */
    @Select("select count(1) from h_user where "+
            "org_id in(" +
            " WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization where id=#{rootOrgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id ) ")
    public int countOrg(@Param("rootOrgId")String rootOrgId);



    @Select("select id_number as idNumber from h_user where id=#{id}")
    public String  getOneIdNumbers(Map<String,Object> parm);

    @Delete("delete   from h_user where org_id   in( " +
            "  WITH RECURSIVE td AS ( " +
            "   SELECT id FROM sys_organization where id=#{orgId} " +
            "   UNION  " +
            "   SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id" +
            ") SELECT * FROM td ORDER BY td.id ) ")
    public int deleteUserByorgs(Map<String,Object> parm);



}
