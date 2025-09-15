package com.aoao.xiaoaoshu.auth.authentication.handler;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.HttpResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("登录成功访问收保护的资源，但是权限不够:{} ", accessDeniedException.getMessage());
        HttpResultUtil.fail(response, Result.fail(ResponseCodeEnum.FORBIDDEN));
    }
}
