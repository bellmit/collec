package com.unicom.mapper;

import com.unicom.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Map;

public interface DicTypeMapper extends BaseMapper {
    @Insert("insert into sys_dictinfo_type(name,code) values (#{name},#{code})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Map<String, Object> parm);

    @Select("select * from sys_dictinfo_type  ")
    Collection<Map<String, Object>> select(Map<String, Object> parm);

    @Update("update sys_dictinfo_type set name=#{name},code=#{code} where id=#{id}")
    int update(Map<String, Object> parm);

    @Delete("delete from sys_dictinfo_type where id=#{id}")
    int delete(Map<String, Object> parm);


    @Select("<script> select * from sys_dictinfo_type  where "
            + "1=1"
            + "<choose>"
            + "<when test='code!=null'> and code =#{code} </when>"
            + "<otherwise> and id=#{id} </otherwise>"
            + "</choose></script>")
    Map<String, Object> selectOne(Map<String, Object> parm);
}
