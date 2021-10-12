package com.unicom.roleRightShiro.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Collection;
import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年7月30日 下午12:22:18
 * 类说明:
 */
public interface UserMapper {
	@Select("select a.id as userId,a.id as id,a.salt as salt,a.username,a.name,a.password,a.phone as phone,a.nickname,a.role_id as roleId,a.org_id as orgId,a.delete_status,b.name as orgName,b.level as level, " +
            "b.province_code,b.city_code,b.county_code,b.province,b.city,b.county,a.is_admin as isAdmin,b.rootId,b.parentId,b.validity,datediff(b.validity,now())  as vday   " +
            "from sys_user a LEFT JOIN sys_organization b on a.org_id=b.id\r\n" +
            " where a.username=#{username}   and a.delete_status='1'")
	@Results({
			@Result(column = "province_code", property = "provinceCode", jdbcType = JdbcType.VARCHAR),
			@Result(column = "city_code", property = "cityCode", jdbcType = JdbcType.VARCHAR),
			@Result(column = "county_code", property = "countyCode", jdbcType = JdbcType.VARCHAR)
	})
	Map<String, Object> select(String username);


	@Select("<script>select a.id as userId,a.username,a.role_id as roleId,a.org_id as orgId,c.parentId,c.rootId,b.role_name as roleName,a.delete_status,a.nickname,c.name as orgName, a.name as name,a.phone as phone, " +
			"c.province_code,c.city_code,c.county_code,c.province,c.city,c.county,"
			+ "DATE_FORMAT(a.create_time, '%Y.%m.%d %T') createTime,\r\n" +
            "                     DATE_FORMAT(a.update_time, '%Y.%m.%d %T') updateTime,a.is_admin as isAdmin from sys_user a "
			+ " left join sys_role b on a.role_id=b.id  "
			+ " left join sys_organization c on a.org_id=c.id "
			+ "where a.delete_status='1' and b.level>=#{roleLevel} "
			+ "  <if test=\"username !='' and username !=null\"> and a.username like CONCAT('%',#{username},'%') </if>"
			+ "  <if test=\"name !='' and name !=null\"> and a.name like CONCAT('%',#{name},'%') </if>"
			+ "<if  test=\"orgId !='' and orgId !=null\">  "
			+ " and org_id in(" +
			" WITH RECURSIVE td AS (\r\n" +
			"    SELECT id FROM sys_organization where id=#{orgId} " +
			"    UNION  " +
			"    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
			") SELECT * FROM td ORDER BY td.id ) "
			+ " </if>"
			+ "ORDER BY a.id LIMIT #{offSet}, #{pageRow} </script>")
	@Results({
			@Result(column = "province_code", property = "provinceCode", jdbcType = JdbcType.VARCHAR),
			@Result(column = "city_code", property = "cityCode", jdbcType = JdbcType.VARCHAR),
			@Result(column = "county_code", property = "countyCode", jdbcType = JdbcType.VARCHAR)
	})
	Collection<Map<String, Object>> selectAll(Map<String, Object> parm);


	@Select("<script>select count(1) from sys_user a "
			+ " left join sys_role b on a.role_id=b.id  "
			+ " left join sys_organization c on a.org_id=c.id "
			+ "where a.delete_status='1' and b.level>=#{roleLevel} "
			+ "  <if test=\"username !='' and username !=null\"> and a.username like CONCAT('%',#{username},'%') </if>"
			+ "<if  test=\"orgId !='' and orgId !=null\">  "
			+ " and org_id in(" +
			" WITH RECURSIVE td AS (\r\n" +
			"    SELECT id FROM sys_organization where id=#{orgId} " +
			"    UNION  " +
			"    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
			") SELECT * FROM td ORDER BY td.id ) "
			+ " </if>"
			+ "</script>"
	)
	int count(Map<String, Object> parm);


	@Insert(" INSERT INTO sys_user " +
			"        (username, password, nickname, role_id,org_id,salt,name,phone)  VALUES" +
			"            (#{username}, #{password}, #{nickname}, #{roleId},#{orgId},#{salt},#{name},#{phone})")
	int insert(Map<String, Object> user);


	@Update("<script> UPDATE sys_user" +
			"        SET" +
			"        nickname = #{nickname}\r\n" +
			"        <if test=\"password !='' and password !=null\">" +
			"            , password = #{password},salt=#{salt}\r\n" +
			"        </if>\r\n" +
			"        <if test=\"name !='' and name !=null\">\r\n" +
			"            , name = #{name}" +
			"        </if>" +
			"        <if test=\"orgId !='' and orgId !=null\">\r\n" +
			"            , org_Id = #{orgId}" +
			"        </if>" +
			"		  ,phone=#{phone}" +
			"        , role_id = #{roleId} " +
			"        <if test=\"deleteStatus !='' and deleteStatus !=null\">\r\n" +
			"        , delete_status = #{deleteStatus}\r\n" +
			"        </if>\r\n" +
			"        WHERE id = #{userId} and id != 10001 </script>")
	int update(Map<String, Object> user);


	@Update("update sys_user set password=#{password},salt=#{salt} where id=#{userId}")
	int updatePassword(Map<String, Object> user);

	@Update("update sys_user set password=#{password},salt=#{salt} where username=#{userName}")
	int resetPassword(Map<String, Object> user);


	@Delete("update sys_user set delete_status=2 WHERE id = #{userId} and id != 10001\"")
	int delete(Map<String, Object> user);


	@Select("<script> select id as roleId,role_name as roleName from sys_role where 1=1  and level>=#{roleLevel}  and delete_status=1</script>")
	Collection<Map<String, Object>> selectAllRole(Map<String, Object> parm);

	@Select("select count(1) from sys_user where username=#{username} and  delete_status=1  ")
	int countUserName(Map<String, Object> parm);


	@Select("select t.id,t.name,t.username,t.password,t.salt,b.name as orgName from sys_user t left join sys_organization  b on t.org_id=b.id ")
	Collection<Map<String, Object>> selectAllPass();

	@Select(
			" select count(1) from sys_user a LEFT JOIN sys_organization  b on a.org_id=b.id "+
			" where delete_status=1 and "+
			" a.org_id   in( "+
			" WITH RECURSIVE td AS ( "+
					" SELECT id FROM sys_organization where id=#{rootId} "+
			" UNION "+
			" SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id "+
	" ) SELECT * FROM td ORDER BY td.id )"
	)
	Integer orgUserCount(Map<String,Object> parm);


}
