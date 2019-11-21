package boot.security.auth;

import boot.common.exception.LocalizedException;
import boot.config.DreamSecurityProperties;
import boot.security.service.DreamUserDetailsService;
import boot.security.service.UserLockService;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.mica.captcha.servlet.MicaCaptchaServlet;
import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义授权处理，添加验证码
 *
 * @author L.cm
 */
public class DreamAuthenticationProvider extends DaoAuthenticationProvider {
    @Getter
    @Setter
    private DreamSecurityProperties dreamProperties;
    @Getter
    @Setter
    private MicaCaptchaServlet dreamCaptcha;
    @Getter
    @Setter
    private CacheManager cacheManager;
    private Cache passwordRetryCache;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.isAuthenticated()) {
            return auth;
        }
        DreamWebAuthenticationDetails details = (DreamWebAuthenticationDetails) auth.getDetails();
        String verificationCode = details.getVerificationCode();
        if (StringUtil.isBlank(verificationCode)) {
            throw new LocalizedException("verification.code.blank", "The verification code is blank.");
        }
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        HttpServletResponse response = attributes.getResponse();
        if (dreamCaptcha.validate(response, verificationCode)) {
            return super.authenticate(auth);
        } else {
            throw new LocalizedException("verification.code.incorrect", "The verification code is incorrect.");
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // 添加用户锁定的功能，用户尝试登录密码错误太多次锁定账号
        String username = userDetails.getUsername();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username, AtomicInteger.class);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        int retryLimit = dreamProperties.getLogin().getRetryLimit();
        if (retryCount.incrementAndGet() > retryLimit) {
            //if retry count > retryLimit
            logger.warn("username: " + username + " tried to login more than " + retryLimit + " times in period");
            UserLockService userLockService = this.getUserLockService();
            userLockService.updateLockUser((AuthUser) userDetails);
            throw new LocalizedException("login.retry.limit", new Object[]{username, retryLimit},
                    "User " + username + " password has been incorrectly typed more than " + retryLimit + " times, the account has been locked.");
        } else {
            passwordRetryCache.put(username, retryCount);
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
        //clear retry data
        passwordRetryCache.evict(username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    protected void doAfterPropertiesSet() {
        super.doAfterPropertiesSet();
        Assert.notNull(dreamProperties, "dreamProperties is null");
        Assert.notNull(dreamCaptcha, "dreamCaptcha is null");
        Assert.notNull(cacheManager, "cacheManager is null");
        String retryLimitCacheName = dreamProperties.getLogin().getRetryLimitCacheName();
        this.passwordRetryCache = cacheManager.getCache(retryLimitCacheName);
        Assert.notNull(this.passwordRetryCache, "retryLimitCache retryLimitCacheName: " + retryLimitCacheName + " is not config.");
    }

    private DreamUserDetailsService getUserLockService() {
        UserDetailsService userDetailsService = super.getUserDetailsService();
        return (DreamUserDetailsService) userDetailsService;
    }
}
