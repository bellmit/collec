package com.unicom.roleRightShiro.controller;

import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.service.UserService;
import com.unicom.roleRightShiro.utils.VcodeCache;
import com.unicom.utils.ResponseUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;



	@Autowired
	private VcodeCache vcodeCache;


	@RequiresPermissions(value = {"user:list"})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		return userService.list(parm);
	}

	@RequiresPermissions(value = {"user:update", "user:add"}, logical = Logical.OR)
	@RequestMapping(value = "/getAllRoles", method = RequestMethod.GET)
	public Map<String, Object> getAllRoles(@RequestBody(required = false) Map<String, Object> parm) {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		return userService.getAllRoles(user);
	}

	@RequiresPermissions(value = {"user:update"})
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public Map<String, Object> updateUser(@RequestBody Map<String, Object> parm) {
		return userService.updateUser(parm);
	}

	@RequiresPermissions(value = {"user:add"})
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public Map<String, Object> addUser(@RequestBody Map<String, Object> parm) {
		return userService.addUser(parm);
	}

	@RequiresPermissions(value = {"user:delete"})
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public Map<String, Object> deleteUser(@RequestBody Map<String, Object> parm) {
		return userService.addUser(parm);
	}

	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	public Map<String, Object> updatePassword(@RequestBody(required = false) Map<String, Object> parm) {
		return this.userService.updatePassword(parm);
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public Map<String, Object> resetPassword(@RequestBody(required = false) Map<String, Object> parm) {


		return this.userService.resetPassword(parm);

	}

//	@RequestMapping(value = "/smsCode", method = RequestMethod.GET)
//	public Map<String, Object> getSmsCode(String phone, String userName) {
//		Map<String, Object> user = this.userService.getUser(userName);
//		if (!(user.get("phone") + "").equals(phone)) {
//			return ResponseUtils.responseError("账号错误，请检查账号与手机是否匹配！");
//		}
//		if (vcodeCache.get(phone) != null) {
//			return ResponseUtils.responseError("请休息一会再请发送！");
//		}
//		String token = null;
//		String expTime = null;
//		if ((token = SMSClient.SMSCache.getIfPresent("smsToken")) == null) {
//			String uname = "hnswjw";
//			String sha = getSign(uname);
//			Map<String, String> bd = new HashMap<>();
//			bd.put("sha", sha);
//			bd.put("username", uname);
//			Map<String, Object> result = this.smsClient.auth(bd);
//			String status = result.get("status") + "";
//			if (!"200".equals(status)) {
//				return ResponseUtils.responseError("短信验证码发送异常，请稍后再试！");
//			}
//			result = (Map<String, Object>) result.get("data");
//			token = result.get("accessToken") + "";
//			expTime = result.get("expireTime") + "";
//			SMSClient.SMSCache.put("smsToken", token);
//			SMSClient.SMSCache.put("expTime", expTime);
//		}
//		expTime = SMSClient.SMSCache.getIfPresent("expTime");
//		if (token == null) {
//			return ResponseUtils.responseError("发送短信失败，请重新发送！");
//		}
//		String r = RandomStringUtils.randomAlphabetic(4);
//
//		Map<String, Object> body = new HashMap<>();
//		body.put("content", StringUtils.join("验证码为：", r, "(有效期3分钟),请勿泄露给他人。"));
//		ArrayList<String> arr = new ArrayList<>();
//		arr.add(phone);
//		body.put("phones", arr);
//		body.put("signText", "湖南省卫健委");
//
//		Map<String, Object> m = smsClient.sendSMS(token, expTime, body);
//		vcodeCache.set(phone, r); //1分钟可以重发
//		SMSClient.SMSCodeCache.put(phone, r); //3分钟失效
//		return ResponseUtils.responseSuccess();
//	}

	private String getSign(String username) {
		String scerat = "309041b57549088e6cdcf4d3cf988b90";


		return DigestUtils.sha512Hex(StringUtils.join(username, scerat));
	}

	@RequiresPermissions(value = {"org:choose"}, logical = Logical.OR)
	@RequestMapping(value = "/getOrgs", method = RequestMethod.GET)
	public Map<String, Object> getOrgs() {
		return this.userService.getOrgs();
	}



}
