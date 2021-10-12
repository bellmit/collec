package com.unicom.common;

/**
 * @author pengyang
 */
public class IConstants {
    // 返回码,成功
    public static final int RESULT_INT_SUCCESS = 0;

    // 返回码,失败
    public static final int RESULT_INT_ERROR = 1;


    // 返回码,失败-未登录
    public static final int RESULT_INT_ERROR_NOUSER = 2;

    // 没有权限
    public static final int RESULT_INT_ERROR_NOROLE = 3;

    // 验证码失效
    public static final int RESULT_INT_ERROR_CODE = 4;

    //已上传
    public static final int FILE_UPDATE = 0;

    //数据已存储
    public static final int FILE_SAVE = 1;

    //文件已删除
    public static final int FILE_DELETE = 3;

    //同一天连续出现
    public static final int QUERY_CONTINUE_ONEDAY = 0;

    //文件连续出现
    public static final int QUERY_CONTINUE_DAY = 1;

    //累计连续出现
    public static final int QUERY_COUNT = 2;

    public static final int DEFAULT_PAGESIZE = 50;

    public static final int DEFAULT_PAGEINDEX = 1;

    public static final int CACHE_MAX_SIZE = 30;

    //用户已激活
    public static final int USER_ACTIVE = 1;
    //用户已注销
    public static final int USER_LOGOUT = 2;


    //临时文件存储目录
    public static final String V_DOWNLOAD_PATG = "/download/";

    /**
     * session中存放用户信息的key值
     */
    public static final String SESSION_USER_INFO = "userInfo";
    public static final String SESSION_PRIVATE_KEY = "privateKey";
    public static final String SESSION_AES_IV = "IV";
    public static final String SESSION_USER_PERMISSION = "userPermission";
    public static final String ROLE_ADMIN = "1";

    public static final String SUPER_ROLE_ADMIN = "admin";

    public static final String CACHE_REDIS = "redis";

    public static final String CACHE_LOCAL = "local";

    public static final String SESSION_TYPE_TOKEN = "token";
    public static final String SESSION_TYPE_SESSION = "session";

    /**
     * 当前页码
     */
    public static final String PAGE = "currPage";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "pageSize";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     * 升序
     */
    public static final String ASC = "asc";

    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private final int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 定时任务状态
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private final int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private final int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
