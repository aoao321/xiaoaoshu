package com.aoao.framework.biz.context.filter;

import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.GlobalConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author aoao
 * @create 2025-09-11-16:38
 */
@Slf4j
public class GetUserId2ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的userId
        String userId = request.getHeader(GlobalConstants.USER_ID);
        // 判断请求头中是否存在用户 ID
        if (StringUtils.isBlank(userId)) {
            // 若为空，则直接放行
            filterChain.doFilter(request, response);
            return;
        }
        // 放入treadLocal中
        LoginUserContextHolder.setCurrentId(Long.valueOf(userId));

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 一定要删除 ThreadLocal ，防止内存泄露
            LoginUserContextHolder.removeCurrentId();
            log.info("===== 删除 ThreadLocal， userId: {}", userId);
        }
    }
}