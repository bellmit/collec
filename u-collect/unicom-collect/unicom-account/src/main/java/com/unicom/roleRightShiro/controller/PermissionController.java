package com.unicom.roleRightShiro.controller;

import com.unicom.common.IConstants;
import com.unicom.controller.BaseController;
import com.unicom.service.BaseService;
import com.unicom.utils.ResponseUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/permission")
@Setter
@Getter
public class PermissionController extends BaseController {
	@Resource(name = "permissionServiceImpl")
	private BaseService baseService;

	@Override
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (!this.validateAdmin()) {
			return ResponseUtils.responseError("没有访问权限！");
		}
		return super.list(parm);
	}

	@Override
	public Map<String, Object> add(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (!this.validateAdmin()) {
			return ResponseUtils.responseError("没有访问权限！");
		}
		return super.add(parm);
	}

	@Override
	public Map<String, Object> update(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (!this.validateAdmin()) {
			return ResponseUtils.responseError("没有访问权限！");
		}
		return super.update(parm);
	}

	@Override
	public Map<String, Object> delete(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (!this.validateAdmin()) {
			return ResponseUtils.responseError("没有访问权限！");
		}
		return super.delete(parm);
	}

	private boolean validateAdmin() {
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(IConstants.SESSION_USER_INFO);
		return user.get("username") != null && "admin".equals(user.get("username"));
	}

}
