package com.aoao.xiaoaoshu.auth.authentication.handler;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.authentication.exception.UsernameOrPasswordNullException;
import com.aoao.xiaoaoshu.auth.util.HttpResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author aoao
 * @create 2025-08-24-22:06
 */
@Component
@Slf4j
public class LoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        if (exception instanceof UsernameOrPasswordNullException) {
            log.warn("用户名或密码为空: {}", exception.getMessage());
            HttpResultUtil.fail(response, Result.fail(exception.getMessage()));
        } else if (exception instanceof BadCredentialsException) {
            log.warn("用户名或密码错误");
            HttpResultUtil.fail(response, Result.fail(ResponseCodeEnum.PASSWORD_ERROR));
        } else {
            log.warn("登录失败: {}", exception.getMessage());
            HttpResultUtil.fail(response, Result.fail(ResponseCodeEnum.LOGIN_FAIL));
        }
    }
}
