package com.unicom.roleRightShiro.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface SignMapper {

    @Select("select appId,secertId from pr_sign where appId=#{appId}")
    public Map<String,String> select(@Param("appId") String appId);
}
