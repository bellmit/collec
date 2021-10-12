package com.unicom.roleRightShiro.service;

import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年7月30日 下午12:22:18
 * 类说明:
 */
public interface UserService {
	Map<String, Object> getUser(String username);


	Map<String, Object> list(Map<String, Object> parm);

	Map<String, Object> getAllRoles(Map<String, Object> parm);

	Map<String, Object> updateUser(Map<String, Object> parm);

	Map<String, Object> addUser(Map<String, Object> parm);

	Map<String, Object> deleteUser(Map<String, Object> parm);

	Map<String, Object> updatePassword(Map<String, Object> parm);

	Map<String, Object> resetPassword(Map<String, Object> parm);

	Map<String, Object> getOrgs();

	Integer getUserOrgLevel(Object id);

}
