package com.aoao.xiaoaoshu.auth.config;

import com.aoao.xiaoaoshu.auth.authentication.filter.AuthenticationFilter;
import com.aoao.xiaoaoshu.auth.authentication.filter.TokenAuthenticationFilter;
import com.aoao.xiaoaoshu.auth.authentication.handler.LoginFailHandler;
import com.aoao.xiaoaoshu.auth.authentication.handler.LoginSuccessHandler;
import com.aoao.xiaoaoshu.auth.authentication.provider.CodeAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author aoao
 * @create 2025-08-24-22:30
 */
@Configuration
public class JwtAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailHandler loginFailHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private CodeAuthenticationProvider codeAuthenticationProvider;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        // 配置 DaoAuthenticationProvider（用户名 + 密码）
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);

        // 注册两个 Provider
        httpSecurity.authenticationProvider(daoProvider);
        httpSecurity.authenticationProvider(codeAuthenticationProvider);

        // 自定义登录过滤器
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(httpSecurity.getSharedObject(AuthenticationManager.class));
        authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(loginFailHandler);

        // 加过滤器
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
