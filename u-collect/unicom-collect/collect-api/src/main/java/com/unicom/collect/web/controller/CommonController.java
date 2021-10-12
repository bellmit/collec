package com.unicom.collect.web.controller;

import com.unicom.common.util.AsyncProcessUtils;
import com.unicom.common.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : yangpeng
 * @description : 通用类
 * @create :  2021/9/18 15:32
 **/
@RestController
@RequestMapping("/common/")
public class CommonController {


    /**
     * 获取异步处理进度
     */
    @GetMapping("/process")
    public Result getProcess(@RequestParam String key) {
        return Result.success(AsyncProcessUtils.getProcess(key));
    }
}
