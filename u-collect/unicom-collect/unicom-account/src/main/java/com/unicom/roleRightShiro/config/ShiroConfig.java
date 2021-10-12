package com.unicom.roleRightShiro.config;


import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.common.CustomerSessionUUID;
import com.unicom.roleRightShiro.filter.CustomerTokenFilter;
import com.unicom.roleRightShiro.realm.RoleRightCommonRealm;
import com.unicom.roleRightShiro.shrioDao.RedisSessionDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 下午12:22:18
 * 类说明:
 */
@MapperScan("com.unicom.roleRightShiro.mapper")
@Configuration
@Slf4j
public class ShiroConfig {

	@Getter
	@Value("${unicom.session.cacheType:local}")
	private String cacheType;

	@Value("${unicom.session.redis.expireTime:0}")
	private long expireTime;
	@Value("${unicom.session.timeOut:30}")
	private long timeOut;
	@Value("${unicom.session.replay.timeOut:120}")
	private long replayTimeOut;

	@Value("${unicom.session.useCookie:true}")
	private Boolean useCookie;


	//session的类型（默认为session,可以为token,jwtToken等）
	public static String sessionType;

	//是否开启加密
	public static Boolean enncry;


	@Autowired
	private RedisTemplate redisTemplate;

	@Bean
	public ShiroFilterChainDefinition shiroFilterChainDefinition() {
		DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
		chainDefinition.addPathDefinition("/logout", "logout");
		chainDefinition.addPathDefinition("/login/logout", "logout");
		chainDefinition.addPathDefinition("/**", "authc"); // all paths are managed via annotations

		// or allow basic authentication, but NOT require it.
		// chainDefinition.addPathDefinition("/**", "authcBasic[permissive]");
		return chainDefinition;
	}

	@Bean("shiroFilterFactoryBean")
	public ShiroFilterFactoryBean factory(SessionsSecurityManager securityManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager);
		// 添加自己的过滤器
		Map<String, Filter> filterMap = new HashMap<>();
		filterMap.put("token", new CustomerTokenFilter(cacheType, replayTimeOut, redisTemplate));
		factoryBean.setFilters(filterMap);


		factoryBean.setUnauthorizedUrl("/401");

		/*
		 * 自定义url规则
		 * http://shiro.apache.org/web.html#urls-
		 */
		Map<String, String> filterRuleMap = new HashMap<>();
		// 所有请求通过我们自己的Filter
		filterRuleMap.put("/login/auth", "anon");
		filterRuleMap.put("/login/vcode", "anon");
		filterRuleMap.put("/user/smsCode", "anon");
		filterRuleMap.put("/user/project/page2","anon");

		filterRuleMap.put("/project/template/**", "anon");

//        filterRuleMap.put("/tender/project/export","anon");
		filterRuleMap.put("/**", "token");


		// 访问401和404页面不通过我们的Filter
		filterRuleMap.put("/401", "anon");
		factoryBean.setFilterChainDefinitionMap(filterRuleMap);
		return factoryBean;
	}


	@Bean
	public SessionsSecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm());
		//自定义的shiro session 缓存管理器
		securityManager.setSessionManager(sessionManager());

		return securityManager;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		defaultAdvisorAutoProxyCreator.setUsePrefix(true);
		return defaultAdvisorAutoProxyCreator;
	}

	/*
	 * @Bean public AuthorizationAttributeSourceAdvisor
	 * authorizationAttributeSourceAdvisor(SecurityManager manager){
	 * AuthorizationAttributeSourceAdvisor advisor = new
	 * AuthorizationAttributeSourceAdvisor();
	 * advisor.setSecurityManager((org.apache.shiro.mgt.SecurityManager) manager);
	 * return advisor; }
	 */


	@Bean
	public Realm realm() {
		Realm realm = new RoleRightCommonRealm();
		return realm;
	}

	@ModelAttribute(name = "subject")
	public Subject subject() {
		return SecurityUtils.getSubject();
	}


	/**
	 * 自定义的 shiro session 缓存管理器，用于跨域等情况下使用 token 进行验证，不依赖于sessionId
	 *
	 * @return
	 */
	@Bean
	public SessionManager sessionManager() {
		//将我们继承后重写的shiro session 注册
		ShiroSession shiroSession = new ShiroSession();
		// shiroSession.setGlobalSessionTimeout(MILLIS_PER_MINUTE * 2);
		//session存储
		shiroSession.setGlobalSessionTimeout(1000 * timeOut);

		//是否使用cookie

		shiroSession.setSessionIdCookieEnabled(useCookie);
		if (IConstants.CACHE_REDIS.equals(cacheType)) {
			log.info("使用redis 作为session 存储");
			RedisSessionDao redisSessionDao = new RedisSessionDao();
			if (expireTime == 0)
				expireTime = timeOut;
			redisSessionDao.setExpireTime(expireTime);
			redisSessionDao.setRedisTemplate(redisTemplate);
			redisSessionDao.setSessionIdGenerator(new CustomerSessionUUID());
			shiroSession.setSessionDAO(redisSessionDao);
		} else {
			log.info("使用内存 作为session 存储");
			shiroSession.setSessionDAO(new EnterpriseCacheSessionDAO());
		}
		return shiroSession;
	}

	@Value("${unicom.session.sessionType:session}")
	public void setSessionType(String sessionType) {
		ShiroConfig.sessionType = sessionType;
	}

	@Value("${unicom.session.encry:true}")
	public void setEncry(Boolean enncry) {
		ShiroConfig.enncry = enncry;
	}
}
