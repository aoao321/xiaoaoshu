package com.aoao.xiaoaoshu.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-08-23-22:42
 */
@Data
@Component
@ConfigurationProperties("jwt")
public class JwtTokenProperties {
    private String issuer;
    private String secret;
    private Long tokenExpireTime;
    private String tokenHeaderKey;
    private String tokenPrefix;

}
