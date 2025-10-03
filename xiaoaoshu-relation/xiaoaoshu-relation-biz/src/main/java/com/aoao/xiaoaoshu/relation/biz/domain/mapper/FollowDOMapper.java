package com.aoao.xiaoaoshu.relation.biz.domain.mapper;

import com.aoao.xiaoaoshu.relation.biz.domain.entity.FollowingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author aoao
 * @create 2025-10-03-17:17
 */
@Mapper
public interface FollowDOMapper {
    List<FollowingDO> selectByUserId(Long userId);
}
