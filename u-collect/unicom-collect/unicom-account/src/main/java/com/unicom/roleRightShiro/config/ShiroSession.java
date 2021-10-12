package com.unicom.roleRightShiro.config;

import com.unicom.common.IConstants;
import com.unicom.utils.RSAEncrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 上午10:29:56
 * 类说明:使用传统session或者token
 */
@Slf4j
@PropertySource(value = "classpath:customConfig.properties")
public class ShiroSession extends DefaultWebSessionManager {
    /**
     * 定义的请求头中使用的标记key，用来传递 token
     */
    private static final String AUTH_TOKEN = "authToken";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    public ShiroSession() {
        super();
        //设置 shiro session 失效时间，默认为30分钟，这里可以设置时间
        //setGlobalSessionTimeout(MILLIS_PER_MINUTE * 15);

        setGlobalSessionTimeout(MILLIS_PER_MINUTE * 2);
        setSessionIdUrlRewritingEnabled(false);
    }


    /**
     * 根据情况获取sessionid
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //获取请求头中的 AUTH_TOKEN 的值，如果请求头中有 AUTH_TOKEN 则其值为sessionId。shiro就是通过sessionId 来控制的
        String sessionId = WebUtils.toHttp(request).getHeader(AUTH_TOKEN);

        log.debug("进入session筛选,session配置类型是" + ShiroConfig.sessionType);

        if (sessionId == null || IConstants.SESSION_TYPE_SESSION.equals(ShiroConfig.sessionType)) {
            //如果没有携带id参数则按照父类的方式在cookie进行获取sessionId
            return super.getSessionId(request, response);

        } else {  //请求头中如果有 authToken, 则其值为sessionId

            //如果使用了加密，那么使用公钥进行解密
            if (ShiroConfig.enncry) {
                String token = RSAEncrypt.decrypt(sessionId, RSAEncrypt.privateKey);
                sessionId = token.split(";")[0];
            }
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            //sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
        }
    }

}
