package com.unicom.handler;

import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Map<String, Object> exceptionHandler(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
			if (e.getMessage() != null && e.getMessage().contains("cannot be null")) {
				return ResponseUtils.responseError("必填字段不能为空！");
			} else if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
				return ResponseUtils.responseError("该数据可能存在关联数据，请处理后再进行该操作！");
			}
		}

		return ResponseUtils.responseError("服务器内部异常");
	}
}
