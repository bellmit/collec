package com.unicom.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月13日 下午3:56:08
 * 类说明:
 */
public class AESUtils {
//	public static void main(String[] args) throws UnsupportedEncodingException, DecoderException {
//		String key = "ed7af7b487f1a55a3e567e31092a23fe";
//		System.out.println(key);
//		byte[] rand=new byte[16];//生成有8个元素的字节数组
//		Random r=new Random();
//		r.nextBytes(rand);
//		System.out.println(Hex.encodeHex(rand));
//		String iv="a1b9394c563604901a13f2e75c3a4327";
//		String data="b3hhcyU15ngFg/Fcrz3psw==";
//		//String data="KZXWSYFqv8zEVoqQ+f8jzQ==";
//		
//		System.out.println(AESUtils.decode(key, data, iv));
//		
//     
//    }


    /**
     * 生成key
     *
     * @return
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            return Hex.encodeHexString(byteKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES加密
     *
     * @param thisKey
     * @param data
     * @return
     */
    public static String encode(String thisKey, String data) {
        try {
            // 转换KEY
            Key key = new SecretKeySpec(Hex.decodeHex(thisKey), "AES");
            //System.out.println(thisKey);

            // 加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(data.getBytes());
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES加密
     *
     * @param thisKey
     * @param data
     * @return
     */
    public static String encode(String thisKey, String data, String iv) {
        try {


            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] keyBytes = Hex.decodeHex(thisKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            IvParameterSpec i = new IvParameterSpec(Hex.decodeHex(iv));

            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), i);
            byte[] decoded = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encodeBase64(decoded));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String decode(String thisKey, String data) {
        try {
            byte[] sourceBytes = Base64.decodeBase64(data);

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            Key key = new SecretKeySpec(Hex.decodeHex(thisKey), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(data.getBytes());
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES解密
     *
     * @param thisKey
     * @param data
     * @return
     */
    public static String decode(String thisKey, String data, String iv) {
        try {
            byte[] sourceBytes = Base64.decodeBase64(data);

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] keyBytes = Hex.decodeHex(thisKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            IvParameterSpec i = new IvParameterSpec(Hex.decodeHex(iv));

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), i);
            byte[] decoded = cipher.doFinal(sourceBytes);
            return new String(decoded, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] args) {
        String iv = "a1b9394c563604901a13f2e75c3a4327";
        String key = "f2c998bf7459a56de722c4060b71f9e1";
        try {
            System.out.println(AESUtils.encode(key, "18608404586", iv));
            System.out.println(AESUtils.decode(key, "Zla+e2kzG9krtHbKcYgmeYSMEKk6eyma9I3hjkWFaWE=", iv));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
