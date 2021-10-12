package com.unicom.roleRightShiro.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface LoginMapper {
    @Select("select a.id as id,a.id as userId,a.username,a.name,a.password,a.nickname,a.role_id as roleId,a.org_id as orgId,a.delete_status,b.name as orgName,b.level as level from sys_user a LEFT JOIN sys_organization b on a.org_id=b.id\r\n" +
            " where a.username=#{username}  and a.password=#{password} and a.delete_status='1'")
    Map<String, String> select(String username, String password);
}
