package com.unicom.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 杨鹏
 */
public class AESAndMD5Crypt {

    /**
     * 以UTF8编码md5加密
     *
     * @param inputText
     * @return
     */
    public static String md5(String inputText) {
        return md5(inputText, StandardCharsets.UTF_8);
    }

    /**
     * md5加密
     *
     * @param inputText
     * @param charset
     * @return
     */
    public static String md5(String inputText, Charset charset) {
        return md5Encrypt(inputText, "md5", charset);
    }

    /**
     * md5或者sha-1加密
     *
     * @param inputText     要加密的内容
     * @param algorithmName 加密算法名称：md5或者sha-1，不区分大小写
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String md5Encrypt(String inputText, String algorithmName, Charset charset) {
        try {
            if (inputText == null || "".equals(inputText.trim())) {
                return "";
            }
            if (algorithmName == null || "".equals(algorithmName.trim())) {
                algorithmName = "md5";
            }
            MessageDigest m = MessageDigest.getInstance(algorithmName);
            m.update(inputText.getBytes(charset));
            return Byte2HexStr(m.digest());
        } catch (NoSuchAlgorithmException e) {
        }
        return inputText;
    }

    /**
     * 以UTF8编码带偏移量的AES加密
     *
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptData(String content, String key) throws Exception {
        return encryptData(content, key, StandardCharsets.UTF_8);
    }

    /**
     * 带偏移量的AES加密
     *
     * @param content
     * @param key
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encryptData(String content, String key, Charset charset) throws Exception {
        String IV = key;
        if (key.length() > 16) {
            // IV为商户MD5密钥后16位
            IV = key.substring(key.length() - 16);
            // RES的KEY 为商户MD5密钥的前16位
            key = key.substring(0, 16);
        }
        return encryptData(content, key, IV, charset);
    }

    /**
     * 以UTF8编码带偏移量的AES加密
     *
     * @param data
     * @param key
     * @param IV
     * @return
     * @throws Exception
     */
    public static String encryptData(String data, String key, String IV) throws Exception {
        return encryptData(data, key, IV, StandardCharsets.UTF_8);
    }

    /**
     * 偏移量的AES加密带
     *
     * @param data
     * @param key
     * @param IV
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encryptData(String data, String key, String IV, Charset charset) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] dataBytes = data.getBytes(charset);
            int plaintextLength = dataBytes.length;
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new String(Base64.encodeBase64(encrypted));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw e;
        }

    }

    /**
     * 以UTF8编码带偏移量的AES解密
     *
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptData(String content, String key) throws Exception {
        return decryptData(content, key, StandardCharsets.UTF_8);
    }

    /**
     * 带偏移量的AES解密
     *
     * @param content
     * @param key
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decryptData(String content, String key, Charset charset) throws Exception {
        String IV = key;
        if (key.length() > 16) {
            // IV为商户MD5密钥后16位
            IV = key.substring(key.length() - 16);
            // RES的KEY 为商户MD5密钥的前16位
            key = key.substring(0, 16);
        }
        return decryptData(content, key, IV, charset);
    }

    /**
     * UTF8编码带偏移量的AES解密
     *
     * @param data
     * @param key
     * @param IV
     * @return
     * @throws Exception
     */
    public static String decryptData(String data, String key, String IV) throws Exception {
        return decryptData(data, key, IV, StandardCharsets.UTF_8);
    }

    /**
     * 带偏移量的AES解密
     *
     * @param data
     * @param key
     * @param IV
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decryptData(String data, String key, String IV, Charset charset) throws Exception {
        try {
            byte[] encrypted1 = Base64.decodeBase64(data.getBytes(charset));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, charset);
            return originalString;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 以UTF8编码不带偏移的AES加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        return encrypt(data, key, StandardCharsets.UTF_8);
    }

    /**
     * 不带偏移的AES加密
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String aesKey, Charset charset) throws Exception {
        byte[] result = null;
        boolean initok = false;
        try {
            Cipher aesECB = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(charset), "AES");
            aesECB.init(Cipher.ENCRYPT_MODE, key);// 初始化
            result = aesECB.doFinal(content.getBytes(charset));
            initok = true;
        } catch (InvalidKeyException e) {
        }
        if (!initok) {
            Cipher aesECB = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(md5(aesKey, charset).substring(0, 16).getBytes(charset), "AES");
            aesECB.init(Cipher.ENCRYPT_MODE, key);// 初始化
            result = aesECB.doFinal(content.getBytes(charset));
        }
        return new String(Base64.encodeBase64(result));
    }

    /**
     * 以UTF8编码不带偏移的AES加密16进制
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptHex(String data, String key) throws Exception {
        return encryptHex(data, key, StandardCharsets.UTF_8);
    }

    /**
     * 不带偏移的AES加密16进制
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encryptHex(String content, String aesKey, Charset charset) throws Exception {
        String data = encrypt(content, aesKey, charset);
        byte[] hexStrByte = data.getBytes(charset);
        return Byte2HexStr(hexStrByte);
    }

    /**
     * 以UTF8编码不带偏移的AES解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {
        return decrypt(data, key, StandardCharsets.UTF_8);
    }

    /**
     * 不带偏移的AES解密
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String aesKey, Charset charset) throws Exception {
        String data = "";
        boolean initok = false;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(charset), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = Base64.decodeBase64(content);
            data = new String(cipher.doFinal(result), charset);// 解密
            initok = true;
        } catch (InvalidKeyException e) {
        }
        if (!initok) {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            SecretKeySpec key = new SecretKeySpec(md5(aesKey, charset).substring(0, 16).getBytes(charset), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = Base64.decodeBase64(content);
            data = new String(cipher.doFinal(result), charset);// 解密
        }
        return data;
    }

    /**
     * 以UTF8编码不带偏移的AES解密16进制
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptHex(String data, String key) throws Exception {
        return decryptHex(data, key, StandardCharsets.UTF_8);
    }

    /**
     * 不带偏移的AES解密16进制
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decryptHex(String content, String aesKey, Charset charset) throws Exception {
        byte[] hexStrByte = HexStr2Byte(content);
        return decrypt(new String(hexStrByte, charset), aesKey, charset);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return String
     */
    public static String Byte2HexStr(final byte[] buf) {
        if (null == buf) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte element : buf) {
            String hex = Integer.toHexString(element & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return byte[]
     */
    public static byte[] HexStr2Byte(final String hexStr) {
        if (hexStr.length() < 1 || (hexStr.length() % 2) != 0) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String rrr;
//        String rrr1;
//        try {
//            rrr = AESAndMD5Crypt.encryptHex("123456", "11111111111111");
//            rrr1 = AESAndMD5Crypt.decryptHex(rrr, "11111111111111");
//            System.out.println("md5:" + AESAndMD5Crypt.md5("70394B71687332393374625956704A336A4D5864445234514751326F375237704D6D303749705859373735614E572F70436B456F724D5854624D4B32486A5872646C4F6D635A7544352B42764342644B67654F534436462F77436157505A534D535737356B67674454304D425147636F764632447A326E4F653175507A6B43637542397A4F646F6E77716B70765748332B63694636784B63626F3032746E346A3170346D577A564E2F6C6268573778767A4A5163625A45555A4F4D3877582F51"
//                    + "~1001" + "~B6C37717"));
//
//            System.out.println(AESAndMD5Crypt.encryptHex("{\"partyCode\":\"1001\",\"content\":{\"smsNumber\":\"18608404586\",\"smsTemplateNumber\":\"10\",\"param\":{\"userName\":\"张三丰\",\"userNumber\":\"18608404586\"}}}", "996ffbfef1a54c53"));
//            //System.out.println(AESAndMD5Crypt.decryptData("BE2VqA6fOzrpda5U6AwW9U72Zx1XWb/WZqLBvZ0n/BY8rSMsHupiCX9HX4BYdSac+0uwTgYJGZFzQc3mBvPBig==", "996ffbfef1a54c53"));
//            System.out.println(AESAndMD5Crypt.decryptHex("70394B71687332393374625956704A336A4D5864445234514751326F375237704D6D303749705859373735614E572F70436B456F724D5854624D4B32486A5872646C4F6D635A7544352B42764342644B67654F534436462F77436157505A534D535737356B67674454304D425147636F764632447A326E4F653175507A6B43637542397A4F646F6E77716B70765748332B63694636784B63626F3032746E346A3170346D577A564E2F6C6268573778767A4A5163625A45555A4F4D3877582F51"
//                    + ""
//                    + "", "996ffbfef1a54c53"));
//            System.out.println(AESAndMD5Crypt.decryptHex("595A68475334464C6C36664E72662B6B4852485A31742B7271416559616E755874762F4A444B47377A434B7235686E333042556F46674C6A354E4276644E49675932787231374432386A6A695953336155766A436C496C55357173614E375443676F49412F2B4F786F4A73654F39424D684B6F353963485054764A35486D456A5A2F585A4A64393564334A505A7A4C6635456650726876486A77434642304C6B71394658566762436235345055664147667053514E314A4943716E49486634676959726C337564477931644159546D5234684E6372413D3D", "996ffbfef1a54c53"));
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }

        try {


            String key = "f2c998bf7459a56de722c4060b71f9e1";
            String s = AESAndMD5Crypt.encrypt("110101199003077010", "f2c998bf7459a56de722c4060b71f9e1");
            System.out.println(s);
            System.out.println(AESAndMD5Crypt.decrypt("FlbuNv3dIwk8byY7jXxRB1qeU4gzCjgVqqNk1WDIsZ4=", key));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
