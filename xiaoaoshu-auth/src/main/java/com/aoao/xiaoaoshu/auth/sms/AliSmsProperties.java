package com.aoao.xiaoaoshu.auth.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-08-22-20:51
 */
@ConfigurationProperties(prefix = "ali.sms")
@Data
@Component
public class AliSmsProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
}
