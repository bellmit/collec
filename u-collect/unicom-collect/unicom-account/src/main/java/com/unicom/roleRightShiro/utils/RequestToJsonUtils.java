package com.unicom.roleRightShiro.utils;

import com.alibaba.fastjson.JSONObject;
import com.unicom.roleRightShiro.common.RequestWapper;

import java.io.IOException;

/**
 * @author yangpeng
 * @version 创建时间：2020年8月12日 下午3:57:20 类说明:
 */
public class RequestToJsonUtils {
    public static JSONObject getRequestJsonObject(RequestWapper request) throws IOException {
        String json = getRequestJsonString(request);
        return JSONObject.parseObject(json);
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(RequestWapper request) throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if (submitMehtod.equals("GET")) {
            //return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
            return "";
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /*
     * @param request
     *
     * @return
     *
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(RequestWapper request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }

        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     *
     * <pre>
     * 举例
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(RequestWapper request) throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }
}
