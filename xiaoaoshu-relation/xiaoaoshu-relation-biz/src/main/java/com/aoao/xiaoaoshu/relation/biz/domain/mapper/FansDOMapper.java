package com.aoao.xiaoaoshu.relation.biz.domain.mapper;

import com.aoao.xiaoaoshu.relation.biz.domain.entity.FansDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author aoao
 * @create 2025-10-03-17:17
 */
@Mapper
public interface FansDOMapper {
    void insert(@Param("fansDO") FansDO fansDO);
}
