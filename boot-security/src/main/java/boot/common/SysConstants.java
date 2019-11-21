package boot.common;

/**
 * @Description 系统常量
 * @Date 2019/11/20 13:10
 * @Author huangyangyang
 */
public interface SysConstants {

    /**
     * 角色前缀
     */
    String SECURITY_ROLE_PREFIX = "ROLE_";

    /**
     * 状态[0:失效,1:正常]
     */
    int DB_STATUS_DEL = 0;
    int DB_STATUS_OK = 1;

    /**
     * 用户锁定状态
     */
    int DB_ADMIN_NON_LOCKED = 0;
    int DB_ADMIN_LOCKED = 1;

    /**
     * 菜单
     */
    int RESOURCE_TYPE_MENU = 0;
}
