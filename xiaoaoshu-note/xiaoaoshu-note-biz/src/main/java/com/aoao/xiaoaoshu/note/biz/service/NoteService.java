package com.aoao.xiaoaoshu.note.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.model.req.UpdateNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.vo.req.FindNoteDetailReqVO;
import com.aoao.xiaoaoshu.note.biz.vo.rsp.FindNoteDetailRspVO;

/**
 * @author aoao
 * @create 2025-09-23-0:26
 */
public interface NoteService {
    Result publish(PublishNoteReqVO reqVO);

    Result<FindNoteDetailRspVO> findDetail(FindNoteDetailReqVO reqVO);

    Result update(UpdateNoteReqVO reqVO);

    void deleteNoteLocalCache(Long id);
}
