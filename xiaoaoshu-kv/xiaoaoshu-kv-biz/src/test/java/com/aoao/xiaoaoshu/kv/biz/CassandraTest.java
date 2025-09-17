package com.aoao.xiaoaoshu.kv.biz;

import com.aoao.xiaoaoshu.kv.biz.domain.entity.NoteContentDO;
import com.aoao.xiaoaoshu.kv.biz.domain.repository.NoteContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-17-19:46
 */
@SpringBootTest
public class CassandraTest {

    @Autowired
    private NoteContentRepository noteContentRepository;

    @Test
    void testInsert() {
        NoteContentDO nodeContent = NoteContentDO.builder()
                .id(UUID.randomUUID())
                .content("代码测试笔记内容插入")
                .build();

        noteContentRepository.save(nodeContent);
    }
}
