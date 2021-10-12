package com.unicom.controller;

import com.unicom.service.BaseService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;


@RequestMapping("/dictType")
@Setter
@Getter
public class DicTypeController extends BaseController {
	@Resource(name = "dicTypeServiceImpl")
	private BaseService baseService;

}
