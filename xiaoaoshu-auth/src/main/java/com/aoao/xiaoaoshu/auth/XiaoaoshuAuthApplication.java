package com.aoao.xiaoaoshu.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "com.aoao.xiaoaoshu.gateway",
        "com.aoao.framework.common",
        "com.aoao.xiaoaoshu.auth"
})
@EnableConfigurationProperties
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.aoao.xiaoaoshu.auth.domain.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.aoao.xiaoaoshu")
public class XiaoaoshuAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuAuthApplication.class, args);
    }

}
