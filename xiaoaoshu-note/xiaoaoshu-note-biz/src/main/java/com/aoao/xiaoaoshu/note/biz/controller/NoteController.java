package com.aoao.xiaoaoshu.note.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import com.aoao.xiaoaoshu.note.biz.vo.req.FindNoteDetailReqVO;
import com.aoao.xiaoaoshu.note.biz.vo.rsp.FindNoteDetailRspVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author aoao
 * @create 2025-09-23-0:25
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Log("笔记发布")
    @PostMapping("/publish")
    public Result publishNote(@Validated @RequestBody PublishNoteReqVO reqVO) {
        return noteService.publish(reqVO);
    }

    @Log("笔记详情")
    @PostMapping("/detail")
    public Result<FindNoteDetailRspVO> findDetail(@Validated @RequestBody FindNoteDetailReqVO reqVO) {
        return noteService.findDetail(reqVO);
    }


}
