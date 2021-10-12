package com.unicom.service.impl;

import com.unicom.mapper.DicTypeMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Setter
@Getter
public class DicTypeServiceImpl extends BaseServiceImpl {
	@Autowired
	private DicTypeMapper baseMapper;
}
