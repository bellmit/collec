package com.unicom.roleRightShiro.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.unicom.common.util.JsonUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;

/**
 * @description: 签名工具类
 * @author: yangpeng
 * @create: 2021-10-09 14:53
 **/
@UtilityClass
@Slf4j
public class SignUtils {

    private final static String SIGN_KEY_NAME = "sign";

    /**
     * 验签
     *
     * @param params
     * @param secret
     * @return
     */
    public boolean verifySign(SortedMap<String, Object> params, String secret) {
        String urlSign = MapUtil.getStr(params, "sign").trim();
        if (StringUtils.isBlank(urlSign)) {
            return false;
        }
        String paramsSign = getParamsSign(params, secret);
        return StringUtils.isNotBlank(paramsSign) && urlSign.equals(paramsSign);
    }


    /**
     * 根据参数获取sign 签名算法
     * 1. SortedMap 参数排序
     * 2. 参数转Json拼接秘钥
     * 3. 转MD5 转小写
     *
     * @param params
     * @return
     */
    public String getParamsSign(SortedMap<String, Object> params, String secret) {
        params.remove(SIGN_KEY_NAME);
        String paramsJson = JsonUtils.mapToJson(params);
        StringBuffer sb = new StringBuffer(secret).append(paramsJson);
        System.out.println(sb.toString());
        return DigestUtil.md5Hex(sb.toString()).toLowerCase();
    }

    public static void main(String[] args) {
        String str = "916lWh2WMcbSWiHv{\"account\":\"250543222@qq.com\",\"email\":\"\",\"password\":\"12345678\",\"slideCode\":\"4qjhWc84KROSbxGF7yyVG21G1EHDp/PRA5RcUKIc/9oHzUPfJRie3Mt27WjI+eTMTjgzzGfdc4dA1gv9g8HQy7TshvmMbQt0w0H+8CVEfbg=\",\"timestamp\":\"1607938108778\"}\n";
        str="4jBw1n05RmfK2dtbY9ma03n9kB92647z{\"appId\":\"0HdY2ESXnE\",\"idNumbers\":[\"430821195312234825\"],\"nonce\":\"23232\",\"timestamp\":1636940033209}";
        System.out.println(DigestUtil.md5Hex(str).toLowerCase());
    }


}
