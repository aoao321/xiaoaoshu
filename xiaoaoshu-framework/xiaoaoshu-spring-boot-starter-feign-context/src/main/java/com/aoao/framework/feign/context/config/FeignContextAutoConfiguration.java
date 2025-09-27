package com.aoao.framework.feign.context.config;


import com.aoao.framework.feign.context.interceptor.FeignRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author aoao
 * @create 2025-09-16-15:17
 */
@AutoConfiguration
public class FeignContextAutoConfiguration {

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
