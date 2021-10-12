package com.unicom.roleRightShiro.controller;

import com.unicom.controller.DicTypeController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dictType")
public class DictTypeShrioController extends DicTypeController {
	@Override
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return super.list(parm);
	}

	@Override
	public Map<String, Object> getOne(@RequestParam Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return super.getOne(parm);
	}

	@RequiresPermissions(value = {"dictType:add"})
	@Override
	public Map<String, Object> add(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return super.add(parm);
	}

	@RequiresPermissions(value = {"dictType:update"})
	@Override
	public Map<String, Object> update(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return super.update(parm);
	}

	@RequiresPermissions(value = {"dictType:delete"})
	@Override
	public Map<String, Object> delete(@RequestBody Map<String, Object> parm) {
		// TODO Auto-generated method stub
		return super.delete(parm);
	}
}
