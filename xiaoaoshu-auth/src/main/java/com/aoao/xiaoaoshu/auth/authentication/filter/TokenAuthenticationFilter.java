package com.aoao.xiaoaoshu.auth.authentication.filter;

import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.jwt.JwtTokenHelper;
import com.aoao.framework.jwt.properties.JwtTokenProperties;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * @author aoao
 * @create 2025-08-24-22:33
 */
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private JwtTokenProperties jwtTokenProperties;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtTokenProperties.getTokenHeaderKey());

        if (StringUtils.isNotBlank(header) && header.startsWith(jwtTokenProperties.getTokenPrefix())) {
            String token = header.substring(jwtTokenProperties.getTokenPrefix().length()).trim();
            try {
                // 解析 token 获取用户 ID
                String userId = jwtTokenHelper.getIdByToken(token);

                if (StringUtils.isNotBlank(userId)
                        && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {

                    // 查询用户信息
                    UserDO user = userDOMapper.getById(Long.valueOf(userId));
                    if (user == null) {
                        throw new AuthenticationServiceException("用户不存在");
                    }

                    // 构造 UserDetails
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());

                    // 构建 Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                // token 解析异常统一交给 AuthenticationEntryPoint 处理
                authenticationEntryPoint.commence(request, response,
                        new AuthenticationServiceException("Token 无效或已过期"));
                return;
            }
        }

        // 继续执行下一个过滤器
        filterChain.doFilter(request, response);
    }
}
