package com.unicom.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicom.common.IConstants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ResponseUtils {

	/**
	 * 使用response输出JSON
	 *
	 * @param response
	 * @param result
	 */
	public static void outJson(ServletResponse response, Object result) {
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			out = response.getWriter();
			out.println(new ObjectMapper().writeValueAsString(result));
		} catch (Exception e) {
			log.error(e + "输出JSON出错");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}


	public static Map<String, Object> response(int resultIntSuccess, Object data, String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", resultIntSuccess);
		result.put("code", resultIntSuccess);
		if (data != null)
			result.put("data", data);
		if (message != null)
			result.put("message", message);
		return result;
	}

	public static Map<String, Object> responseSuccessData(Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_SUCCESS);
		result.put("code", IConstants.RESULT_INT_SUCCESS);
		result.put("message", "success");
		if (data != null)
			result.put("data", data);


		return result;
	}


	public static Map<String, Object> responseSuccessData(Object data, int count) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_SUCCESS);
		result.put("code", IConstants.RESULT_INT_SUCCESS);
		result.put("message", "success");
		if (data != null) {
			result.put("data", data);
			result.put("count", count);
		}

		return result;
	}


	public static Map<String, Object> responseSuccessData(Object data, int count, int page) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_SUCCESS);
		result.put("code", IConstants.RESULT_INT_SUCCESS);
		result.put("message", "success");
		if (data != null) {
			result.put("data", data);
			result.put("count", count);
			result.put("page", page);
		}


		return result;
	}


	public static Map<String, Object> responseSuccessData(Object data, int count, String key) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_SUCCESS);
		result.put("code", IConstants.RESULT_INT_SUCCESS);
		result.put("queryId", key);
		result.put("message", "success");
		if (data != null) {
			result.put("data", data);
			result.put("count", count);
		}


		return result;
	}


	public static Map<String, Object> responseSuccess() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_SUCCESS);
		result.put("code", IConstants.RESULT_INT_SUCCESS);

		result.put("message", "success");
		return result;
	}

	public static Map<String, Object> responseError(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_ERROR);
		result.put("code", IConstants.RESULT_INT_ERROR);

		if (message != null)
			result.put("message", message);
		return result;
	}

	public static Map<String, Object> responseErrorNoUser(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_ERROR_NOUSER);
		result.put("code", IConstants.RESULT_INT_ERROR_NOUSER);
		if (message != null)
			result.put("message", message);
		return result;
	}

	public static Map<String, Object> responseErrorNoCoder(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_ERROR_CODE);
		result.put("code", IConstants.RESULT_INT_ERROR);
		if (message != null)
			result.put("message", message);
		return result;
	}


	public static Map<String, Object> responseErrorNoRole(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", IConstants.RESULT_INT_ERROR_NOROLE);
		result.put("code", IConstants.RESULT_INT_ERROR);
		if (message != null)
			result.put("message", message);
		return result;
	}

}
