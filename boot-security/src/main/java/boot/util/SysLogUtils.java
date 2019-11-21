package boot.util;

import boot.common.SysConstants;
import boot.security.auth.AuthUser;
import boot.system.model.syslog.SysLogEvent;
import net.dreamlu.mica.core.utils.CharPool;
import net.dreamlu.mica.core.utils.ObjectUtil;
import net.dreamlu.mica.core.utils.StringUtil;
import net.dreamlu.mica.core.utils.WebUtil;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 系统日志工具类
 */
public class SysLogUtils {

    public static SysLogEvent getSysLogDTO() {
        SysLogEvent sysLogDTO = new SysLogEvent();
        HttpServletRequest request = WebUtil.getRequest();
        // url
        String requestUrl = request.getRequestURI();
        // paraMap
        Map<String, String[]> paraMap = request.getParameterMap();
        if (ObjectUtil.isEmpty(paraMap)) {
            sysLogDTO.setContent(requestUrl);
        } else {
            // 注意使用 全角 的字符，避免sql问题 【？＝＆】
            StringBuilder builder = new StringBuilder(requestUrl).append(CharPool.QUESTION_MARK);
            paraMap.forEach((key, values) -> {
                builder.append(key).append("＝");
                if ("password".equalsIgnoreCase(key)) {
                    builder.append("******");
                } else {
                    builder.append(StringUtil.join(values));
                }
                builder.append("＆");
            });
            builder.deleteCharAt(builder.length() - 1);
            sysLogDTO.setContent(builder.toString());
        }
        sysLogDTO.setClientIp(WebUtil.getIP());
        AuthUser authUser = SecurityUtils.getUser();
        if (authUser != null) {
            sysLogDTO.setUsername(authUser.getUsername());
            String roles = authUser.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(x -> x.startsWith(SysConstants.SECURITY_ROLE_PREFIX))
                    .map(x -> x.replace(SysConstants.SECURITY_ROLE_PREFIX, ""))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            sysLogDTO.setRoleName(roles);
        }
        return sysLogDTO;
    }

}
