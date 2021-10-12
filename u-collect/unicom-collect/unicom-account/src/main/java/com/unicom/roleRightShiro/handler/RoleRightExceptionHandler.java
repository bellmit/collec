package com.unicom.roleRightShiro.handler;

import com.unicom.handler.BaseExceptionHandler;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class RoleRightExceptionHandler extends BaseExceptionHandler {
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	@Override
	public Map<String, Object> exceptionHandler(Exception e) {
		// TODO Auto-generated method stub
		e.printStackTrace();
		log.error("发生异常：" + e.getMessage());

		if (e instanceof UnauthorizedException) {
			return ResponseUtils.responseErrorNoRole("权限不足， 不能访问!");
		}

		if (e instanceof AuthenticationException) {

			if (e instanceof IncorrectCredentialsException) {
				return ResponseUtils.responseErrorNoUser("登录验证失败！");
			}

			if (e instanceof UnknownAccountException) {
				return ResponseUtils.responseErrorNoUser("登录验证失败");
			}

			if(e instanceof ExpiredCredentialsException){
				return ResponseUtils.responseErrorNoUser(e.getMessage());
			}


			return ResponseUtils.responseErrorNoUser("权限验证失败,请检查是否拥有该权限！");
		}
		return super.exceptionHandler(e);
	}


}
