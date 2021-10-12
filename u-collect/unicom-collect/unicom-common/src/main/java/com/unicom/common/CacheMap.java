package com.unicom.common;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class CacheMap {
	//public static  final ConcurrentHashMap<String, List<WarnResultModel>> cache=new ConcurrentHashMap<String,  List<WarnResultModel>>();
	public static final ConcurrentHashMap<String, Map<String, String>> userCache = new ConcurrentHashMap<String, Map<String, String>>();
	public static final ConcurrentHashMap<String, String> kaptchaCache = new ConcurrentHashMap<String, String>();
	public static final ConcurrentHashMap<String, String> secretKeyCache = new ConcurrentHashMap<String, String>();

	public static final ConcurrentHashMap<String, String> operationCache = new ConcurrentHashMap<String, String>(); //操作缓存

	public static final ConcurrentHashMap<Integer, String> dictCache = new ConcurrentHashMap<>();

	public static final ConcurrentHashMap<Integer, String> opsCache = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Integer, String> orgCache = new ConcurrentHashMap<>();


	@Value("${epidemic.userSessionTimes}")
	private String sessionTimes;

	public int getSessionTimes() {
		return Integer.parseInt(this.sessionTimes);
	}

	public void setSessionTimes(String sessionTimes) {
		if (StringUtils.isEmpty(sessionTimes)) {
			sessionTimes = "10";
		}
		this.sessionTimes = sessionTimes;
	}



	/*
	 * @Scheduled(cron = "0 0/10 * * * ?") public void timeCleaning() {
	 * if(cache.size()>IConstants.CACHE_MAX_SIZE) { log.info("cache cleare");
	 * cache.clear(); return;
	 *
	 * } Long current=System.nanoTime();
	 *
	 * for(String timex:cache.keySet()) { String ti=new
	 * String(Base64.decodeBase64(timex.getBytes())); long time=Long.parseLong(ti);
	 * if(current-time>600000000000L) { cache.remove(timex); } }
	 *
	 * }
	 */

	@Scheduled(cron = "0 0/5 * * * ?")
	public void timeOut() {
		log.info("....................session 清理开始..................");
		Long current = System.nanoTime();
		log.debug("........usersessionTimes:" + this.getSessionTimes());
		//userCache.keySet().removeIf(key->System.currentTimeMillis()-Long.parseLong(userCache.get(key).get("time"))>60000000000L);
		for (String key : userCache.keySet()) {
			Map<String, String> user = userCache.get(key);
			long before = Long.parseLong(user.get("time"));
			if (current - before > this.getSessionTimes() * 60000000000L) { //60000000000L=1分钟
				log.debug("....................session clearn  user..................");
				userCache.remove(key);
			}
		}
		log.info("....................session 清理完毕..................");
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	public void kaptchaTime() {
		log.info("....................kaptcha and secer clearn start..................");
		Long current = System.currentTimeMillis();
		//kaptchaCache.keySet().removeIf(key->System.currentTimeMillis()-Long.parseLong(kaptchaCache.get(key))>60000L);

		for (String key : kaptchaCache.keySet()) {
			String time = kaptchaCache.get(key);
			long before = Long.parseLong(time);
			if (current - before > 60000) {
				userCache.remove(key);
			}
		}

		//secretKeyCache.keySet().removeIf(key->System.currentTimeMillis()-Long.parseLong(secretKeyCache.get(key))>60000L);

		for (String key : secretKeyCache.keySet()) {
			String time = secretKeyCache.get(key);
			long before = Long.parseLong(time);
			if (current - before > 60000) {
				secretKeyCache.remove(key);
			}
		}

		log.info("....................kaptcha and secer clearn  end..................");
	}


	/**
	 * 操作清除
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void opertionClearTime() {
		log.info("....................operationCache  clearn start..................");
		Long current = System.currentTimeMillis();
		//operationCache.keySet().removeIf(key->System.currentTimeMillis()-Long.parseLong(operationCache.get(key))>60000);
		for (String key : operationCache.keySet()) {
			String time = operationCache.get(key);
			long before = Long.parseLong(time);
			if (current - before > 60000) {
				operationCache.remove(key);
			}
		}


		log.info("....................operationCache clearn  end..................");
	}


}
