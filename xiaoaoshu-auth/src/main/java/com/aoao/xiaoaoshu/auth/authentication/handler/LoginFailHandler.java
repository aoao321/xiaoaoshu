package com.aoao.xiaoaoshu.auth.authentication.handler;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
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
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorCode;
        String errorMessage;

        // 1. 手机号为空异常
        if (exception instanceof UsernameOrPasswordNullException) {
            errorMessage = exception.getMessage();
            errorCode = ResponseCodeEnum.USERNAME_OR_PWD_IS_NULL.getErrorCode();
            log.warn("用户名或密码为空: {}", errorMessage);
            HttpResultUtil.fail(response, Result.fail(errorCode, errorMessage));
            return;
        }

        // 2. 密码错误或验证码错误
        if (exception instanceof BadCredentialsException) {
            Throwable cause = exception.getCause();
            errorMessage = (cause != null && cause.getMessage() != null) ? cause.getMessage() : exception.getMessage();
            errorCode = ResponseCodeEnum.PASSWORD_ERROR.getErrorCode();
            log.warn("登录失败: {}", errorMessage);
            HttpResultUtil.fail(response, Result.fail(errorCode, errorMessage));
            return;
        }

        // 3. 自定义业务异常（BizException）
        Throwable cause = exception.getCause();
        if (cause instanceof BizException) {
            BizException biz = (BizException) cause;
            errorMessage = biz.getErrorMessage();
            errorCode = biz.getErrorCode();
            log.warn("登录失败: {} - {}", errorCode, errorMessage);
            HttpResultUtil.fail(response, Result.fail(errorCode, errorMessage));
            return;
        }

        // 4. 其他未捕获异常
        errorMessage = exception.getMessage() != null ? exception.getMessage() : "登录失败";
        errorCode = ResponseCodeEnum.LOGIN_FAIL.getErrorCode();
        log.warn("登录失败: {}", errorMessage);
        HttpResultUtil.fail(response, Result.fail(errorCode, errorMessage));
    }
}

