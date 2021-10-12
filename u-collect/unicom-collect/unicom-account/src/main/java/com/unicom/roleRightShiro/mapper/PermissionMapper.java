package com.unicom.roleRightShiro.mapper;

/**
 * @author yangpeng
 * @version 创建时间：2020年7月30日 下午2:05:55
 * 类说明:
 */

import com.unicom.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Map;

public interface PermissionMapper extends BaseMapper {
    @Select(" SELECT\r\n" + "            u.id              userId,\r\n" + "            u.nickname        nickname, u.name name,\r\n"
            + "            u.role_id         roleId,\r\n" + "            r.role_name       roleName,\r\n"
            + "            p.menu_code       menuCode,\r\n" + "            p.permission_code permissionCode\r\n"
            + "        FROM sys_user u\r\n" + "                 LEFT JOIN sys_role r ON r.id = u.role_id\r\n"
            + "                 LEFT JOIN sys_role_permission rp ON u.role_id = rp.role_id\r\n"
            + "                 LEFT JOIN sys_permission p ON rp.permission_id = p.id AND rp.delete_status = '1'\r\n"
            + "        WHERE u.username = #{username}\r\n" + "          AND u.delete_status = '1' and p.isAdmin=0 ")
    Collection<Map<String, String>> getPermissionByUserName(String userNmae);


    @Select("select a.id as userId,a.username,a.name as name,a.nickname,b.role_name as roleName,b.level as roleLevel,a.org_id as orgId from sys_user a left join sys_role b on a.role_id=b.id where a.id=#{id}")
    Map<String, Object> getRoleByUserId(String id);


    @Select(" SELECT\r\n" + " p.menu_code as menuCode, p.permission_code as  permissionCode \r\n"
            + "        FROM sys_user u\r\n" + "                 LEFT JOIN sys_role r ON r.id = u.role_id\r\n"
            + "                 LEFT JOIN sys_role_permission rp ON u.role_id = rp.role_id\r\n"
            + "                 LEFT JOIN sys_permission p ON rp.permission_id = p.id AND rp.delete_status = '1'\r\n"
            + "        WHERE u.id = #{id}\r\n" + "          AND u.delete_status = '1' and p.isAdmin=0 ")
    Collection<Map<String, String>> getPermissionsByUserId(String id);


    @Select(" SELECT\r\n" + " p.menu_code as menuCode, p.permission_code as  permissionCode\r\n from sys_permission p")
    Collection<Map<String, String>> getPermissionsByUserIdByAdmin();


    @Insert("insert into sys_permission(menu_code,menu_name,permission_code,permission_name,required_permission) values (#{menu_code},#{menu_name},#{permission_code},#{permission_name},#{required_permission})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Map<String, Object> parm);

    @Delete("delete from sys_permission where id=#{id}")
    int delete(Map<String, Object> parm);

    @Update("update sys_permission set menu_code=#{menu_code},menu_name=#{menu_name},permission_code=#{permission_code},permission_name=#{permission_name},required_permission=#{required_permission}  where id=#{id}")
    int update(Map<String, Object> parm);

    @Select("<script> select * from sys_permission  where isAdmin=0 "
            + "<if  test=\"keyword !='' and keyword !=null\"> and  (  LOCATE(#{keyword},menu_name)>0  " +
            "			 or  LOCATE(#{keyword},menu_code)>0 )</if>"
            + "</script>")
    Collection<Map<String, Object>> select(Map<String, Object> parm);


    @Select("select * from sys_permission where id=#{id}")
    Map<String, Object> selectOne(Map<String, Object> parm);

}
