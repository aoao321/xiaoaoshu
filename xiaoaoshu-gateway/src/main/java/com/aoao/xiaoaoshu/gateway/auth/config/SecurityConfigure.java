package com.aoao.xiaoaoshu.gateway.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author aoao
 * @create 2025-09-02-20:43
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfigure {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 1. 禁用 CSRF 保护（核心解决措施）
                .csrf(csrf -> csrf.disable())

                // 2. 配置授权规则（按需调整，例如放行登录、健康检查接口）
                .authorizeExchange(exchanges -> exchanges
                        // 放行白名单路径（无需认证）
                        .pathMatchers(
                                "/auth/**",
                                "/public/**")
                        .permitAll()

                        // 其他所有请求需要认证（配合你的 Token 过滤器）
                        .anyExchange()
                        .authenticated()
                )

                // 3. 禁用默认的表单登录/HTTP Basic 认证（网关用 Token 认证，无需这些）
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
