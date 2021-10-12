package com.unicom.roleRightShiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.common.RequestWapper;
import com.unicom.roleRightShiro.utils.RequestToJsonUtils;
import com.unicom.utils.RSAEncrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 下午3:33:33
 * 类说明:该方法用于过滤options试探请求以及处理加解密和使用非session的token
 */
@Slf4j
public class CustomerTokenFilter extends AccessControlFilter {
	private Cache<String, String> cache;
	private final long replayTimeOut;
	private final RedisTemplate<String, String> redisTemplate;
	private final String cacheType;

	public CustomerTokenFilter(String cacheType, long replayTimeOut, RedisTemplate<String, String> redisTemplate) {
		this.replayTimeOut = replayTimeOut;
		this.redisTemplate = redisTemplate;
		this.cacheType = cacheType;
		// TODO Auto-generated constructor stub
		if (IConstants.CACHE_LOCAL.equals(cacheType))
			cache = CacheBuilder.newBuilder().expireAfterWrite(replayTimeOut, TimeUnit.SECONDS).build();
	}


	@Override
	public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletRequest requestWrapper = null;
		if (request instanceof HttpServletRequest) {
			String contentType = request.getContentType();
			log.debug("contentType:" + contentType);
			if (contentType == null || !contentType.contains("multipart/form-data;"))
				requestWrapper = new RequestWapper((HttpServletRequest) request);
		}
		if (requestWrapper != null)
			super.doFilterInternal(requestWrapper, response, chain);
		else
			super.doFilterInternal(request, response, chain);
	}


	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		// TODO Auto-generated method stub
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			setHeader(httpRequest, httpResponse);
			return true;
		}

		return super.onPreHandle(request, response, mappedValue);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Session session = SecurityUtils.getSubject().getSession();

		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) session.getAttribute(IConstants.SESSION_USER_INFO);

		if (user == null) {
			setHeader((HttpServletRequest) request, (HttpServletResponse) response);
			return this.responseMessage((HttpServletResponse) response, "登录已失效，请重新登录！", IConstants.RESULT_INT_ERROR_NOUSER);
		}

		HttpServletRequest req = (HttpServletRequest) request;
		boolean flag = this.validate(req, (HttpServletResponse) response, session);
		if (!flag)
			setHeader((HttpServletRequest) request, (HttpServletResponse) response);
		return flag;

	}

	private boolean responseMessage(HttpServletResponse response, String message, int code) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject obj = new JSONObject();
		try {
			obj.put("message", message);
			obj.put("code", code);
			out.println(obj.toJSONString());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500);
			return false;
		}
	}

	/**
	 * 为response设置header，实现跨域
	 */
	private void setHeader(HttpServletRequest request, HttpServletResponse response) {
		// 跨域的header设置
		response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", request.getMethod());
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
		// 防止乱码，适用于传输JSON数据
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		response.setStatus(org.springframework.http.HttpStatus.OK.value());
	}

	private boolean validate(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException {
		// 校验信息是否篡改，伪造
		String vtoken = request.getHeader("authToken");
		// 解密token数据
		vtoken = RSAEncrypt.decrypt(vtoken, RSAEncrypt.privateKey);
		String[] ks = vtoken.split(";");
		String token = ks[0];
		String rds = ks[1];
		String time = ks[2];

		//String rando = ks[2];
		String sign = request.getHeader("sign");

		//log.debug("vtoken is:"+vtoken+" sign  is:"+sign+" time is:"+time);
		//校验是否篡改
		if (!falsifyValidate(request, token, rds, time, sign)) {
			return this.responseMessage(response, "请求校验失败！信息可能已被篡改！",
					IConstants.RESULT_INT_ERROR_NOROLE);
		}
		if (!replayValidate(sign, time)) {
			return this.responseMessage(response, "不能重复提交请求！",
					IConstants.RESULT_INT_ERROR_NOROLE);
		}

		return true;
	}

	/**
	 * 回放校验
	 *
	 * @param remoteSign
	 * @param currentTime
	 * @param timevz
	 * @return
	 */
	private boolean replayValidate(String remoteSign, String timevz) {
		Long currentTime = System.currentTimeMillis();
		log.info("回放攻击校验开始.................");
		// 防止回放操作
		if (IConstants.CACHE_LOCAL.equals(cacheType))
			return this.dealLocal(remoteSign, currentTime, timevz);
		else
			return this.dealRedis(remoteSign, currentTime, timevz);
	}

	private boolean dealLocal(String remoteSign, Long currentTime, String timevz) {
		// 防止回放操作 操作在缓存里
		if (cache.getIfPresent(remoteSign) != null)
			return false;

		// 如果不在回放记录里，但是这次操作与记载的时间间隔1分以上，不通过
		if (currentTime - Long.parseLong(timevz) > replayTimeOut * 1000)
			return false;

		// 将此次操作放入缓存
		cache.put(remoteSign, currentTime + "");
		return true;
	}

	/**
	 * 篡改和伪造校验
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean falsifyValidate(HttpServletRequest request, String token, String rds, String time, String sign) {
		// 解密token数据

		String vsString = rds + time + token;
		String json = "";

		if (request.getContentType() != null && request.getContentType().indexOf("application/json") > -1) {
			try {
				RequestWapper requestw = new RequestWapper(request);
				json = RequestToJsonUtils.getRequestJsonString(requestw);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		if (StringUtils.isNotBlank(json)) {
//			JSONObject jo = JSONObject.parseObject(json);
//			
//			ArrayList<String> arr = new ArrayList<String>();
//			for (String key : jo.keySet()) {
//				String o=jo.getString(key);
//				arr.add(key + o.toString());
//
//			}
//			Collections.sort(arr);
//			json = "";
//			for (String a : arr) {
//				json = json + a;
//			}
//			
//			json=json.replaceAll("\\[", "");
//			json=json.replaceAll("\\]", "");
//			
//			vsString = vsString + json;
//		}
		vsString = vsString + json;
		log.debug("组合后的数据是：" + vsString);

		String vsign = DigestUtils.md5Hex(vsString);


		// 获取私钥
//		String privateKey=(String)session.getAttribute(IConstants.SESSION_PRIVATE_KEY);
//		if(ShiroConfig.enncry)
//			RSAEncrypt.decrypt(token, privateKey);

		return vsign.equals(sign);
	}

	private boolean dealRedis(String remoteSign, Long currentTime, String timevz) {
		log.debug("回放的签名是" + remoteSign + "  时间" + timevz);
		// 防止回放操作 操作在缓存里
		if (redisTemplate.opsForValue().get(remoteSign) != null)
			return false;

		// 如果不在回放记录里，但是这次操作与记载的时间间隔1分以上，不通过
		if (currentTime - Long.parseLong(timevz) > replayTimeOut * 1000)
			return false;

		// 将此次操作放入缓存
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.multi();
		redisTemplate.opsForValue().set(remoteSign, currentTime + "", replayTimeOut, TimeUnit.SECONDS);
		redisTemplate.exec();
		return true;
	}

}
