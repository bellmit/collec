package com.unicom.controller;


import com.unicom.service.BaseService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Setter
@Getter
public class DictController extends BaseController {
	@Resource(name = "dictServiceImpl")
	private BaseService baseService;
}
