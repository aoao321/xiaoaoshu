package com.aoao.xiaoaoshu.note.biz.consumer;

import com.aoao.xiaoaoshu.note.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import com.aoao.xiaoaoshu.note.biz.vo.rsp.FindNoteDetailRspVO;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-10-01-20:46
 */
@Component
public class DeleteNoteLocalCacheConsumer {

    @Autowired
    private NoteService noteService;

    @RabbitListener(queues = RabbitConfig.DELETE_NOTE_LOCAL_CACHE_QUEUE)
    public void onMessage(Long noteId) {
        System.out.println("收到删除缓存通知，noteId = " + noteId);
        noteService.deleteNoteLocalCache(noteId);
    }

}
