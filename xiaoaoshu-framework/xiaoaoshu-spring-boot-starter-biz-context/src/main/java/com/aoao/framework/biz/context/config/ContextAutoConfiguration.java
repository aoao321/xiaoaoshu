package com.aoao.framework.biz.context.config;

import com.aoao.framework.biz.context.filter.GetUserId2ContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


/**
 * @author aoao
 * @create 2025-09-12-8:10
 */
@Configuration
public class ContextAutoConfiguration {

    @Value("${security.whitelist}")
    private List<String> whiteList;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(new GetUserId2ContextFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

