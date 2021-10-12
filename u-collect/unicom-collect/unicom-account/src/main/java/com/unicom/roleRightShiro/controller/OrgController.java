package com.unicom.roleRightShiro.controller;

import com.unicom.roleRightShiro.service.OrgService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月18日 下午2:21:50
 * 类说明:
 */
@RestController
@RequestMapping("/org")
public class OrgController {
	@Autowired
	private OrgService orgService;

	@RequiresPermissions(value = {"org:list"})
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		return orgService.list();
	}

	@RequiresPermissions(value = {"org:list"})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> listGet(@RequestParam Map<String, Object> parm) {
		return this.list(parm);
	}

	/**
	 * 查看是否能够删除
	 *
	 * @param parm
	 * @return
	 */
	@RequiresPermissions(value = {"org:delete"})
	@RequestMapping(value = "/haveUser", method = RequestMethod.POST)
	public Map<String, Object> isHaveUser(@RequestBody Map<String, Object> parm) {

		return orgService.haveUser(parm);
	}

	/**
	 * 全部提交
	 *
	 * @param parm
	 * @return
	 */
	@RequiresPermissions(value = {"org:delete", "org:update", "org:add"}, logical = Logical.OR)
	@RequestMapping(value = "/submitOrgAll", method = RequestMethod.POST)
	public Map<String, Object> submitOrgAll(@RequestBody Map<String, Object> parm) {
		Subject subject = SecurityUtils.getSubject();

		if (!subject.isPermitted("org:add"))
			parm.put("addData", new ArrayList<Object>());
		if (!subject.isPermitted("org:update"))
			parm.put("updateData", new ArrayList<Object>());
		if (!subject.isPermitted("org:delete"))
			parm.put("delData", new ArrayList<Object>());
		return orgService.doSubmit(parm);
	}

	/**
	 * 新增
	 *
	 * @param parm
	 * @return
	 */
	@RequiresPermissions(value = {"org:add"})
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> add(@RequestBody Map<String, Object> parm) {

		return orgService.add(parm);
	}

	/**
	 * 更新
	 *
	 * @param parm
	 * @return
	 */
	@RequiresPermissions(value = {"org:update"})
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@RequestBody Map<String, Object> parm) {

		return orgService.update(parm);
	}

	/**
	 * 删除
	 *
	 * @param parm
	 * @return
	 */
	@RequiresPermissions(value = {"org:delete"})
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@RequestBody Map<String, Object> parm) {

		return orgService.delete(parm);
	}




}
