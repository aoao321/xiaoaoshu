package com.aoao.xiaoaoshu.auth.authentication.filter;

import com.aoao.xiaoaoshu.auth.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.domain.entity.UserDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.auth.properties.JwtTokenProperties;
import com.aoao.xiaoaoshu.auth.util.JwtTokenHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author aoao
 * @create 2025-08-24-22:33
 */
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private JwtTokenProperties jwtTokenProperties;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取 key 为 Authorization 的值
        String header = request.getHeader(jwtTokenProperties.getTokenHeaderKey());

        // 判断 value 值是否以 Bearer 开头
        if (StringUtils.startsWith(header, jwtTokenProperties.getTokenPrefix())) {
            // 截取 Token 令牌
            String token = StringUtils.substring(header, 7);
            log.info("Token: {}", token);

            // 判空 Token，不可用直接放行
            if (StringUtils.isNotBlank(token)) {
                try {
                    // 校验 Token 是否可用, 若解析异常，针对不同异常做出不同的响应参数
                    jwtTokenHelper.validateToken(token);
                } catch (ExpiredJwtException e) {
                    authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效"));
                    return;
                } catch (JwtException | IllegalArgumentException e) {
                    // 抛出异常，统一让 AuthenticationEntryPoint 处理响应参数
                    authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 不可用"));
                    return;
                }

                // 从 Token 中解析出id
                String id = jwtTokenHelper.getIdByToken(token);
                if (StringUtils.isNotBlank(id)
                        && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                    UserDO userDO = userDOMapper.getById(Long.valueOf(id));
                    // 验证redis
                    String redisToken = stringRedisTemplate.opsForValue().get(RedisKeyConstants.buildTokenKey(userDO.getPhone()));
                    if (!token.equals(redisToken)) {
                        // Token 已失效或被覆盖，拒绝访问
                        throw new AuthenticationServiceException("Token 已失效或被覆盖，请重新登录");
                    }
                    // 根据用户名获取用户详情信息
                    UserDO user = userDOMapper.getById(Long.valueOf(id));
                    String phone = user.getPhone();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 继续执行写一个过滤器
        filterChain.doFilter(request, response);
    }
}
