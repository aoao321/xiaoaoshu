package com.aoao.xiaoaoshu.auth;

import com.alibaba.druid.filter.config.ConfigTools;
import com.aoao.xiaoaoshu.auth.properties.AliSmsProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@Slf4j
class XiaoaoshuAuthApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AliSmsProperties aliSmsProperties;

    @Test
    @SneakyThrows
    void testEncodePassword() {
        // 你的密码
        String password = "040731";
        String[] arr = ConfigTools.genKeyPair(512);

        // 私钥
        log.info("privateKey: {}", arr[0]);
        // 公钥
        log.info("publicKey: {}", arr[1]);

        // 通过私钥加密密码
        String encodePassword = ConfigTools.encrypt(arr[0], password);
        log.info("password: {}", encodePassword);
    }

    @Test
    void testRedis() {
        System.out.println(aliSmsProperties.getTemplateCode());
    }
}
