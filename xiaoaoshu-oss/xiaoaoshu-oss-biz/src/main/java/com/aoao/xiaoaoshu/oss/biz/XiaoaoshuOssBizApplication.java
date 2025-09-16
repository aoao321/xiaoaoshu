package com.aoao.xiaoaoshu.oss.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author aoao
 * @create 2025-09-08-22:31
 */
@SpringBootApplication
@EnableDiscoveryClient
public class XiaoaoshuOssBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuOssBizApplication.class, args);
    }
}
