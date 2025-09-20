package com.aoao.xiaoaoshu.kv.biz.service;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.kv.model.dto.req.AddNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.DeleteNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.FindNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.rsp.FindNoteContentRspDTO;


/**
 * @author aoao
 * @create 2025-09-20-13:46
 */
public interface NoteContentService {
    Result add(AddNoteContentReqDTO addNoteContentReqDTO);

    Result<FindNoteContentRspDTO> find(FindNoteContentReqDTO findNoteContentReqDTO);

    Result delete(DeleteNoteContentReqDTO deleteNoteContentReqDTO);
}
