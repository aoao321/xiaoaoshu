package com.aoao.xiaoaoshu.auth.authentication.handler;

import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.constant.RedisTimeConstants;
import com.aoao.framework.jwt.JwtTokenHelper;
import com.aoao.xiaoaoshu.auth.domain.authoriztion.LoginUser;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.framework.common.util.HttpResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author aoao
 * @create 2025-08-24-22:06
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LoginUser user = (LoginUser)authentication.getPrincipal();
        // 生成token
        UserDO userDO = user.getUserDO();
        Long id = userDO.getId();
        String token = jwtTokenHelper.generateToken(id.toString());
        // 存入redis中
        String key = RedisKeyConstants.buildTokenKey(id);
        stringRedisTemplate.opsForValue().set(key,token, RedisTimeConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        // 返回
        HttpResultUtil.ok(response, Result.success(token));
    }
}
