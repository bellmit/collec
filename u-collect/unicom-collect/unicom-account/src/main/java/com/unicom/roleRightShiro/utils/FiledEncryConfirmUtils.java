package com.unicom.roleRightShiro.utils;

import com.unicom.roleRightShiro.config.FiledEncryConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月5日 下午5:14:27
 * 类说明:
 */
//@Component
//@Slf4j
public class FiledEncryConfirmUtils {
    @Autowired
    private FiledEncryConfig filedEncryConfig;

    private static final Map<String, String> customerMap = new HashMap<String, String>();
    private static final Map<String, String> publicMap = new HashMap<String, String>();

//	@PostConstruct
//	public void initMethod() {
//		log.info("rule..utils..init......");
//		String customerKey = filedEncryConfig.getCustomerKeyFiled();
//		String publicKey = filedEncryConfig.getPublicKeyFiled();
//		if (customerKey != null) {
//			String[] customerKeys = customerKey.split(",");
//			for (String cusName : customerKeys) {
//				customerMap.put(cusName, "1");
//			}
//		}
//		log.info("customer key rule is:"+customerMap.size());
//		if(publicKey!=null) {
//		String[] publicKeys = publicKey.split(",");
//			for (String pubName : publicKeys) {
//				publicMap.put(pubName, "1");
//			}
//		}
//		log.info("public key rule is:"+publicMap.size());
//	}
//	
//
//	public static boolean isCustomerFiled(String name) {
//		if(customerMap.get(name)!=null)
//			return true;
//		return false;
//	}
//
//	public static  boolean isPublicFiled(String name) {
//		if(publicMap.get(name)!=null)
//			return true;
//		return false;
//	}

}
