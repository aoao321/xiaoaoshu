package com.aoao.framework.biz.context.config;

import com.aoao.framework.biz.context.filter.GetUserId2ContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aoao
 * @create 2025-09-12-8:10
 */
@Configuration
public class ContextAutoConfiguration {

    @Bean
    public FilterRegistrationBean<GetUserId2ContextFilter> filterFilterRegistrationBean() {
        GetUserId2ContextFilter getUserId2ContextFilter = new GetUserId2ContextFilter();
        FilterRegistrationBean<GetUserId2ContextFilter> filterRegistrationBean = new FilterRegistrationBean<>(getUserId2ContextFilter);
        return filterRegistrationBean;
    }

}
