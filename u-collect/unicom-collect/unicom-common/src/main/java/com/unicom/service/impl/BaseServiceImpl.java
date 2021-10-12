package com.unicom.service.impl;

import com.unicom.service.BaseService;
import com.unicom.utils.BaseMapper;
import com.unicom.utils.ResponseUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Getter
public class BaseServiceImpl implements BaseService {

	private BaseMapper baseMapper;

	@Override
	public Map<String, Object> insert(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		this.getBaseMapper().insert(parm);
		return ResponseUtils.responseSuccessData(parm.get("id"));
	}


	@Override
	public Map<String, Object> delete(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(parm.get("id") + "")) {
			return ResponseUtils.responseErrorNoRole("id不能为空！");
		}
		this.getBaseMapper().delete(parm);
		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> update(Map<String, Object> parm) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(parm.get("id") + "")) {
			return ResponseUtils.responseErrorNoRole("id不能为空！");
		}
		this.getBaseMapper().update(parm);
		return ResponseUtils.responseSuccess();
	}

	@Override
	public Map<String, Object> select(Map<String, Object> parm) {
		// TODO Auto-generated method stub

		return ResponseUtils.responseSuccessData(this.getBaseMapper().select(parm));
	}


	@Override
	public Map<String, Object> selectOne(Map<String, Object> parm) {
		// TODO Auto-generated method stub

		return ResponseUtils.responseSuccessData(this.getBaseMapper().selectOne(parm));
	}

}
