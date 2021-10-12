package com.unicom.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("status", 0);
        put("code", 0);
    }

    public static R error() {

        return error(-1, 500, "未知异常，请联系管理员");
    }

    public static R error(String message) {
        return error(-1, 500, message);
    }

    public static R error(int status, int code, String message) {
        R r = new R();
        r.put("status", status);
        r.put("code", code);
        r.put("message", message);
        return r;
    }

    public static R ok(String message) {
        R r = new R();
        r.put("message", message);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        R r = new R();
        r.put("message", "success");
        return r;
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
