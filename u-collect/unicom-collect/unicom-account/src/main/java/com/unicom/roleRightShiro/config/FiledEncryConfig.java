package com.unicom.roleRightShiro.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月5日 下午5:15:43
 * 类说明:加密字段配置
 */
//@Configuration
//@PropertySource(value = "classpath:fieldConfig.properties",ignoreResourceNotFound=true)
//@Data
public class FiledEncryConfig {
	@Value("${rule.customerKey:null}")
	private String customerKeyFiled;

	@Value("${rule.publicKey:null}")
	private String publicKeyFiled;

}
