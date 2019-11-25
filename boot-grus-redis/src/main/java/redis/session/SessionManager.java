package redis.session;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.service.RedisService;
import redis.util.SessionIdGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @Description session管理
 */
public class SessionManager {


    @VisibleForTesting
    static final String SESSION_ID_COOKIE_NAME = "GSESSIONID";
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    private static final String SESSION_ID_TAG = "___SESSION_ID_TAG____";
    private static final SessionIdGenerator SESSIONIDGENERATOR = new SessionIdGenerator();

    @Autowired
    RedisService redisService;
    //todo 可选择：环境检查 开发， 测试， 预发/生产  ,  调用频率限制

    /**
     * 获取当前请求的session，如果没有会话或者之前会话已过期，会生成一个新的
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    public static HttpSession getCurrentSession(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        Object value = httpServletRequest.getAttribute(SESSION_ID_TAG);
        if (value == null) {
            value = getCurrentSession0(httpServletRequest, httpServletResponse, true).setServletContext(httpServletRequest.getServletContext());
            httpServletRequest.setAttribute(SESSION_ID_TAG, value);
        }
        return (HttpSession) value;
    }

    public static HttpSession getCurrentSession(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, boolean writeCookie) {
        Object value = httpServletRequest.getAttribute(SESSION_ID_TAG);
        if (value == null) {
            value = getCurrentSession0(httpServletRequest, httpServletResponse, writeCookie).setServletContext(httpServletRequest.getServletContext());
            httpServletRequest.setAttribute(SESSION_ID_TAG, value);
        }
        return (HttpSession) value;
    }

    public static HttpSession getSessionById(final String sessionId) {
        if (exists(sessionId))
            return new RedisSession(sessionId, redisExecutor, false);
        return null;
    }

    private static RedisSession getCurrentSession0(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, boolean writeCookie) {
        final String sessionId = getCookieValue(SESSION_ID_COOKIE_NAME, httpServletRequest);
        if (sessionId != null && exists(sessionId)) {
            return new RedisSession(sessionId, redisExecutor, false);
        }

        //控制session产生的频次，防止DDOS攻击和爬虫
        if (frequencyController.isOverLimit(httpServletRequest, httpServletResponse)) {
            throw new RuntimeException("too many request");
        }

        final String newSessionId = SESSIONIDGENERATOR.generateSessionId();
        LOGGER.info("generate session,old {} ,new {}", sessionId, newSessionId);
        if (writeCookie) {
            Cookie cookie = new Cookie(SESSION_ID_COOKIE_NAME, newSessionId);
            cookie.setMaxAge(getSessionProp().getMaxSessionCookieAliveTime());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(secure); //true为只允许https
            httpServletResponse.addCookie(cookie);
        }
        return new RedisSession(newSessionId, redisExecutor, true);
    }

    static SessionProp getSessionProp() {
        return GlobalGconfConfig.getConfig().getBean("session-prop.properties", SessionProp.class);
    }

    private static boolean exists(final String sessionId) {
        String value = redisExecutor.execute(jedis -> jedis.hget(RedisSession.PREFIX + sessionId, RedisSession.CREATIONTIME_TAG));

        if (value == null || value.isEmpty()) {
            return false;
        }

        try {
            return System.currentTimeMillis() - Long.valueOf(value) < getSessionProp().getMaxSessionAliveTime();
        } catch (NumberFormatException e) {
            LOGGER.error(value, e);
        }
        return false;
    }
    private static String getCookieValue(String cookieName, HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(cookieName);
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return URLDecoder.decode(cookie.getValue(),  Charset.forName("UTF-8"));
            }
        }
        return null;
    }
}