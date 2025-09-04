package com.aoao.framework.jwt.config;

import com.aoao.framework.jwt.properties.JwtTokenProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aoao.framework.jwt.JwtTokenHelper;

/**
 * Jwt 配置类
 * 注册 JwtTokenHelper 到 Spring 容器
 */
@Configuration
@EnableConfigurationProperties(JwtTokenProperties.class)
public class JwtConfig {

    @Bean
    public JwtTokenHelper jwtTokenHelper(JwtTokenProperties jwtTokenProperties) {
        return new JwtTokenHelper(jwtTokenProperties);
    }
}
