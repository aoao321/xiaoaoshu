package com.aoao.xiaoaoshu.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author aoao
 * @create 2025-09-10-16:10
 */
@SpringBootApplication
@MapperScan("com.aoao.xiaoaoshu.user.biz.domain.mapper")
public class XiaoaoshuUserBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuUserBizApplication.class, args);
    }
}
