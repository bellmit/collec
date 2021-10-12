package com.unicom.roleRightShiro.service;

import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月4日 下午3:45:25
 * 类说明:
 */
public interface RoleService {
    Map<String, Object> list(Map<String, Object> parm);

    Map<String, Object> listAllPermission();

    Map<String, Object> update(Map<String, Object> parm);

    Map<String, Object> insert(Map<String, Object> parm);

    Map<String, Object> delete(Map<String, Object> parm);
}
