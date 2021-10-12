package com.unicom.roleRightShiro.service.impl;

import com.unicom.roleRightShiro.mapper.RoleMapper;
import com.unicom.roleRightShiro.service.RoleService;
import com.unicom.utils.PageUtils;
import com.unicom.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月4日 下午3:45:34 类说明:
 */
@Transactional
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 返回角色列表
	 *
	 * @param parm
	 * @return
	 */
	public Map<String, Object> list(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		int count = this.roleMapper.count(parm);
		PageUtils.page(parm, count);
		Collection<Map<String, Object>> coll = this.roleMapper.selectAll(parm);

		return ResponseUtils.responseSuccessData(coll, count);
	}

	@Override
	public Map<String, Object> listAllPermission() {
		// TODO Auto-generated method stub
		Collection<Map<String, Object>> coll = this.roleMapper.listAllPermission();
		Map<Object, List<Map<String, Object>>> o = coll.parallelStream().collect(Collectors.groupingBy(e -> {
			Map<String, Object> map = e;
			return map.get("menuName");
		}));

		Collection<Object> rcol = new ArrayList<Object>();
		for (Object key : o.keySet()) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("menuName", key);
			m.put("permissions", o.get(key));
			rcol.add(m);
		}

		return ResponseUtils.responseSuccessData(rcol);
	}


	@Override
	public Map<String, Object> update(Map<String, Object> parm) {
		// TODO Auto-generated method stub

		this.roleMapper.update(parm);
		this.roleMapper.deleteAllPer(parm);
		this.roleMapper.insertPer(parm);
		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> insert(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		this.roleMapper.insert(parm);

		this.roleMapper.insertPer(parm);
		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> delete(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		this.roleMapper.deleteRole(parm);
		return ResponseUtils.responseSuccess();
	}


}
