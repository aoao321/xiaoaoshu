package com.aoao.xiaoaoshu.relation.biz.domain.mapper;

import com.aoao.xiaoaoshu.relation.biz.domain.entity.FollowingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aoao
 * @create 2025-10-03-17:17
 */
@Mapper
public interface FollowingDOMapper {
    List<FollowingDO> selectByUserId(Long userId);

    int insert(@Param("followingDO") FollowingDO followingDO);

    int deleteByUserIdAndFollowingUserId(@Param("userId") Long userId,@Param("unfollowUserId") Long unfollowUserId);

    long selectCountByUserId(Long userId);

    List<FollowingDO> selectPageListByUserId(@Param("userId") Long userId,
                                             @Param("offset") long offset,
                                             @Param("limit") long limit);

    List<FollowingDO> selectAllByUserId(Long userId);
}
