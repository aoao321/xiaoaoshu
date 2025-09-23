package com.aoao.xiaoaoshu.note.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author aoao
 * @create 2025-09-22-15:20
 */
@SpringBootApplication
@EnableFeignClients("com.aoao")
public class XiaoaoshuNoteBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuNoteBizApplication.class, args);
    }
}
