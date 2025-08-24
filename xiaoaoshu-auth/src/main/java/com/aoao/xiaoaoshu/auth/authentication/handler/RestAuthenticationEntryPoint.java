package com.aoao.xiaoaoshu.auth.authentication.handler;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.util.HttpResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("用户未登录访问受保护的资源: {}", authException.getMessage());
        if (authException instanceof InsufficientAuthenticationException) {
            HttpResultUtil.fail(response, HttpStatus.UNAUTHORIZED.value(), Result.fail(ResponseCodeEnum.UNAUTHORIZED));
            return;
        }

        HttpResultUtil.fail(response, HttpStatus.UNAUTHORIZED.value(), Result.fail(authException.getMessage()));
    }
}
