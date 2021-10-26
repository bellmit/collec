package com.unicom.account.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicom.account.constant.AccountConstants;
import com.unicom.account.constant.AccountRedisKeyConstants;
import com.unicom.account.entity.UserAuthorizeEntity;
import com.unicom.account.entity.UserEntity;
import com.unicom.account.entity.enums.AccountChannelEnum;
import com.unicom.account.mapper.AUserMapper;
import com.unicom.account.request.AccountLoginRequest;
import com.unicom.account.request.QqLoginRequest;
import com.unicom.account.request.RegisterAccountRequest;
import com.unicom.account.service.UserAuthorizeService;
import com.unicom.account.service.UserService;
import com.unicom.account.util.JwtUtils;
import com.unicom.account.util.NameUtils;
import com.unicom.account.vo.LoginUserVO;
import com.unicom.common.util.RedisUtils;
import com.unicom.common.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户(AcUser)表服务实现类
 *
 * @author yangpeng
 * @since 2020-11-10 18:10:43
 */
@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<AUserMapper, UserEntity> implements UserService {


    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final UserAuthorizeService userAuthorizeService;


    @Override
    public Result emailRegister(RegisterAccountRequest request) {
        //检查验证码是否正确
        String code = redisUtils.get(StrUtil.format(AccountRedisKeyConstants.EMAIL_CODE, request.getEmail()), String.class);
        if (!request.getCode().equals(code)) {
            return Result.failed("验证码错误");
        }
        if (ObjectUtil.isNotNull(getUserByEmail(request.getEmail()))) {
            return Result.failed("该邮箱已经注册");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(request.getPassword());
        userEntity.setRegChannel(AccountChannelEnum.EMAIL);
        this.createUser(userEntity);
        return Result.success();
    }

    @Override
    public Result phoneRegister(RegisterAccountRequest request) {
        if (ObjectUtil.isNotNull(getUserByPhoneNumber(request.getPhoneNumber()))) {
            return Result.failed("该手机已经注册");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setPhoneNumber(request.getPhoneNumber());
        userEntity.setPassword(request.getPassword());
        userEntity.setRegChannel(AccountChannelEnum.PHONE);
        this.createUser(userEntity);
        return Result.success();
    }

    /**
     * 创建用户
     */
    private void createUser(UserEntity userEntity) {
        userEntity.setName(NameUtils.getCnName());
        userEntity.setAvatar(AccountConstants.DEFAULT_AVATAR);
        userEntity.setPassword(DigestUtil.sha256Hex(userEntity.getPassword()));
        this.save(userEntity);
    }


    @Override
    public Result accountLogin(AccountLoginRequest request) {
        UserEntity userEntity;
        if (ReUtil.isMatch(Validator.EMAIL, request.getAccount())) {
            userEntity = getUserByEmail(request.getAccount());
        } else {
            userEntity = getUserByPhoneNumber(request.getAccount());
        }
        if (ObjectUtil.isNull(userEntity) || !DigestUtil.sha256Hex(request.getPassword()).equals(userEntity.getPassword())) {
            return Result.failed("账号或密码错误");
        }
        return Result.success(getLoginResult(userEntity,
                ReUtil.isMatch(Validator.EMAIL, request.getAccount()) ? AccountChannelEnum.EMAIL : AccountChannelEnum.PHONE,
                request.getRequestIp()));
    }


    /**
     * 获取登录结果
     */
    @Override
    public LoginUserVO getLoginResult(UserEntity userEntity, AccountChannelEnum channel, String requestIp) {
        userEntity.setLastLoginIp(requestIp);
        userEntity.setLastLoginChannel(channel);
        userEntity.setLastLoginTime(LocalDateTime.now());
        this.updateById(userEntity);
        String token = jwtUtils.generateToken(userEntity.getId());
        return new LoginUserVO(userEntity.getAvatar(), userEntity.getName(), token);
    }

    @Override
    public LoginUserVO qqLogin(QqLoginRequest request) {
        UserEntity userEntity = new UserEntity();
        UserAuthorizeEntity authorizeEntity = userAuthorizeService.getQqAuthorization(request.getAuthorizeCode(), request.getRedirectUri(), userEntity);
        if (ObjectUtil.isNotNull(userEntity) && ObjectUtil.isNull(userEntity.getId())) {
            this.save(userEntity);
            //更新绑定
            authorizeEntity.setUserId(userEntity.getId());
            userAuthorizeService.updateById(authorizeEntity);
        } else {
            userEntity = this.getById(userEntity.getId());
        }
        return getLoginResult(userEntity,
                AccountChannelEnum.QQ, request.getRequestIp());
    }

    /**
     * 根据邮箱获取
     */
    @Override
    public UserEntity getUserByEmail(final String email) {
        return this.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getEmail, email));
    }


    @Override
    public UserEntity getUserByPhoneNumber(final String phoneNumber) {
        return this.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getPhoneNumber, phoneNumber));
    }

    @Override
    public Boolean updatePassword(Long userId, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setPassword(DigestUtil.sha256Hex(password));
        return this.updateById(userEntity);
    }
}


