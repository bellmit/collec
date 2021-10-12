package com.unicom.mapper;

import com.unicom.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.Map;

public interface DictMapper extends BaseMapper {

    @Insert("insert into sys_dictinfo(type_code,code,info,userId) values (#{typeCode},#{code},#{info},#{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Map<String, Object> parm);

    @Delete("delete from sys_dictinfo where id=#{id}")
    int delete(Map<String, Object> parm);

    @Update("update sys_dictinfo set info=#{info},code=#{code} where id=#{id}")
    int update(Map<String, Object> parm);

    @Select("<script>" +
            "select * from sys_dictinfo " +
            "<where>" +
            "<if test = 'typeCode != null and typeCode !=24'>" +
            "type_code=#{typeCode}" +
            "</if>" +
            "</where>" +
            "</script> ")
    Collection<Map<String, Object>> select(Map<String, Object> parm);


    @Select("select * from sys_dictinfo where id=#{id}")
    Map<String, Object> selectOne(Map<String, Object> parm);


}
