package com.aoao.framework.biz.operationlog.annotation;

import java.lang.annotation.*;

/**
 * @author aoao
 * @create 2025-08-18-22:33
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Log {
    String value() default "";
}
