package com.unicom.collect.util;

public interface CollectConstants {
    int DATA_TYPE_SYSTEM = 0;
    int DATA_TYPE_OPERATION = 1;
    int DATA_TYPE_CONTRACT = 2;
    int DATA_TYPE_TEL = 3;

    String DATA_TYPE_TEL_HEAD = "TEL";
    String DATA_TYPE_SYSTEM_HEAD = "SIN";
    String DATA_TYPE_OPERATION_HEAD = "OIN";
    String DATA_TYPE_CONTRACT_HEAD = "CIN";

    int ROLE_PM_TYPE = 0;

    int ROLE_SALSE_TYPE = 1;

    int ROLE_LEADER_TYPE = 2;
    //权限-项目经理
    String PERMISSION_PM = "home:listPm";
    //权限-销售经理
    String PERMISSION_SM = "home:listSm";
    //权限-领导
    String PERMISSION_LEADER = "home:listLeader";


    //分页
    int PAGE_NUM = 1;
    int PAGE_SIZE = 30;




}
