package com.unicom.sms;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

//@FeignClient(url = "http://47.114.90.114:8906/dxpt-api/", name = "WJXSMS")
public interface SMSClient {
    Cache<String, String> SMSCache = CacheBuilder.newBuilder().expireAfterWrite(115, TimeUnit.MINUTES).build();

    Cache<String, String> SMSCodeCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();

    @ResponseBody
    @RequestMapping(value = "authorization", method = RequestMethod.POST)
    Map<String, Object> auth(@RequestBody Map<String, String> body);

    @RequestMapping(value = "sms/addPlan", method = RequestMethod.POST)
    Map<String, Object> sendSMS(@RequestHeader("access_token") String token, @RequestHeader("expireTime") String expireTime, @RequestBody Map<String, Object> body);

}
