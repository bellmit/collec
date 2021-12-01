package com.unicom.roleRightShiro.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月18日 下午4:24:37 类说明:
 */
public interface OrgMapper {
    @Select("<script>"
            + "WITH RECURSIVE td AS (" +
            "    SELECT * FROM sys_organization  "
            + "where 1=1   "
            + "<choose>"
            + "<when  test=\"orgId !='' and orgId !=null\">  and id=#{orgId} </when>"
            + "<otherwise> and level=1 </otherwise>"
            + "</choose>" +
            "    UNION " +
            "    SELECT c.* FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id desc"
            + "</script> ")
    @Results({
            @Result(column = "province_code", property = "provinceCode", jdbcType = JdbcType.VARCHAR),
            @Result(column = "city_code", property = "cityCode", jdbcType = JdbcType.VARCHAR),
            @Result(column = "county_code", property = "countyCode", jdbcType = JdbcType.VARCHAR),
            @Result(column = "t_number", property = "tNumber", jdbcType = JdbcType.VARCHAR),
            @Result(column = "account_number", property = "accountNumber", jdbcType = JdbcType.VARCHAR)

    })
    List<Map<String, Object>> selectAll(Map<String, Object> parm);


    @Select("WITH RECURSIVE td AS ( SELECT id FROM sys_organization WHERE id = #{orgId} UNION SELECT c.id FROM sys_organization c, td WHERE c.parentId = td.id ) SELECT" +
            " * " +
            " FROM " +
            " td " +
            " ORDER BY" +
            " td.id "
          )
    List<Integer> selectAllId(Map<String,Object> parm);


    @Select("WITH RECURSIVE td AS (" +
            "               SELECT * FROM sys_organization  " +
            "       where 1=1   " +
            "      " +
            "  and id=#{orgId} " +
            "         " +
            "  UNION " +
            "              SELECT c.* FROM sys_organization c ,td WHERE c.id = td.parentId " +
            "           ) SELECT td.id,td.name as name,td.t_number as tNumber,td.validity,td.account_number as accountNumber FROM td where td.level=1  ORDER BY td.id;")
    Map<String,Object> selectRoot(@Param("orgId") Object orgId);


    @Select("<script>"
            + "WITH RECURSIVE td AS (" +
            "    SELECT id FROM sys_organization  "
            + "where 1=1   "
            + "<choose>"
            + "<when  test=\"orgId !='' and orgId !=null\">  and id=#{orgId} </when>"
            + "<otherwise> and level=1 </otherwise>"
            + "</choose>" +
            "    UNION " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id;"
            + "</script> ")
    Set<Integer> selectAllById(Map<String, Object> parm);


    @Select("<script> select count(1)  from sys_user where org_Id in"
            + " <foreach collection='delData' item='id' open='(' separator=',' close=')'>  #{id}  </foreach> </script>")
    int countUser(Map<String, Object> parm);

    @Update("update sys_organization set name=#{name},address=#{address},tel=#{tel},description=#{description}," +
            "province_code=#{provinceCode},city_code=#{cityCode}" +
            ",county_code=#{countyCode},province=#{province},city=#{city},county=#{county}  where id=#{id} ")
    int update(Map<String, Object> parm);

    @Update({"<script>" + "<foreach item='org' collection='updateData' index='index'  separator=','>"
            + " UPDATE sys_organization " + "SET update_time = NOW(),"
            + "name=#{org.name},address=#{org.address},tel=#{org.tel},description=#{org.description}" +
            ",province_code=#{org.provinceCode},city_code=#{org.cityCode}" +
            ",county_code=#{org.countyCode},province=#{org.province},city=#{org.city},county=#{org.county} " +
            "<if  test=\" org.tNumber !='' and org.tNumber !=null\">  " +
            " ,t_number=#{org.tNumber},validity=#{org.validity},account_number=#{org.accountNumber}  "+
            "</if>"
            + " WHERE id = #{org.id} " + "</foreach>" + "</script>"})
    int updateAll(Map<String, Object> parm);


    @Update({"<script>"
            + " UPDATE sys_organization " + "SET update_time = NOW()"+
            "<if  test=\" tNumber !='' and tNumber !=null\">  " +
            " ,t_number=#{tNumber},validity=#{validity},account_number=#{accountNumber} "+
            "</if>"
            + " where  id  in(" +
            " WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization where id=#{id} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id ) "
            + "</script>"})
    int updateOrgTNumber(Map<String, Object> parm);

    @Insert("insert into sys_organization(name,code,parentId,level,description,address,tel,province_code,city_code,county_code,province,city,county,t_number,validity,rootId,account_number) "
            + "values (#{name},#{code},#{parentId},#{level},#{description},#{address},#{tel},#{provinceCode},#{cityCode},#{county_code},#{province},#{city},#{county},#{tNumber},#{validity},#{rootId},#{accountNumber})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Map<String, Object> parm);

    @Insert("<script> insert into sys_organization (id,name,code,parentId,level,description,address,tel,province_code,city_code,county_code,province,city,county,t_number,validity,rootId,account_number) "
            + "        values " + "        <foreach collection='addData' item='org' index='index' separator=','> "
            + "            (null,#{org.name},#{org.code},#{org.parentId},#{org.level},#{org.description},#{org.address},#{org.tel},#{org.provinceCode},#{org.cityCode},#{org.countyCode},#{org.province},#{org.city},#{org.county},#{org.tNumber},#{org.validity},#{org.rootId},#{org.accountNumber})"
            + "        </foreach> </script> ")
    @Options(useGeneratedKeys = true, keyProperty = "org.id", keyColumn = "id")
    int insertAll(Map<String, Object> parm);

    @Delete("<script> delete from sys_organization where id in "
            + " <foreach collection='delData' item='id' open='(' separator=',' close=')'>  #{id}  </foreach> </script>")
    int delete(Map<String, Object> parm);

    @Select("<script> select count(1) from sys_user where delete_status=1 and  org_id in  (WITH RECURSIVE td AS (\r" + "    SELECT id FROM sys_organization WHERE id in "
            + "<foreach collection='delData' item='id' open='(' separator=',' close=')'>  #{id}  </foreach> "
            + "  \r" + "    UNION \r"
            + "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id   \r"
            + ") SELECT * FROM td ORDER BY td.id )  </script>")
    int selectChindern(Map<String, Object> parm);

    @Select("<script> select count(1) from h_user where  org_id in  (WITH RECURSIVE td AS (\r" + "    SELECT id FROM sys_organization WHERE id in "
            + "<foreach collection='delData' item='id' open='(' separator=',' close=')'>  #{id}  </foreach> "
            + "  \r" + "    UNION \r"
            + "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id   \r"
            + ") SELECT * FROM td ORDER BY td.id )  </script>")
    int selectHuser(Map<String,Object> parm);

    @Select("select level from sys_organization where id=#{id}")
    int selectOrgLevel(Object id);

    @Select("<script>" +
            "   WITH RECURSIVE td AS (  " +
            "              SELECT id FROM sys_organization where id=#{rootId}  " +
            "              UNION   " +
            "              SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id  " +
            "               ) SELECT * FROM td where td.id=#{orgId}   ORDER BY td.id " +
            "</script>")
    Integer selectInId(Map<String,Object> parm);


    @Select("select id,account_number as accountNumber,rootId   from sys_organization where id=#{orgId}")
    Map<String,Object> selectAccountNumber(Map<String,Object> parm);


    @Select("select id,t_number as tNumber from sys_organization where name=#{name}")
    Map<String,Object> selectOrgByName(@Param("name") Object name);





    @Select(
            "WITH RECURSIVE td AS (\n" +
                    "              SELECT code ,name as label,pcode,level FROM area_code_2021   where code='430000000000'\n" +
                    "         UNION  \n" +
                    "              SELECT c.code,c.name as label ,c.pcode,c.level  FROM area_code_2021 c ,td WHERE c.pcode = td.code\n" +
                    "          ) SELECT * FROM td where td.LEVEL<5  ORDER BY td.code"
    )
    Collection<Map<String,Object>> getOrgJson();


    @Select(
            "select  code ,name as label,pcode,level FROM area_code_2021 where pcode=#{pcode} "
    )
    Collection<Map<String,Object>> getPOrg(Map<String,Object> parm);


    int addProject();

}
