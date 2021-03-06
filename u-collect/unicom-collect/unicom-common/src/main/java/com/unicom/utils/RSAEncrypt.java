package com.unicom.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(value = "classpath:key.properties")
@Slf4j
public class RSAEncrypt {
	public static String privateKey;
	public static String publicKey;

//	public static void main(String[] args) {
//		privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOfzN1WOy8QlPBRZ/nbfHnq5iTiwG1yOcKq0I9QdDv8zrnDdAJDjKp+bPxbMVCp+9haLa0Y5tSIyxPLnXs08C3JtPzuDMLsIUjGS2OFPtTfq51xnBy/jfqC28e6j3gQcKfB/wCbeDR97kJ8f6iG2bFLGdaYs+or6NwyzYF2os9yhAgMBAAECgYAb0JO7I9OVou1wd1ZHhPwPtX4aGSw+zin+nnmJby6kNdteV3JA7azn1OMc5cXWDsGN9IwZsMPi5PmvM0WwENpCVRAH7Ep5AY1XYEt0Et8IAg55ry/b3jbzKqmOOp2E8ZsX6RcAn4/p8Tghb+YADj3t5Pdcvc3TT3CfldXEfs26QQJBAP2dypY2JJMs8JGt0/VQW1dWhw6QS2Fl8lxU6l6TO0LE2/4Gz8QzA6boAqFOw+PZnSr/jRl3hgBFCy9lhHSP0KkCQQDqIUuFJvoA2DftoRa5T8HtjWOTPjY7+GNALBXz7iAkzX7z9tl6DvXHoGnwRr2MzMCF4453yf6l+K7LgEs6II85AkAiB/xZL6a0wQD+605XypKXI5s3zxDuXtmonhRc4HDBQt0qNwvGDfpdeSBxOLp8ZoT6chQi6LDodFtN3tdpNKsRAkBNEqTPsmL+QC85FJxsaGsIjlDHOeWUnFHf4CNh53PBabxvB0XUPnR+QLpfFIp1fTmrWZuep2oirNituRAKlIlRAkEA/H3lhKWpRXWSxNEmZv5NXhw2bDVYNRpHHPm/ECTDKcupwNBVEAGhfEyCH/JTtCvXEAhQyTfg+ZSBWU3jkgbUjg==";
//		publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDn8zdVjsvEJTwUWf523x56uYk4sBtcjnCqtCPUHQ7/M65w3QCQ4yqfmz8WzFQqfvYWi2tGObUiMsTy517NPAtybT87gzC7CFIxktjhT7U36udcZwcv436gtvHuo94EHCnwf8Am3g0fe5CfH+ohtmxSxnWmLPqK+jcMs2BdqLPcoQIDAQAB";
//			
//		String s = "haha123";
//		Map<Integer, String> map = RSAEncrypt.genKeyPair();
//		System.out.println(map.get(0)); // ??????
//
//		System.out.println(map.get(1)); // ??????
//		String s2 = RSAEncrypt.encrypt(s, publicKey);
//		System.out.println("??????????????????" + s2);
//		System.out.println("??????????????????" + RSAEncrypt.decrypt(s2, privateKey));
//
//		String s3 = RSAEncrypt.encrypt(s, privateKey);
//		System.out.println("??????????????????" + s3);
//		System.out.println("??????????????????" + RSAEncrypt.decrypt(s3, publicKey));
//
//		
//		String ae=AESUtils.generateKey();
//	
////		String s1=RSAEncrypt.encrypt("ccc",
////				"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALv44ruI/0e6aXn1MiR/cffUJWuOB82F9s6kHVdjn6zr1LP4TSXjcebmFiu0MUMyUCAUqUtlXQlPA8gk3DakIt9cdjAYSp2dchuC6lfl+Sch82K4itUHrQu53kj6BBstn7IyKWNE9sgIIeEWRUj6g55GZ+RZEFtUruxj1zlCuq11AgMBAAECgYBCJiDsLBbiEBjEQWRm8a6Sr0aH5hCa4/Tp3VkyAq4TdV6CAvRrS/IudnQA50Waxx7pWzwEe2RVPUgwa8BPPk3Cvvu1/jYvBPvp4+zYcBUnulVMjXeatKHmi4xUDz82ysoktFgLiUGykeiodk52LkP1TMwAy/7E+qs4Fey5cKQ6FQJBAPthRTBL3ygfcu9eJXKVUrtNv5OTSUiJYR+G0MA+tDWqKk7p0RnoCDETbminSmuYN7fPmbqg+qrNOjJcMzfIyRcCQQC/bUkDXaWMR0sggoZg/Zm7XLYbGOkg6aqypBvM6Po14DPBUXzdJ66VeZTSgN+i+cqD+AL1BSaM0c21aunVYT1TAkBzrE3XLIKShu4vgYX/1QCN2ff244aMT1MW28WBQRlRvGzdhTBIGIJ0ermKhLh1DEVPWsMlot/V6rwF4nFbERSfAkBjISLYPVyHYjwDX9Ra0bkUj5Q2QBYp1xdwXw+Oc29vCuWCTMKOfAgqm8V3pQkqipRW9s4BOGOYNOWD9xOUTkDFAkEAx8XieW522ZUT6I40Z8ca+rHOOjavYUCy2auKQjpqaPAtt9pG2dJ3hwD/JKcMOawmuDK08RiFL86XfNEN+zS1Nw==");
////		System.out.println(s1);
//	}

	/**
	 * ?????????????????????
	 */
	public static Map<Integer, String> genKeyPair() {

		Map<Integer, String> keyMap = new HashMap<Integer, String>(); // ??????????????????????????????????????????

		try {
			// KeyPairGenerator??????????????????????????????????????????RSA??????????????????
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

			// ?????????????????????????????????????????????96-1024???
			keyPairGen.initialize(1024, new SecureRandom());

			// ?????????????????????????????????keyPair???
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // ????????????
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // ????????????

			// ?????????????????????
			String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
			// ?????????????????????
			String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));

			// ???????????????????????????Map
			keyMap.put(0, publicKeyString); // 0????????????
			keyMap.put(1, privateKeyString); // 1????????????
		} catch (Exception e) {
			log.info("???????????????????????????" + e.getMessage());
			return null;
		}

		return keyMap;
	}

	/**
	 * RSA????????????
	 *
	 * @param str       ????????????????????????
	 * @param publicKey ??????
	 * @return ????????????????????????
	 */
	public static String encrypt(String str, String publicKey) {
		String outStr = null;
		try {
			// base64???????????????
			byte[] decoded = Base64.decodeBase64(publicKey);
			RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
					.generatePublic(new X509EncodedKeySpec(decoded));
			// RSA??????
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			log.info("?????????????????????" + str + "????????????" + e.getMessage());
		}
		return outStr;
	}

	/**
	 * RSA????????????
	 *
	 * @param str        ???????????????
	 * @param privateKey ??????
	 * @return ????????????????????????
	 */
	public static String decrypt(String str, String privateKey) {
		String outStr = null;
		try {
			// 64??????????????????????????????
			byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
			// base64???????????????
			byte[] decoded = Base64.decodeBase64(privateKey);
			RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
					.generatePrivate(new PKCS8EncodedKeySpec(decoded));
			// RSA??????
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			outStr = new String(cipher.doFinal(inputByte));
		} catch (Exception e) {
			log.info("???????????????????????????" + e.getMessage());
		}
		return outStr;

	}

	@Value("${unicom.privateKey}")
	public void setPrivateKey(String privateKey) {
		RSAEncrypt.privateKey = privateKey;
	}

	@Value("${unicom.publicKey}")
	public void setPublicKey(String publicKey) {
		RSAEncrypt.publicKey = publicKey;
	}

	public static void main(String[] args) {
		//String s=RSAEncrypt.encrypt("431102198602252017","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFIdVBosEwErIO2ckaqHlFZK/SwYOC0Ib6fLcuWOtuwcwiWMDj0X3hVvTPuhfm8W7LAzMYkvlIxCLSKk+FNc866DOXZf8JBaCYA3YvxevEQiWgjjO2Sfn8zFTlrGb4PGTrRfHOKcKiiOnN7vIiDdB7mCSiSwyjY6mC4BPa3Sq0jwIDAQAB");
		System.out.println(RSAEncrypt.genKeyPair().get(0));
		//System.out.println(AESUtils.decrypt("ed7af7b487f1a55a3e567e31092a23fe","test"));

	}


}
