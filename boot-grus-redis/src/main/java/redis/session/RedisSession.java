package redis.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.service.RedisService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("deprecation")
class RedisSession implements HttpSession {

    static final String PREFIX = "GSESSION_";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSession.class);
    static String CREATIONTIME_TAG = "__CREATION_TIME__TAG__";
    protected volatile long creationTime = -1L;
    protected volatile int maxInactiveInterval = -10;
    private String sessionId;
    private ServletContext servletContext;
    private RedisService redisService;
    private String redisKey;
    private boolean isNew;

//    private static StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean("stringRedisTemplate");

    RedisSession(String sessionId, RedisService redisService, boolean isNew) {
        this.sessionId = sessionId;
        this.redisService = redisService;
        this.redisKey = PREFIX + sessionId;
        this.isNew = isNew;
        final SessionProp sessionProp = SessionManager.getSessionProp();
        if (this.isNew) {
            creationTime = System.currentTimeMillis();
            this.redisService
            this.redisService.execute(jedis -> {
                jedis.hset(redisKey, CREATIONTIME_TAG, String.valueOf(creationTime));
                jedis.expire(redisKey, sessionProp.getDefaultSessionAliveTime());
                return null;
            });
        } else {
            try {
                this.redisExecutor.execute(jedis -> jedis.expire(redisKey, sessionProp.getDefaultSessionAliveTime()));
            } catch (Exception e) {
                LOGGER.error("unexpected error", e);
            }

        }
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
