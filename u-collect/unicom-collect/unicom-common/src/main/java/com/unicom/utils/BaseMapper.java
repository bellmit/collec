package com.unicom.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface BaseMapper {


    int insert(Map<String, Object> parm);


    int delete(Map<String, Object> parm);


    int update(Map<String, Object> parm);

    Collection<Map<String, Object>> select(Map<String, Object> parm);

    Map<String, Object> selectOne(Map<String, Object> parm);

    String selectInfoByStatusCode(Map<String, Object> parm);

    List<Map<String, Object>> selectDictInfo(Map<String, Object> parm);

    List<Map<String, Object>> selectDictInfoByType(Map<String, Object> parm);
}
