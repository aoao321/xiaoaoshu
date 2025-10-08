package com.aoao.xiaoaoshu.search.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.PageResult;
import com.aoao.xiaoaoshu.search.model.vo.req.SearchUserReqVO;
import com.aoao.xiaoaoshu.search.model.vo.rsp.SearchUserRspVO;
import com.aoao.xiaoaoshu.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aoao
 * @create 2025-10-08-21:38
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/user")
    @Log("搜索用户")
    public PageResult<SearchUserRspVO> searchUser(@RequestBody @Validated SearchUserReqVO searchUserReqVO) {
        return searchService.searchUser(searchUserReqVO);
    }
}
