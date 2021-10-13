package com.unicom.roleRightShiro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataKeyConfig {

    private static String AESKEY;
    private static String AESIV;

    @Value("${health.data.aesKey:f2c998bf7459a56de722c4060b71f9e1}")
    public void setAESKEY(String aesKey) {
        AESKEY = aesKey;
    }

    @Value("${health.data.aesIv:a1b9394c563604901a13f2e75c3a4327}")
    public void setAESIV(String aesIv) {
        AESIV = aesIv;
    }

    public static String getAESKEY() {
        return AESKEY;
    }

    public static String getAESIV() {
        return AESIV;
    }

}
