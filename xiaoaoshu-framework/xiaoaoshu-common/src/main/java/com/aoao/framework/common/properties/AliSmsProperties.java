package com.aoao.framework.common.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-08-22-20:51
 */
@Data
@Component
public class AliSmsProperties {

    @Value("${ali.sms.endpoint}")
    private String endpoint;

    @Value("${ali.sms.access-key}")
    private String accessKeyId;

    @Value("${ali.sms.secret-key}")
    private String accessKeySecret;

    @Value("${ali.sms.sign-name}")
    private String signName;

    @Value("${ali.sms.template-code}")
    private String templateCode;
}

