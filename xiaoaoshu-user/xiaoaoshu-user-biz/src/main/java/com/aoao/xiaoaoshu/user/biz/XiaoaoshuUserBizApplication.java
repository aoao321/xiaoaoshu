package com.aoao.xiaoaoshu.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author aoao
 * @create 2025-09-10-16:10
 */
@SpringBootApplication
@MapperScan("com.aoao.xiaoaoshu.user.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.aoao.xiaoaoshu")
public class XiaoaoshuUserBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuUserBizApplication.class, args);
    }
}
