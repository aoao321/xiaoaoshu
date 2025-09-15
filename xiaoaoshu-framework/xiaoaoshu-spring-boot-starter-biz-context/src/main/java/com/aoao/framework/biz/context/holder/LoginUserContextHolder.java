package com.aoao.framework.biz.context.holder;

import com.aoao.framework.biz.context.model.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * @author aoao
 * @create 2025-09-11-16:45
 */
public class LoginUserContextHolder {

    private static final ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    public static void set(LoginUser loginUser) {
        threadLocal.set(loginUser);
    }

    public static LoginUser get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

    // 便捷方法
    public static Long getCurrentId() {
        LoginUser user = threadLocal.get();
        return user != null ? user.getUserId() : null;
    }

    public static List<String> getCurrentRoles() {
        LoginUser user = threadLocal.get();
        return user != null ? user.getRoles() : null;
    }

    public static List<String> getCurrentPermissions() {
        LoginUser user = threadLocal.get();
        return user != null ? user.getPermissions() : null;
    }


}

