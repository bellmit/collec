package com.unicom.sms;

//import feign.Response;
///import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(url = "http://116.162.55.58:6003/", name = "YQPIC")
public interface OSSClient {
   // @RequestMapping(value = "yiqingpic/{pic}", method = RequestMethod.GET)
   // Response getPic(@RequestHeader("PicSign") String sign, @PathVariable String pic);
}
