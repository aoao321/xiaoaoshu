package com.aoao.xiaoaoshu.relation.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author aoao
 * @create 2025-10-03-16:27
 */
@SpringBootApplication
@EnableFeignClients("com.aoao")
public class XiaoaoshuRelationBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoaoshuRelationBizApplication.class, args);
    }
}
