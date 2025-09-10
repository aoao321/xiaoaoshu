package com.aoao.xiaoaoshu.oss.biz.factory;

import com.aoao.xiaoaoshu.oss.biz.strategy.FileStrategy;
import com.aoao.xiaoaoshu.oss.biz.strategy.impl.AliyunOSSFileStrategy;
import com.aoao.xiaoaoshu.oss.biz.strategy.impl.MinioFileStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.commons.lang3.StringUtils;

/**
 * @author aoao
 * @create 2025-09-09-16:12
 */
@Configuration
@RefreshScope
public class FileStrategyFactory {

    @Value("${storage.type}")
    private String strategyType;

    @Bean
    @RefreshScope
    public FileStrategy getFileStrategy() {
        if (StringUtils.equals(strategyType, "minio")) {
            return new MinioFileStrategy();
        } else if (StringUtils.equals(strategyType, "aliyun")) {
            return new AliyunOSSFileStrategy();
        }

        throw new IllegalArgumentException("不可用的存储类型");
    }

}
