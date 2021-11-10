package com.unicom.collect.web.controller;

import com.unicom.common.util.Result;
import com.unicom.project.service.UserProjectResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DataController {

    private final UserProjectResultService projectResultService;


    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public Result queryData(@RequestBody Map<String,Object> request) {
        // return Result.success(projectResultService.listByQueryConditions(request));
        return Result.success(projectResultService.getData(request));
    }

}
