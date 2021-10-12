package com.unicom.service.impl;


import com.unicom.common.CacheMap;
import com.unicom.mapper.DictMapper;
import com.unicom.utils.ResponseUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("dictServiceImpl")
@Setter
@Getter
public class DictServiceImpl extends BaseServiceImpl {
	@Autowired
	private DictMapper baseMapper;

	@Override
	public Map<String, Object> insert(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		this.getBaseMapper().insert(parm);
		CacheMap.dictCache.put(Integer.parseInt((String) parm.get("code")), (String) parm.get("info"));
		return ResponseUtils.responseSuccessData(parm.get("id"));
	}


	@Override
	public Map<String, Object> delete(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(parm.get("id") + "")) {
			return ResponseUtils.responseErrorNoRole("id不能为空！");
		}
		this.getBaseMapper().delete(parm);
		CacheMap.dictCache.remove(Integer.parseInt((String) parm.get("code")));
		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> update(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(parm.get("id") + "")) {
			return ResponseUtils.responseErrorNoRole("id不能为空！");
		}
		this.getBaseMapper().update(parm);
		CacheMap.dictCache.put((Integer) parm.get("code"), (String) parm.get("info"));
		return ResponseUtils.responseSuccess();
	}

}
