package com.aoao.xiaoaoshu.auth.authentication.exception;

import org.springframework.security.core.AuthenticationException;


/**
 * @author aoao
 * @create 2025-07-13-18:01
 */
public class UsernameOrPasswordNullException extends AuthenticationException {

    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}
