package com.unicom.roleRightShiro.service;

import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年7月30日 下午12:22:18
 * 类说明:
 */
public interface LoginService {
    Map<String, Object> login(Map<String, Object> parm);

    Map<String, Object> getInfo(Map<String, Object> parm);

    Map<String, Object> logOut(Map<String, Object> parm);

    Map<String, Object> updatePassword(Map<String, Object> parm);

}
