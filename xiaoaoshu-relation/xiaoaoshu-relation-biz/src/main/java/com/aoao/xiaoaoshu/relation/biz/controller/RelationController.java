package com.aoao.xiaoaoshu.relation.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.UnfollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author aoao
 * @create 2025-10-03-17:13
 */
@RestController
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @Log("用户关注")
    @PostMapping("/follow")
    public Result follow(@RequestBody @Validated FollowUserReqVO reqVO) {
        return relationService.follow(reqVO);
    }

    @Log("用户取关")
    @PostMapping("/unfollow")
    public Result unfollow(@RequestBody @Validated UnfollowUserReqVO reqVO) {
        return relationService.unfollow(reqVO);
    }

}
