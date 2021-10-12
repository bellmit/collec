package com.unicom.roleRightShiro.shrioDao;

import lombok.Data;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 下午3:20:32
 * 类说明:
 */
@Data
public class RedisSessionDao extends AbstractSessionDAO {


    private long expireTime;


    private RedisTemplate redisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(session.getId(), session, expireTime, TimeUnit.SECONDS);

        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return sessionId == null ? null : (Session) redisTemplate.opsForValue().get(sessionId);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            if (expireTime != 0)
                session.setTimeout(expireTime * 1000);
            redisTemplate.opsForValue().set(session.getId(), session, expireTime, TimeUnit.SECONDS);

        }
    }

    @Override
    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            redisTemplate.opsForValue().getOperations().delete(session.getId());
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return redisTemplate.keys("*");
    }

}
