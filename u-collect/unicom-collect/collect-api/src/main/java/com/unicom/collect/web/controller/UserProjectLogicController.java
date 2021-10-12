package com.unicom.collect.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.unicom.collect.annotation.Login;
import com.unicom.common.util.Result;
import com.unicom.project.entity.UserProjectLogicEntity;
import com.unicom.project.service.UserProjectLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author : yangpeng
 * @description : 项目逻辑
 * @create : 2021-09-18 18:17
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserProjectLogicController {

    private final UserProjectLogicService projectLogicService;

    @Login
    @PostMapping("/user/project/logic/save")
    public Result saveUserProjectLogic(@RequestBody UserProjectLogicEntity userProjectLogicEntity) {
        projectLogicService.saveOrUpdate(userProjectLogicEntity);
        return Result.success(userProjectLogicEntity);
    }


    @Login
    @PostMapping("/user/project/logic/delete")
    public Result deleteUserProjectLogic(@RequestBody UserProjectLogicEntity userProjectLogicEntity) {
        return Result.success(projectLogicService.removeById(userProjectLogicEntity));
    }

    @GetMapping("/user/project/logic/list")
    public Result queryProjectItem(@RequestParam @NotBlank String projectKey) {
        List<UserProjectLogicEntity> entityList = projectLogicService.list(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, projectKey));
        return Result.success(entityList);
    }
}