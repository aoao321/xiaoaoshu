package com.aoao.xiaoaoshu.note.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.model.req.*;
import com.aoao.xiaoaoshu.note.biz.model.rsp.FindNoteDetailRspVO;

/**
 * @author aoao
 * @create 2025-09-23-0:26
 */
public interface NoteService {
    Result publish(PublishNoteReqVO reqVO);

    Result<FindNoteDetailRspVO> findDetail(FindNoteDetailReqVO reqVO);

    Result update(UpdateNoteReqVO reqVO);

    Result delete(DeleteNoteReqVO reqVO);

    Result updateVisible(UpdateNoteVisibleOnlyMeReqVO reqVO);

    void deleteNoteLocalCache(Long id);

    void delayDeleteNoteRedisCache(String key);

    Result top(TopNoteReqVO topNoteReqVO);
}
