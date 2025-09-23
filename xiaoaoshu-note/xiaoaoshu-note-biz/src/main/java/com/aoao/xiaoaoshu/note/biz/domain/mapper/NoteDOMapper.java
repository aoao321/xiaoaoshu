package com.aoao.xiaoaoshu.note.biz.domain.mapper;

import com.aoao.xiaoaoshu.note.biz.domain.entity.NoteDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author aoao
 * @create 2025-09-22-19:18
 */
@Mapper
public interface NoteDOMapper {
    void insert(@Param("note") NoteDO noteDO);
}
