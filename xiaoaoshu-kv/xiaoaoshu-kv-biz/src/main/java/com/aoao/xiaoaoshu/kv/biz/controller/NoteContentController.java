package com.aoao.xiaoaoshu.kv.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.kv.biz.service.NoteContentService;
import com.aoao.xiaoaoshu.kv.model.dto.req.AddNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.DeleteNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.FindNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.rsp.FindNoteContentRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author aoao
 * @create 2025-09-20-13:46
 */
@RestController
@RequestMapping("/kv/note/content")
public class NoteContentController {

    @Autowired
    private NoteContentService noteContentService;

    @Log("/新增笔记")
    @PostMapping("/add")
    public Result addNoteContent(@Validated @RequestBody AddNoteContentReqDTO addNoteContentReqDTO) {
        return noteContentService.add(addNoteContentReqDTO);
    }

    @Log("/查询笔记")
    @PostMapping("/find")
    public Result<FindNoteContentRspDTO> findNoteContent(@Validated @RequestBody FindNoteContentReqDTO findNoteContentReqDTO) {
        return noteContentService.find(findNoteContentReqDTO);
    }

    @Log("/删除笔记")
    @PostMapping("/delete")
    public Result deleteNoteContent(@Validated @RequestBody DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        return noteContentService.delete(deleteNoteContentReqDTO);
    }


}
