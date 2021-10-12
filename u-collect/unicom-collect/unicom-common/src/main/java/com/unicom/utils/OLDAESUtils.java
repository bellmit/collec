package com.unicom.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
@Slf4j
public class OLDAESUtils {
    public static String aesEncrypt(String org, String secret) {
        try {
            byte[] secretBytes = Arrays.copyOf(secret.getBytes("ASCII"), 16);
            SecretKey secretKey = new SecretKeySpec(secretBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] orgBytes = org.getBytes("UTF-8");
            byte[] encryptedBytes = cipher.doFinal(orgBytes);
            char[] encodedHexChars = Hex.encodeHex(encryptedBytes);
            String encrypted = new String(encodedHexChars).toUpperCase();
            return encrypted;
        } catch (Exception e) {
            log.error("encode error", e);
        }
        return null;
    }

    public static String aesDecrypt(String encrypted, String secret) {
        try {
            byte[] secretBytes = Arrays.copyOf(secret.getBytes("ASCII"), 16);
            SecretKey secretKey = new SecretKeySpec(secretBytes, "AES");
            Cipher decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, secretKey);
            char[] encryptedChars = encrypted.toCharArray();
            byte[] decodedHexBytes = Hex.decodeHex(encryptedChars);
            byte[] decryptedBytes = decipher.doFinal(decodedHexBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            log.error("decode error", e);
        }
        return null;
    }

}
