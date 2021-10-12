package com.unicom.controller;

import com.unicom.service.BaseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

@Data
@Slf4j
public class BaseController {
	@Resource(name = "baseServiceImpl")
	private BaseService baseService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestParam Map<String, Object> parm) {

		return this.getBaseService().select(parm);
	}


	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Map<String, Object> getOne(@RequestParam Map<String, Object> parm) {

		return this.getBaseService().selectOne(parm);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> add(@RequestBody Map<String, Object> parm) {

		return this.getBaseService().insert(parm);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@RequestBody Map<String, Object> parm) {

		return this.getBaseService().update(parm);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(@RequestBody Map<String, Object> parm) {

		return this.getBaseService().delete(parm);
	}


}
