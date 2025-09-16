package com.aoao.xiaoaoshu.user.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author aoao
 * @create 2025-09-16-20:24
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register",
                                "/user/role/findRoleByPhone",
                                "/user/findByPhone",
                                "/user/findById").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

