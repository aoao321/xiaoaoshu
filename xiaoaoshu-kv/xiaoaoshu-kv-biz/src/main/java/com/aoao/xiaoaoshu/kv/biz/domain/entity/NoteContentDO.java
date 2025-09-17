package com.aoao.xiaoaoshu.kv.biz.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-17-18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("note_content")
public class NoteContentDO {
    @PrimaryKey("id")
    private UUID id;

    private String content;
}
