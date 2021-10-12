package com.unicom.roleRightShiro.service.impl;

import com.fasterxml.uuid.Generators;
import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.mapper.OrgMapper;
import com.unicom.roleRightShiro.mapper.RoleMapper;
import com.unicom.roleRightShiro.mapper.UserMapper;
import com.unicom.roleRightShiro.service.UserService;
import com.unicom.sms.SMSClient;
import com.unicom.utils.AESUtils;
import com.unicom.utils.PageUtils;
import com.unicom.utils.RSAEncrypt;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private OrgMapper orgMapper;

	@Autowired
	private RoleMapper roleMapper;


	/**
	 * 返回单个用户信息
	 */
	public Map<String, Object> getUser(String username) {

		// TODO Auto-generated method stub
		return this.userMapper.select(username);
	}


	/**
	 * 返回用户列表
	 *
	 * @param parm
	 * @return
	 */
	public Map<String, Object> list(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		parm.put("orgId", user.get("orgId"));
		parm.put("roleLevel", user.get("roleLevel"));

		int count = this.userMapper.count(parm);
		PageUtils.page(parm, count);
		Collection<Map<String, Object>> coll = this.userMapper.selectAll(parm);


		return ResponseUtils.responseSuccessData(coll, count);
	}


	@Override
	public Map<String, Object> getAllRoles(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return ResponseUtils.responseSuccessData(this.userMapper.selectAllRole(parm));
	}


	@Override
	public Map<String, Object> updateUser(Map<String, Object> parm) {
		// TODO Auto-generated method stub


		String pass = parm.get("password") + "";
		if (StringUtils.isNotBlank(pass)) {
			String hashAlgorithName = "SHA-1";
			UUID uuid = Generators.randomBasedGenerator().generate();
			// 加密盐值
			ByteSource salt = ByteSource.Util.bytes(uuid.toString());
			int count = 500;

			Object s = new SimpleHash(hashAlgorithName, pass, salt, count);
			parm.put("password", s.toString());
			parm.put("salt", uuid.toString());
		}

		int i = this.userMapper.update(parm);
		parm.remove("deleteStatus");
		if (i > 0) {
			return ResponseUtils.responseSuccess();
		}
		return ResponseUtils.responseError("修改失败");
	}

	@Override
	public Map<String, Object> addUser(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		//查询是否
		if(!this.validateUserAccount(parm))
			return ResponseUtils.responseError("账户数已到达上限,无法添加,请联系客户经理增加授权账户！");
		String pass = parm.get("password") + "";
		Map<String, Object> validateResult = this.validateUser(parm);
		if (validateResult != null)
			return validateResult;
		String hashAlgorithName = "SHA-1";
		UUID uuid = Generators.randomBasedGenerator().generate();
		// 加密盐值
		ByteSource salt = ByteSource.Util.bytes(uuid.toString());
		int count = 500;

		Object s = new SimpleHash(hashAlgorithName, pass, salt, count);
		parm.put("password", s.toString());
		parm.put("salt", uuid.toString());
		this.userMapper.insert(parm);

		return ResponseUtils.responseSuccess();
	}

	private Map<String, Object> validateUser(Map<String, Object> parm) {
		String usernmae = parm.get("username") + "";
		String pass = parm.get("password") + "";
		String roleId = parm.get("roleId") + "";
		String orgId = parm.get("orgId") + "";
		if (StringUtils.isAllBlank(pass) || StringUtils.isAllBlank(usernmae)) {
			return ResponseUtils.responseErrorNoCoder("添加失败，用户名或密码不能为空");
		}

		if (StringUtils.isAllBlank(roleId)) {
			return ResponseUtils.responseErrorNoCoder("添加失败，角色不能为空");
		}


		//验证用户名是否已存在
		int countuser = this.userMapper.countUserName(parm);
		if (countuser > 0) {
			return ResponseUtils.responseErrorNoCoder("添加失败，该用户已存在！");
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		Map<String, Object> m = this.roleMapper.selectOne(parm);

		if ((Integer) m.get("level") < (Integer) user.get("roleLevel")) {
			return ResponseUtils.responseErrorNoCoder("添加失败，不能选择比自己权限等级高的角色！");
		}


		//验证机构选择是否越权

		if (user.get("orgId") != null) {
			if (StringUtils.isAllBlank(orgId))
				return ResponseUtils.responseErrorNoCoder("添加失败，必须选择机构！");

			Set<Integer> set = orgMapper.selectAllById(parm);

			if (!set.contains(Integer.parseInt(orgId))) {
				return ResponseUtils.responseErrorNoCoder("添加失败，非法的机构！");
			}
		}

		return null;
	}


	@Override
	public Map<String, Object> deleteUser(Map<String, Object> parm) {
		// TODO Auto-generated method stub

		this.userMapper.delete(parm);
		return ResponseUtils.responseSuccess();
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
		String privateKey = session.getAttribute(IConstants.SESSION_PRIVATE_KEY) + "";
		String iv = session.getAttribute(IConstants.SESSION_AES_IV) + "";
		log.info("key is" + privateKey + "iv is" + iv);
		oldPassword = AESUtils.decode(privateKey, oldPassword, iv);
		password = AESUtils.decode(privateKey, password, iv);
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

		user.put("password", s.toString());
		user.put("salt", uuid.toString());

		this.userMapper.updatePassword(user);
		return ResponseUtils.responseSuccess();
	}


	@Override
	public Map<String, Object> resetPassword(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		Map<String, Object> user = getUser(parm.get("userName") + "");

		if (user == null || !(user.get("phone") + "").equals(parm.get("phone") + "")) {
			return ResponseUtils.responseError("账号错误，请检查账号与手机是否匹配！");
		}

		String code = parm.get("smsCode") + "";
		String phone = parm.get("phone") + "";
		Session session = SecurityUtils.getSubject().getSession();
		code = RSAEncrypt.decrypt(code, RSAEncrypt.privateKey).split(";")[0];

		if (!code.equals(SMSClient.SMSCodeCache.getIfPresent(phone))) {
			return ResponseUtils.responseError("修改失败，验证码错误！");
		}

		String password = parm.get("password") + "";
		password = RSAEncrypt.decrypt(password, RSAEncrypt.privateKey).split(";")[0];
		//password= RSAEncrypt.decrypt(password, RSAEncrypt.privateKey).split(";")[0];
		if (StringUtils.isAllBlank(password)) {
			return ResponseUtils.responseErrorNoCoder("新密码不能为空！");
		}

		int count = 500;
		String hashAlgorithName = "SHA-1";

		UUID uuid = Generators.randomBasedGenerator().generate();
		// 加密盐值
		ByteSource newSalt = ByteSource.Util.bytes(uuid.toString());
		SimpleHash s = new SimpleHash(hashAlgorithName, password, newSalt, count);
		parm.put("password", s.toString());
		parm.put("salt", uuid.toString());

		this.userMapper.resetPassword(parm);
		SMSClient.SMSCodeCache.invalidate(parm.get("phone")); //移除验证码
		return ResponseUtils.responseSuccess();
	}


	@Override
	public Map<String, Object> getOrgs() {
		// TODO Auto-generated method stub
		Session session = SecurityUtils.getSubject().getSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) session.getAttribute(IConstants.SESSION_USER_INFO);
		Collection<Map<String, Object>> orgs = this.orgMapper.selectAll(user);
		return ResponseUtils.responseSuccessData(orgs);
	}


	@Override
	public Integer getUserOrgLevel(Object id) {
		// TODO Auto-generated method stub
		return this.orgMapper.selectOrgLevel(id);
	}

	//校验账户数量
	private boolean validateUserAccount(Map<String,Object> parm){
		Map<String,Object>  limitMapr=this.orgMapper.selectAccountNumber(parm);
		if(limitMapr.get("rootId")==null)
			limitMapr.put("rootId",limitMapr.get("id"));
		Integer nowLimit=this.userMapper.orgUserCount(limitMapr);
		Integer limit=Integer.parseInt(limitMapr.get("accountNumber")+"");
		if((nowLimit+1)>limit){
			return false;
		}
		return true;

	}

}
