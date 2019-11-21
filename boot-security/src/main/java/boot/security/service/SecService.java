package boot.security.service;

import boot.security.auth.AuthUser;
import boot.system.model.Resource;
import boot.util.SecurityUtils;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 权限判断
 * url: https://stackoverflow.com/questions/41434231/use-spring-security-in-thymeleaf-escaped-expressions-in-javascript
 */
@Service("sec")
@AllArgsConstructor
public class SecService {
    private final IResourceService resourceService;

    /**
     * 提供给页面输出当前用户
     *
     * @return {AuthUser}
     */
    public AuthUser currentUser() {
        return SecurityUtils.getUser();
    }

    /**
     * 已经授权的
     *
     * @return 是否授权
     */
    public boolean isAuthenticated() {
        return this.currentUser() != null;
    }

    /**
     * 判断请求是否有权限
     *
     * @param request        HttpServletRequest
     * @param authentication 认证信息
     * @return 是否有权限
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        AuthUser authUser = SecurityUtils.getUser(authentication);
        if (authUser == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.isEmpty()) {
            return false;
        }
        Integer adminId = authUser.getUserId();
        List<Resource> resourceList = resourceService.findAllByAdminId(adminId);
        return resourceList.stream()
                .map(Resource::getUrl)
                .filter(StringUtil::isNotBlank)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(x, request.getRequestURI()));
    }

    /**
     * 判断按钮是否有xxx:xxx权限
     *
     * @param permission 权限
     * @return {boolean}
     */
    public boolean hasPermission(String permission) {
        if (StringUtil.isBlank(permission)) {
            return false;
        }
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null) {
            return false;
        }
        AuthUser authUser = SecurityUtils.getUser(authentication);
        if (authUser == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtil::isNotBlank)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));
    }
}
