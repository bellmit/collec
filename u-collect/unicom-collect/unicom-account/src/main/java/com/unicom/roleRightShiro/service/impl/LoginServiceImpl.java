package com.unicom.roleRightShiro.service.impl;


import com.fasterxml.uuid.Generators;
import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.config.ShiroConfig;
import com.unicom.roleRightShiro.mapper.OrgMapper;
import com.unicom.roleRightShiro.mapper.PermissionMapper;
import com.unicom.roleRightShiro.service.LoginService;
import com.unicom.utils.AESUtils;
import com.unicom.utils.RSAEncrypt;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private OrgMapper OrgMapper;

	/**
	 * 登录
	 */
	@Override
	public Map<String, Object> login(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		String userName = parm.get("username") + "";
		String password = parm.get("password") + "";
		userName = RSAEncrypt.decrypt(userName, RSAEncrypt.privateKey).split(";")[0];
		password = RSAEncrypt.decrypt(password, RSAEncrypt.privateKey).split(";")[0];
		UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
		Subject subject = SecurityUtils.getSubject(); // 获取当前主体

		subject.login(token);
		Map<String, Object> map = new HashMap<String, Object>();
		if (IConstants.SESSION_TYPE_TOKEN.equals(ShiroConfig.sessionType)) {

			Session session = SecurityUtils.getSubject().getSession();
			String id = session.getId().toString();
			log.info("登录获取的sessionId is" + id);
			// 获取秘钥对
			//String key = AESUtils.generateKey();
			byte[] rand = new byte[16];//生成有8个元素的字节数组
			Random r = new Random();
			r.nextBytes(rand);
			String iv = Hex.encodeHexString(rand);
			String publicKey = AESUtils.generateKey();
			session.setAttribute(IConstants.SESSION_PRIVATE_KEY, publicKey);
			session.setAttribute(IConstants.SESSION_AES_IV, iv);

			// 公钥加密
			if (ShiroConfig.enncry) {
				id = RSAEncrypt.encrypt(id, RSAEncrypt.publicKey);
				publicKey = RSAEncrypt.encrypt(publicKey, RSAEncrypt.publicKey);
				iv = RSAEncrypt.encrypt(iv, RSAEncrypt.publicKey);
			}
			map.put("token", id);
			map.put("publicKey", publicKey);
			map.put("iv", iv);

		}
		return ResponseUtils.responseSuccessData(map);
	}

	/**
	 * 获取权限信息
	 */
	public Map<String, Object> getInfo(Map<String, Object> parm) {
		Session session = SecurityUtils.getSubject().getSession();

		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) session.getAttribute(IConstants.SESSION_USER_INFO);
		if (user == null)
			return ResponseUtils.responseErrorNoUser("登录已失效，请重新登录");
		Collection<Map<String, String>> per = null;
		Map<String, Object> u = this.permissionMapper.getRoleByUserId(user.get("userId") + "");
		user.put("roleName", u.get("roleName"));
		user.put("roleLevel", u.get("roleLevel"));
		if (IConstants.ROLE_ADMIN.equals((user.get("isAdmin") + "").trim()))
			per = permissionMapper.getPermissionsByUserIdByAdmin();
		else
			per = permissionMapper.getPermissionsByUserId(user.get("userId") + "");

		this.getPer(per, session, u);
		user.put("orgId", u.get("orgId"));
		if (u.get("orgId") != null) {
			user.put("level", this.OrgMapper.selectOrgLevel(user.get("orgId")));
		}
		u.put("orgName", user.get("orgName"));
		u.put("provinceCode", user.get("provinceCode"));
		u.put("cityCode", user.get("cityCode"));
		u.put("countyCode", user.get("countyCode"));
		u.put("isAdmin", user.get("isAdmin"));
		session.setAttribute(IConstants.SESSION_USER_INFO, user);
		return ResponseUtils.responseSuccessData(u);
	}

	private void getPer(Collection<Map<String, String>> per, Session session, Map<String, Object> user) {

		Collection<String> arr = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (Map<String, String> pe : per) {
			arr.add(pe.get("permissionCode"));
			set.add(pe.get("menuCode"));
		}

		user.put("menuList", set);

		user.put("permissionList", arr);
		session.setAttribute(IConstants.SESSION_USER_PERMISSION, arr);

	}

	@Override
	public Map<String, Object> logOut(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return ResponseUtils.responseSuccessData("登出成功");
	}

	@Override
	public Map<String, Object> updatePassword(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		String oldPassword = parm.get("oldPassword") + "";
		String password = parm.get("password") + "";
		if (StringUtils.isAllBlank(oldPassword) || StringUtils.isAllBlank(password)) {
			return ResponseUtils.responseErrorNoCoder("原始密码或者新密码不能为空！");
		}
		Session session = SecurityUtils.getSubject().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) session.getAttribute(IConstants.SESSION_USER_INFO);
		String reoldPass = user.get("password") + "";
		String salt = user.get("salt") + "";
		int count = 500;
		String hashAlgorithName = "SHA-1";

		Object s = new SimpleHash(hashAlgorithName, oldPassword, salt, count);
		if (!s.toString().equalsIgnoreCase(reoldPass)) {
			return ResponseUtils.responseErrorNoCoder("原始密码不正确！");
		}

		UUID uuid = Generators.randomBasedGenerator().generate();
		// 加密盐值
		ByteSource newSalt = ByteSource.Util.bytes(uuid.toString());
		s = new SimpleHash(hashAlgorithName, password, newSalt, count);


		return null;
	}


}
