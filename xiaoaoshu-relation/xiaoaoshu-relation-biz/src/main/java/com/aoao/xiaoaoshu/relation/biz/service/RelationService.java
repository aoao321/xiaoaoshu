package com.aoao.xiaoaoshu.relation.biz.service;

import com.aoao.framework.common.result.PageResult;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FindFollowingListReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.UnfollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.rsp.FindFollowingUserRspVO;

/**
 * @author aoao
 * @create 2025-10-03-17:13
 */
public interface RelationService {
    Result follow(FollowUserReqVO reqVO);

    Result unfollow(UnfollowUserReqVO reqVO);

    PageResult<FindFollowingUserRspVO> list(FindFollowingListReqVO reqVO);
}
