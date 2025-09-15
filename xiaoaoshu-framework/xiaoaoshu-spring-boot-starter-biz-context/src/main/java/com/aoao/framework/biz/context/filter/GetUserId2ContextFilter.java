package com.aoao.framework.biz.context.filter;

import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.biz.context.model.LoginUser;
import com.aoao.framework.common.constant.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author aoao
 * @create 2025-09-11-16:38
 */
@Slf4j
public class GetUserId2ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头里拿用户信息
        String userIdHeader = request.getHeader(GlobalConstants.USER_ID);
        String rolesHeader = request.getHeader(GlobalConstants.USER_ROLES);
        String permsHeader = request.getHeader(GlobalConstants.USER_PERMISSIONS);

        if (StringUtils.isBlank(userIdHeader)) {
            // 请求头里没有 userId，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = Long.valueOf(userIdHeader);
        List<String> roles = StringUtils.isNotBlank(rolesHeader)
                ? Arrays.asList(rolesHeader.split(","))
                : Collections.emptyList();

        List<String> permissions = StringUtils.isNotBlank(permsHeader)
                ? Arrays.asList(permsHeader.split(","))
                : Collections.emptyList();

        // 放入 ThreadLocal
        LoginUser userInfo = new LoginUser(userId, roles, permissions);
        LoginUserContextHolder.set(userInfo);
        log.debug("===== 用户上下文已保存 userId: {}, roles: {}, perms: {}", userId, roles, permissions);

        // 放入 Spring Security 的 Context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userInfo,
                        null,
                        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 一定要删除，防止内存泄漏
            LoginUserContextHolder.remove();
            SecurityContextHolder.clearContext();
            log.debug("===== 删除 ThreadLocal，userId: {}", userId);
        }
    }
}
