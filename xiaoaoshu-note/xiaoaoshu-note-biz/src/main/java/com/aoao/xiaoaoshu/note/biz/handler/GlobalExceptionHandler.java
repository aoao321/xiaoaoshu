package com.aoao.xiaoaoshu.note.biz.handler;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BaseException;
import com.aoao.framework.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * @author aoao
 * @create 2025-08-21-13:49
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 捕获自定义业务异常
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public <T extends BaseException> Result handleBizException(HttpServletRequest request, T e) {
        log.warn("{} request fail, errorCode: {}, errorMessage: {}", request.getRequestURI(), e.getErrorCode(), e.getErrorMessage());
        return Result.fail(e);
    }

    /**
     * 捕获参数校验异常
     * @return
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public Result handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        // 参数错误异常码
        String errorCode = ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode();

        // 获取 BindingResult
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        // 获取校验不通过的字段，并组合错误信息，格式为： email 邮箱格式不正确, 当前值: '123124qq.com';
        Optional.ofNullable(bindingResult.getFieldErrors()).ifPresent(errors -> {
            errors.forEach(error ->
                    sb.append(error.getField())
                            .append(" ")
                            .append(error.getDefaultMessage())
                            .append(", 当前值: '")
                            .append(error.getRejectedValue())
                            .append("'; ")
            );
        });
        // 错误信息
        String errorMessage = sb.toString();
        log.warn("{} request error, errorCode: {}, errorMessage: {}", request.getRequestURI(), errorCode, errorMessage);
        return Result.fail(errorCode, errorMessage);
    }

    /**
     * 只捕获异常，处理交给RestAccessDeniedHandler
     * @param e
     * @throws AccessDeniedException
     */
    @ExceptionHandler({ AccessDeniedException.class })
    public void throwAccessDeniedException(AccessDeniedException e) throws AccessDeniedException {
        // 捕获到鉴权失败异常，主动抛出，交给 RestAccessDeniedHandler 去处理
        log.info("============= 捕获到 AccessDeniedException");
        throw e;
    }

    /**
     * 其他异常统一处理
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleOtherException(HttpServletRequest request, Exception e) {
        log.error("{} request error , errorMessage: {}", request.getRequestURI(), e);
        return Result.fail(ResponseCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Result handleBadCredentialsException(BadCredentialsException e) {
        log.warn("登录失败：密码错误", e);
        return Result.fail(ResponseCodeEnum.PASSWORD_ERROR);
    }


}
