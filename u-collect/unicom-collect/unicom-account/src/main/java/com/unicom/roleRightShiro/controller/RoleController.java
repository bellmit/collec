package com.unicom.roleRightShiro.controller;

import com.unicom.common.IConstants;
import com.unicom.roleRightShiro.service.RoleService;
import com.unicom.utils.ResponseUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {
	@Autowired
	private RoleService roleService;

	@RequiresPermissions(value = {"role:list"})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		parm.put("roleLevel", user.get("roleLevel"));
		return roleService.list(parm);
	}

	@RequiresPermissions(value = {"role:update", "role:add"}, logical = Logical.OR)
	@RequestMapping(value = "/listAllPermission", method = RequestMethod.GET)
	public Map<String, Object> listAllPermission(@RequestParam Map<String, Object> parm) {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		parm.put("roleLevel", user.get("roleLevel"));
		return roleService.listAllPermission();
	}


	@RequiresPermissions(value = {"role:update"})
	@RequestMapping(value = "/updateRole", method = RequestMethod.POST)
	public Map<String, Object> updateRole(@RequestBody Map<String, Object> parm) {
		if (!validateParm(parm)) {
			return ResponseUtils.responseError("非法的修改,不能将角色修改为权限等级比自己更高的角色");
		}
		return this.roleService.update(parm);
	}

	@RequiresPermissions(value = {"role:add"})
	@RequestMapping(value = "/addRole", method = RequestMethod.POST)
	public Map<String, Object> addRole(@RequestBody Map<String, Object> parm) {
		if (!validateParm(parm)) {
			return ResponseUtils.responseError("非法的修改,不能增加比自己权限等级更高的角色");
		}
		return this.roleService.insert(parm);
	}

	@RequiresPermissions(value = {"role:delete"})
	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	public Map<String, Object> deleteRole(@RequestBody Map<String, Object> parm) {
		return this.roleService.delete(parm);
	}

	private boolean validateParm(Map<String, Object> parm) {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		Integer leve = Integer.parseInt(parm.get("level") + "");
		Integer rLev = (Integer) user.get("roleLevel");
		if (leve < rLev) {
			return false;
		}

		parm.put("roleLevel", user.get("roleLevel"));


		return true;
	}


}
