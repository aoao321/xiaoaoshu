package com.aoao.xiaoaoshu.relation.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;

/**
 * @author aoao
 * @create 2025-10-03-17:13
 */
public interface RelationService {
    Result follow(FollowUserReqVO reqVO);
}
