package boot.config;

import boot.util.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MybatisPlus配置
 *
 * @author L.cm
 */
@Configuration
@MapperScan("net.dreamlu.**.mapper.**")
public class MybatisPlusConfig {

    /**
     * 全局参数填充
     */
    @Slf4j
    @Component
    public static class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            log.info("mybatis plus start insert fill ....");
            LocalDateTime now = LocalDateTime.now();
            String userName = SecurityUtils.getUserName();
            this.fillValIfNullByName("createTime", now, metaObject);
            this.fillValIfNullByName("updateTime", now, metaObject);
            this.fillValIfNullByName("createdBy", userName, metaObject);
            this.fillValIfNullByName("updatedBy", userName, metaObject);
            // 逻辑删除的填充，避免数据库没有设置默认值，存储为 1 正常
            this.fillValIfNullByName("status", 1, metaObject);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            log.info("mybatis plus start update fill ....");
            this.fillValIfNullByName("updateTime", LocalDateTime.now(), metaObject);
            this.fillValIfNullByName("updatedBy", SecurityUtils.getUserName(), metaObject);
        }

        /**
         * 填充值，先判断是否有手动设置，优先手动设置的值，例如：job必须手动设置
         *
         * @param fieldName  属性名
         * @param fieldVal   属性值
         * @param metaObject MetaObject
         */
        private void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject) {
            // 用户手动设置的值
            Object userSetValue = this.getFieldValByName(fieldName, metaObject);
            if (userSetValue != null) {
                return;
            }
            this.setFieldValByName(fieldName, fieldVal, metaObject);
        }
    }
}
