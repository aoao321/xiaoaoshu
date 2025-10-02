package com.aoao.xiaoaoshu.note.biz.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
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

    /**
     * ============= Redis 延迟双删 (延迟队列 + 死信) =============
     */
    public static final String DELAY_DELETE_NOTE_REDIS_CACHE_EXCHANGE = "delay.delete.note.redis.exchange";
    public static final String DELAY_DELETE_NOTE_REDIS_CACHE_QUEUE = "delay.delete.note.redis.queue";
    public static final String DELAY_DELETE_NOTE_REDIS_ROUTING_KEY = "delay.delete.note.redis";


    public static final String DEAD_LETTER_EXCHANGE = "delete.note.redis.dead.exchange";
    public static final String DEAD_LETTER_QUEUE = "delete.note.redis.dead.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "delete.note.redis.dead";

    @Bean
    public DirectExchange delayDeleteNoteRedisExchange() {
        return new DirectExchange(DELAY_DELETE_NOTE_REDIS_CACHE_EXCHANGE);
    }

    // 延迟队列（带 TTL + 死信路由）
    @Bean
    public Queue delayDeleteNoteRedisCacheQueue() {
        return QueueBuilder.durable(DELAY_DELETE_NOTE_REDIS_CACHE_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 500) // 500ms 后进入死信队列
                .build();
    }

    @Bean
    public Binding delayDeleteNoteRedisCacheBinding(Queue delayDeleteNoteRedisCacheQueue,
                                                    @Qualifier("delayDeleteNoteRedisExchange")DirectExchange delayDeleteNoteRedisCacheExchange) {
        return BindingBuilder.bind(delayDeleteNoteRedisCacheQueue)
                .to(delayDeleteNoteRedisCacheExchange)
                .with(DELAY_DELETE_NOTE_REDIS_ROUTING_KEY);
    }

    // 死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // 死信队列
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE);
    }

    // 死信队列绑定
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY);
    }



}
