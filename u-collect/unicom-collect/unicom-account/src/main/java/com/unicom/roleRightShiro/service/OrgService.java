package com.unicom.roleRightShiro.service;

import java.util.Collection;
import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月18日 下午4:24:00
 * 类说明:
 */
public interface OrgService {
    Map<String, Object> list();

    Map<String, Object> haveUser(Map<String, Object> parm);

    Map<String, Object> doSubmit(Map<String, Object> parm);

    Map<String, Object> add(Map<String, Object> parm);


    Map<String, Object> update(Map<String, Object> parm);

    Map<String, Object> delete(Map<String, Object> parm);

    public Collection<Map<String,Object>> listChild(Map<String,Object> parm);


    public Integer selectInId(Map<String,Object> parms);

    public Map<String,Object> selectRootId(Object orgId);

}
