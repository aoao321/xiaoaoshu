package com.aoao.xiaoaoshu.relation.biz.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aoao
 * @create 2025-10-04-11:03
 */
@Configuration
public class RabbitConfig {

    public static final String FOLLOW_UNFOLLOW_EXCHANGE = "follow_unfollow_exchange";
    public static final String FOLLOW_UNFOLLOW_QUEUE = "follow_unfollow_queue";
    public static final String FOLLOW_ROUTING_KEY = "follow";
    public static final String UNFOLLOW_ROUTING_KEY = "unfollow";



    @Bean
    public TopicExchange followUnfollowExchange() {
        return new TopicExchange(FOLLOW_UNFOLLOW_EXCHANGE);
    }

    @Bean
    public Queue followUnfollowQueue() {
        return new Queue(FOLLOW_UNFOLLOW_QUEUE);
    }

    // 绑定“关注”事件
    @Bean
    public Binding followBinding(Queue followUnfollowQueue, TopicExchange followUnfollowExchange) {
        return BindingBuilder
                .bind(followUnfollowQueue)
                .to(followUnfollowExchange)
                .with(FOLLOW_ROUTING_KEY);
    }

    // 绑定“取关”事件
    @Bean
    public Binding unfollowBinding(Queue followUnfollowQueue, TopicExchange followUnfollowExchange) {
        return BindingBuilder
                .bind(followUnfollowQueue)
                .to(followUnfollowExchange)
                .with(UNFOLLOW_ROUTING_KEY);

    }


}
