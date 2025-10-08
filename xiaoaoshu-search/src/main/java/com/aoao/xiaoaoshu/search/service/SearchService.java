package com.aoao.xiaoaoshu.search.service;

import com.aoao.framework.common.result.PageResult;
import com.aoao.xiaoaoshu.search.model.vo.req.SearchNoteReqVO;
import com.aoao.xiaoaoshu.search.model.vo.req.SearchUserReqVO;
import com.aoao.xiaoaoshu.search.model.vo.rsp.SearchNoteRspVO;
import com.aoao.xiaoaoshu.search.model.vo.rsp.SearchUserRspVO;

/**
 * @author aoao
 * @create 2025-10-08-21:37
 */
public interface SearchService {

    PageResult<SearchUserRspVO> searchUser(SearchUserReqVO searchUserReqVO);

    PageResult<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO);
}
