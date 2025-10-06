package com.aoao.xiaoaoshu.user.biz.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aoao
 * @create 2025-10-05-22:22
 */
@Configuration
public class RabbitConfig {

    public static final String USER_INFO_TO_REDIS_EXCHANGE = "user_info_to_redis_exchange";
    public static final String USER_INFO_TO_REDIS_QUEUE = "user_info_to_redis_queue";
    public static final String USER_INFO_TO_REDIS_ROUTING_KEY = "user_info_to_redis_routing_key";

    @Bean
    public Queue userInfoToRedisQueue() {
        return new Queue(USER_INFO_TO_REDIS_QUEUE);
    }

    @Bean
    public DirectExchange userInfoToRedisExchange() {
        return new DirectExchange(USER_INFO_TO_REDIS_EXCHANGE);
    }

    @Bean
    public Binding userInfoToRedisBinding(Queue userInfoToRedisQueue, DirectExchange userInfoToRedisExchange) {
        return BindingBuilder.bind(userInfoToRedisQueue)
                .to(userInfoToRedisExchange)
                .with(USER_INFO_TO_REDIS_ROUTING_KEY);
    }
}
