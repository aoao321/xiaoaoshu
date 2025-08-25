package com.aoao.xiaoaoshu.auth.authentication.provider;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.xiaoaoshu.auth.authentication.token.CodeAuthenticationToken;
import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.PermissionDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author aoao
 * @create 2025-08-24-18:10
 */
@Component
public class CodeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PermissionDOMapper permissionDOMapper;

    @Autowired
    private UserDOMapper userDOMapper;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String phone = (String) authentication.getPrincipal();
            String code = (String) authentication.getCredentials();

            String key = RedisKeyConstants.buildVerificationCodeKey(phone);
            String sentCode = redisTemplate.opsForValue().get(key);

            if (sentCode == null) {
                throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_USELESS);
            }
            if (!sentCode.equals(code)) {
                throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
            }
            redisTemplate.delete(key);

            UserDO user = userDOMapper.getByPhone(phone);
            if (user == null) {
                // 自动注册逻辑
                user = register(phone);
            }
            List<String> permissions = permissionDOMapper.findPermissonByPhone(phone);
            LoginUser loginUser = new LoginUser(user, permissions);
            return new CodeAuthenticationToken(loginUser, code, loginUser.getAuthorities());
        } catch (BizException e) {
            throw new org.springframework.security.authentication.BadCredentialsException(e.getErrorMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private UserDO register(String phone) {
        return null;
    }
}
