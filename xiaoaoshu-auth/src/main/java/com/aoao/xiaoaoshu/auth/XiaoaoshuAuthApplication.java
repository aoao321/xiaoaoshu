package com.aoao.xiaoaoshu.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.aoao.xiaoaoshu.auth.domain.mapper")
public class XiaoaoshuAuthApplication {
    public static void main(String[] args) {SpringApplication.run(XiaoaoshuAuthApplication.class, args);}

}
