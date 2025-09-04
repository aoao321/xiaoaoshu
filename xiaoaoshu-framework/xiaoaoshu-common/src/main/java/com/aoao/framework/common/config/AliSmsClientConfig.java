package com.aoao.framework.common.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aoao.framework.common.properties.AliSmsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aoao
 * @create 2025-08-22-21:35
 */
@Configuration
public class AliSmsClientConfig {

    @Autowired
    private AliSmsProperties aliSmsProperties;

    @Bean
    public Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(aliSmsProperties.getAccessKeyId())
                .setAccessKeySecret(aliSmsProperties.getAccessKeySecret())
                .setEndpoint(aliSmsProperties.getEndpoint());

        return new com.aliyun.dysmsapi20170525.Client(config); // 确保导入正确的 Client 类
    }
}
