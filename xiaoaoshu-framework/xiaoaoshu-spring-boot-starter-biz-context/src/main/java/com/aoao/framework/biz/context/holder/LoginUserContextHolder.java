package com.aoao.framework.biz.context.holder;

import java.util.Map;

/**
 * @author aoao
 * @create 2025-09-11-16:45
 */
public class LoginUserContextHolder {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
