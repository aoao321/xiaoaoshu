package com.aoao.xiaoaoshu.note.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;

/**
 * @author aoao
 * @create 2025-09-23-0:26
 */
public interface NoteService {
    Result publish(PublishNoteReqVO reqVO);
}
