package com.aoao.xiaoaoshu.note.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author aoao
 * @create 2025-09-22-19:18
 */
@Mapper
public interface TopicDOMapper {
    String selectNameByPrimaryKey(Long id);
}
