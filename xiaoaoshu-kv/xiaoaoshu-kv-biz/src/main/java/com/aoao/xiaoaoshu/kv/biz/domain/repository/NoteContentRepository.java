package com.aoao.xiaoaoshu.kv.biz.domain.repository;

import com.aoao.xiaoaoshu.kv.biz.domain.entity.NoteContentDO;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-17-19:42
 */
public interface NoteContentRepository extends CassandraRepository<NoteContentDO, UUID> {
}
