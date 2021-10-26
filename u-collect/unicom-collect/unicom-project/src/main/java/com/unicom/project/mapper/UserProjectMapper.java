package com.unicom.project.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicom.project.entity.UserProjectEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 项目表(Project)表数据库访问层
 *
 * @author yangpeng
 * @since 2020-11-18 18:16:17
 */
public interface UserProjectMapper extends BaseMapper<UserProjectEntity> {

    @Select("<script> " +
            "select * from (" +
            "select b.name as orgName,a.id,a.`key`,a.name,a.`describe`,a.source_type as sourceType,a.source_id as sourceId,a.`status`,a.type,a.is_deleted, a.is_deleted AS deleted,a.org_id,a.org_id as orgId," +
            "a.create_time,a.create_time as createTime,a.update_time,a.update_time as updateTime from pr_user_project a  inner  join sys_organization b on a.org_id=b.id"
            + "<if  test=\"orgId !='' and orgId !=null\">   "
            + " and  a.org_id  in(" +
            " WITH RECURSIVE td AS (\r" +
            "    SELECT id FROM sys_organization   where id=#{orgId} " +
            "    UNION  " +
            "    SELECT c.id FROM sys_organization c ,td WHERE c.parentId = td.id " +
            ") SELECT * FROM td ORDER BY td.id )  "+
            " </if>" +
            ") k " +
            "  ${ew.customSqlSegment}" +
            "" +
            "</script>")
    public IPage<Map<String,Object>> pageProject(IPage<Map<String,Object>>  page, @Param(Constants.WRAPPER) Wrapper queryWrapper,@Param("orgId") Object OrgId);

}