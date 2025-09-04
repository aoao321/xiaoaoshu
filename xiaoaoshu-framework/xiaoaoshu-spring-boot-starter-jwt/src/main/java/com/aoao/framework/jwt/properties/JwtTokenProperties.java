package com.aoao.framework.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author aoao
 * @create 2025-08-23-22:42
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtTokenProperties {
    private String issuer;
    private String secret;
    private Long tokenExpireTime;
    private String tokenHeaderKey;
    private String tokenPrefix;
}
