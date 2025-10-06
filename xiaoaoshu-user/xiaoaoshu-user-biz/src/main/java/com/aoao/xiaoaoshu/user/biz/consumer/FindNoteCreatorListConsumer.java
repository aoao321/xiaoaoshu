package com.aoao.xiaoaoshu.user.biz.consumer;

import cn.hutool.core.util.RandomUtil;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.util.JsonUtil;
import com.aoao.xiaoaoshu.user.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author aoao
 * @create 2025-10-05-22:37
 */
@Component
public class FindNoteCreatorListConsumer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 同步redis中没有的用户信息
     * @param userFromDB
     */
    @RabbitListener(queues = RabbitConfig.USER_INFO_TO_REDIS_QUEUE)
    public void onMessage(List<FindNoteCreatorByIdRspDTO> userFromDB) {
        // 构建 Redis key-value map
        Map<String, String> map = userFromDB.stream()
                .collect(Collectors.toMap(
                        user -> RedisKeyConstants.buildUserInfoKey(user.getId()),
                        JsonUtil::toJson
                ));
        // 写入redis
        stringRedisTemplate.opsForValue().multiSet(map);
        // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
        long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
        map.keySet().forEach(key ->
                stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS)
        );
    }
}
