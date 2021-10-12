package com.unicom.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.project.entity.UserProjectLogicEntity;
import com.unicom.project.mapper.UserProjectLogicMapper;
import com.unicom.project.service.UserProjectLogicService;
import org.springframework.stereotype.Service;


@Service("userProjectLogicService")
public class UserProjectLogicServiceImpl extends ServiceImpl<UserProjectLogicMapper, UserProjectLogicEntity> implements UserProjectLogicService {
}
