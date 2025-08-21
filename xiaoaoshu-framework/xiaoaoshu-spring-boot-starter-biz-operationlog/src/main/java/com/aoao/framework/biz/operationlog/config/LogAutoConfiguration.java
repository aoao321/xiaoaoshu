package com.aoao.framework.biz.operationlog.config;

import com.aoao.framework.biz.operationlog.aspect.LogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LogAutoConfiguration {

    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }
}
