package com.aoao.xiaoaoshu.note.biz.consumer;

import com.aoao.xiaoaoshu.note.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-10-02-16:46
 */
@Component
public class DelayDeleteNoteRedisCacheConsumer {

    @Autowired
    private NoteService noteService;

    @RabbitListener(queues = RabbitConfig.DEAD_LETTER_QUEUE)
    public void onMessage(String key) {

        noteService.delayDeleteNoteRedisCache(key);
    }
}
