package com.aoao.xiaoaoshu.auth.authentication.provider;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.util.JsonUtil;
import com.aoao.xiaoaoshu.auth.authentication.token.CodeAuthenticationToken;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.constant.RedisTimeConstants;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.RoleDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserRoleDOMapper;
import com.aoao.xiaoaoshu.auth.rpc.UserRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author aoao
 * @create 2025-08-24-18:10
 */
@Component
public class CodeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RoleDOMapper roleDOMapper;
    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserRoleDOMapper userRoleDOMapper;
    @Autowired
    private UserRpcService userRpcService;

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
                // 调用user服务注册
                Long userIdTmp = userRpcService.register(phone);
                // 若调用用户服务，返回的用户 ID 为空，则提示登录失败
                if (Objects.isNull(userIdTmp)) {
                    throw new BizException(ResponseCodeEnum.LOGIN_FAIL);
                }
            }
            List<String> roles = roleDOMapper.findRoleByPhone(phone);
            LoginUser loginUser = new LoginUser(user, roles);
            // 把用户-角色存入redis中
            String userRolesKey = RedisKeyConstants.buildUserRoleKey(user.getId().toString());
            redisTemplate.opsForValue().set(userRolesKey, JsonUtil.toJson(roles), RedisTimeConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
            return new CodeAuthenticationToken(loginUser, code, loginUser.getAuthorities());
        } catch (BizException e) {
            throw new org.springframework.security.authentication.BadCredentialsException(e.getErrorMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CodeAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
