package com.aoao.xiaoaoshu.oss.biz.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-09-09-20:38
 */
@ConfigurationProperties(prefix = "storage.ali.oss")
@Component
@Data
public class AliyunOSSProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
