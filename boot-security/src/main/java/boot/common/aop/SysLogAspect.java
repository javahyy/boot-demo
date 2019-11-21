package boot.common.aop;

import boot.common.annotation.SysLog;
import boot.system.model.syslog.SysLogEvent;
import boot.util.SysLogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 操作日志使用spring event异步入库
 */
@Aspect
@Order
@Slf4j
@Component
@RequiredArgsConstructor
public class SysLogAspect {
    private final ApplicationEventPublisher publisher;

    @Around("@annotation(sysLog)")
    public Object aroundSysLog(ProceedingJoinPoint point, SysLog sysLog) throws Throwable {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.info("[类名]:{},[方法]:{}", strClassName, strMethodName);

        SysLogEvent sysLogEvent = SysLogUtils.getSysLogDTO();
        sysLogEvent.setOperation(sysLog.value());
        sysLogEvent.setClassMethod(strClassName + "." + strMethodName + "();");
        // 发送异步日志事件
        publisher.publishEvent(sysLogEvent);
        return point.proceed();
    }

}
