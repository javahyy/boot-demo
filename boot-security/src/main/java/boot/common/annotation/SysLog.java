package boot.common.annotation;

import java.lang.annotation.*;

/**
 * @Description 操作日志注解
 * @Date 2019/11/20 13:11
 * @Author huangyangyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 描述
     * @return {String}
     */
    String value();
}
