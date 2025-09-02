package com.aoao.xiaoaoshu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author aoao
 * @create 2025-09-02-19:55
 */
@SpringBootApplication
@EnableDiscoveryClient
public class XiaoaoshuGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuGatewayApplication.class, args);
    }
}
