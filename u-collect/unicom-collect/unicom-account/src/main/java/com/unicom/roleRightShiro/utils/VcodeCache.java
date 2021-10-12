package com.unicom.roleRightShiro.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.config.ShiroConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class VcodeCache {
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;


	private final static Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();


	@Autowired
	private ShiroConfig shiroConfig;


	public void set(String key, Object value) {
		if (IConstants.CACHE_LOCAL.equals(shiroConfig.getCacheType())) {
			cache.put(key, value);
		} else if (IConstants.CACHE_REDIS.equals(shiroConfig.getCacheType()))
			redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
	}

	public Object get(String key) {
		Object value = null;
		if (IConstants.CACHE_LOCAL.equals(shiroConfig.getCacheType())) {
			value = cache.getIfPresent(key);
		} else if (IConstants.CACHE_REDIS.equals(shiroConfig.getCacheType()))
			value = redisTemplate.opsForValue().get(key);

		return value;
	}


}
