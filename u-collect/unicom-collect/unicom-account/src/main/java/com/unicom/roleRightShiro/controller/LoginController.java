package com.unicom.roleRightShiro.controller;

import com.unicom.roleRightShiro.service.LoginService;
import com.unicom.roleRightShiro.utils.UnicomArithmeticCaptcha;
import com.unicom.roleRightShiro.utils.VcodeCache;
import com.unicom.utils.RSAEncrypt;
import com.unicom.utils.ResponseUtils;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author yangpeng
 * @version 创建时间：2020年8月10日 下午12:22:18
 * 类说明:
 */
@RestController
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;

	@Autowired
	private VcodeCache vcodeCache;

	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public Map<String, Object> login_get(@RequestParam Map<String, Object> parm) {
		return this.login(parm);
	}


	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public Map<String, Object> login(@RequestBody Map<String, Object> parm) {
		String key = (String) parm.get("vcodeKey");
		String vcode = (String) parm.get("vcode");
		key = RSAEncrypt.decrypt(key, RSAEncrypt.privateKey);
		vcode = RSAEncrypt.decrypt(vcode, RSAEncrypt.privateKey);
		Object validateCode = vcodeCache.get(key);
		if (StringUtils.isAllBlank(key) || StringUtils.isAllBlank(vcode) || validateCode == null) {
			return ResponseUtils.responseErrorNoUser("验证码已失效！");
		}

		if (!vcode.equals(validateCode)) {
			return ResponseUtils.responseErrorNoUser("验证码错误，请重新输入！");
		}

		return loginService.login(parm);
	}

	@ApiOperation(value = "验证码", notes = "验证码")
	@ApiImplicitParams({@ApiImplicitParam(dataType = "String", name = "status", value = "状态", required = true)})
	@RequestMapping(value = "/vcode")
	public Map<String, Object> vcode(HttpServletRequest request, HttpServletResponse response) {
		ArithmeticCaptcha captcha = new UnicomArithmeticCaptcha(130, 48);
		captcha.setLen(2);  // 几位数运算，默认是两位
		// 获取运算的结果：5

		String key = UUID.randomUUID().toString();
		vcodeCache.set(key, captcha.text());
		Map<String, Object> vcode = new HashMap<String, Object>();
		vcode.put("code", key);
		vcode.put("img", captcha.toBase64());
		return ResponseUtils.responseSuccessData(vcode);
	}


	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	public Map<String, Object> getInfo_get(@RequestParam Map<String, Object> parm) {
		return this.getInfo(parm);
	}


	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	public Map<String, Object> getInfo(@RequestParam Map<String, Object> parm) {
		return this.loginService.getInfo(parm);
	}


	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public Map<String, Object> logout(@RequestBody(required = false) Map<String, Object> parm) {
		return this.loginService.getInfo(parm);
	}




}
