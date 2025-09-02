package com.aoao.xiaoaoshu.auth.service.impl;

import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.auth.enums.LoginTypeEnum;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.xiaoaoshu.auth.model.vo.user.UserLoginReqVO;
import com.aoao.xiaoaoshu.auth.service.UserService;
import com.aoao.xiaoaoshu.auth.util.JwtTokenHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author aoao
 * @create 2025-08-24-14:42
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;


    @Override
    public Result logout() {
        return Result.success();
    }
}
