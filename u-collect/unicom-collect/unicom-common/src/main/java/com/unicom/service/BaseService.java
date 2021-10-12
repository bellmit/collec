package com.unicom.service;

import java.util.Map;

public interface BaseService {
    Map<String, Object> insert(Map<String, Object> parm);


    Map<String, Object> delete(Map<String, Object> parm);


    Map<String, Object> update(Map<String, Object> parm);

    Map<String, Object> select(Map<String, Object> parm);

    Map<String, Object> selectOne(Map<String, Object> parm);

}
