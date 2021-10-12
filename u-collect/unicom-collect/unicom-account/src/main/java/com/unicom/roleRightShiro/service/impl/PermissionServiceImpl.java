package com.unicom.roleRightShiro.service.impl;

import com.unicom.roleRightShiro.mapper.PermissionMapper;
import com.unicom.service.impl.BaseServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("permissionServiceImpl")
@Setter
@Getter
public class PermissionServiceImpl extends BaseServiceImpl {
	@Autowired
	private PermissionMapper baseMapper;
}
