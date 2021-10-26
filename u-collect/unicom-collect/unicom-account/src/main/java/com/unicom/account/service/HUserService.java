package com.unicom.account.service;

import java.util.Map;

public interface HUserService {


    public Map<String,Object> list(Map<String,Object> parm);

    public Map<String,Object> update(Map<String,Object> parm);

    public Map<String,Object> delete(Map<String,Object> parm);
    public Map<String,Object> add(Map<String,Object> parm);

    public Map<String,Object> clearOrgs(Map<String,Object> parm);



}
