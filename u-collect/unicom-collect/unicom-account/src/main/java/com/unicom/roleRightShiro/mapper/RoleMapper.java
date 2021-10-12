package com.unicom.roleRightShiro.mapper;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月4日 上午11:16:02
 * 类说明:
 */

import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Map;

public interface RoleMapper {
    @Results({
            @Result(property = "roleId", column = "roleId"),
            @Result(property = "menus", column = "roleId", many = @Many(select = "com.unicom.roleRightShiro.mapper.RoleMapper.getPerByRole")),
            @Result(property = "users", column = "roleId", many = @Many(select = "com.unicom.roleRightShiro.mapper.RoleMapper.getUserByRole"))})
    @Select(" select id as roleId,role_name as roleName,level as level from sys_role a where 1=1 and delete_status='1' and level>=#{roleLevel} "
            + "ORDER BY a.id LIMIT #{offSet}, #{pageRow}")
    Collection<Map<String, Object>> selectAll(Map<String, Object> parm);

    @Select("select * from sys_role where id=#{roleId}")
    Map<String, Object> selectOne(Map<String, Object> parm);

    @Results({
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "permissions", column = "{roleId=roleId,menuCode=menuCode}", many = @Many(select = "com.unicom.roleRightShiro.mapper.RoleMapper.getPerByRoleMenu"))})
    @Select("<script>"
            + "select DISTINCT menu_code as menuCode, menu_name as menuName,b.role_id as roleId from sys_permission a LEFT JOIN sys_role_permission as b on a.id=b.permission_id "
            + " where 1=1   " + "<if test='0!=null and 0!=1'>  and b.role_id=#{0} </if>  " + " </script>")
    Collection<Map<String, Object>> getPerByRole(String id);

    @Select("<script> select  permission_id as permissionId ,permission_code as permissionCode ,permission_name as permissionName from sys_permission a LEFT JOIN sys_role_permission as b on a.id=b.permission_id"
            + " where 1=1 and  a.menu_code=#{menuCode} <if test='id!=1'>  and b.role_id=#{roleId} </if> "
            + "</script>")
    Collection<Map<String, Object>> getPerByRoleMenu(@Param("roleId") String id, @Param("menuCode") String menuCode);

    @Select("select name, nickname,id as userId from sys_user where role_id=#{0} and delete_status='1' ")
    Collection<Map<String, Object>> getUserByRole(String id);

    @Select("select count(1) from sys_role where delete_status='1' and  delete_status='1' and level>=#{roleLevel}")
    int count(Map<String, Object> parm);

    @Select(" SELECT\r\n" +
            "            p.id                  id,\r\n" +
            "            p.menu_name           menuName,\r\n" +
            "            p.permission_name     permissionName,\r\n" +
            "            ifnull(p.required_permission,0) requiredPerm\r\n" +
            "        FROM sys_permission p")
    Collection<Map<String, Object>> listAllPermission();

    @Insert("<script> insert into sys_role_permission (role_id, permission_id) " +
            "        values " +
            "        <foreach collection='permissions' item='item' index='index' separator=','> " +
            "            (#{roleId}, #{item})" +
            "        </foreach> </script> ")
    int insertPer(Map<String, Object> parm);

    @Insert("insert into sys_role (role_name,role_desc,level) values(#{roleName},#{roleDesc},#{level})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId", keyColumn = "id")
    int insert(Map<String, Object> parm);


    @Update(" UPDATE sys_role SET   delete_status = '2' WHERE id = #{roleId}")
    int deleteRole(Map<String, Object> parm);


    @Delete("delete from sys_role_permission where role_id=#{roleId}")
    int deleteAllPer(Map<String, Object> parm);

    @Update(" UPDATE sys_role" +
            "        SET" +
            "            role_name = #{roleName},role_desc=#{roleDesc},level=#{level} " +
            "        WHERE id = #{roleId} ")
    int update(Map<String, Object> parm);
}
