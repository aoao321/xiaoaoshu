package com.aoao.xiaoaoshu.note.biz.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aoao
 * @create 2025-10-01-19:42
 */
@Configuration
public class RabbitConfig {

    public static final String DELETE_NOTE_LOCAL_CACHE_EXCHANGE = "delete.note.local.cache.exchange";
    public static final String DELETE_NOTE_LOCAL_CACHE_QUEUE = "delete.note.local.cache.queue";

    @Bean
    public FanoutExchange deleteNoteLocalCacheExchange() {
        return new FanoutExchange(DELETE_NOTE_LOCAL_CACHE_EXCHANGE);
    }

    @Bean
    public Queue deleteNoteLocalCacheQueue() {
        return new Queue(DELETE_NOTE_LOCAL_CACHE_QUEUE);
    }

    /**
     * 队列绑定到 Fanout 交换机
     */
    @Bean
    public Binding binding(Queue deleteNoteLocalCacheQueue, FanoutExchange deleteNoteLocalCacheExchange) {
        return BindingBuilder.bind(deleteNoteLocalCacheQueue).to(deleteNoteLocalCacheExchange);
    }

}
