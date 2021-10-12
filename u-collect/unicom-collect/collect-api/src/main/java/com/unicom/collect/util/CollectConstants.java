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

    String ROLE_PM = "项目经理";

    String ROLE_SALES = "销售经理";

    String ROLE_LEADER = "领导";

    String HUNAN = "湖南省分公司";

    int ROLE_PM_TYPE = 0;

    int ROLE_SALSE_TYPE = 1;

    int ROLE_LEADER_TYPE = 2;
    //权限-项目经理
    String PERMISSION_PM = "home:listPm";
    //权限-销售经理
    String PERMISSION_SM = "home:listSm";
    //权限-领导
    String PERMISSION_LEADER = "home:listLeader";

    //项目表状态-待认领
    Integer PROJECT_NEED_CLAIMED = 1000;
    //项目表状态-待编写计划
    Integer PROJECT_NEED_PLAN = 1001;
    //项目表状态-已编写计划
    Integer PROJECT_PLANNED = 1002;
    //项目表状态-项目已完成
    Integer PROJECT_COMPLETED = 1003;

    //项目表状态-项目正常
    Integer PROJECT_NORMALL = 1006;

    //项目表状态-已逾期
    Integer PROJECT_OVERDUE = 1004;
    // 项目表状态-预警
    Integer PROJECT_WARNING = 1005;
    //项目表状态-项目状态不存在
    Integer PROJECT_NO_STATUS = 1099;

    //任务表状态-未填报
    Integer SCHEDULE_UNFILLED = 1100;
    //任务表状态-已填报
    Integer SCHEDULE_FILLED = 1101;
    // 任务表状态-已逾期
    Integer SCHEDULE_OVERDUE = 1102;
    // 任务表状态-预警
    Integer SCHEDULE_WARNING = 1103;
    //分页
    int PAGE_NUM = 1;
    int PAGE_SIZE = 30;




}
