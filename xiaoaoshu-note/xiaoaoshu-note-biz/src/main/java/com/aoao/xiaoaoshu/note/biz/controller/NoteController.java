package com.aoao.xiaoaoshu.note.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.model.req.*;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import com.aoao.xiaoaoshu.note.biz.model.rsp.FindNoteDetailRspVO;
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

    @Log("笔记更新")
    @PostMapping("/update")
    public Result updateNote(@Validated @RequestBody UpdateNoteReqVO reqVO) {
        return noteService.update(reqVO);
    }

    @Log("笔记删除")
    @PostMapping("/delete")
    public Result delete(@Validated @RequestBody DeleteNoteReqVO reqVO) {
        return noteService.delete(reqVO);
    }

    @Log("仅自己可见")
    @PostMapping("/visible/onlyme")
    public Result updateVisible(@Validated @RequestBody UpdateNoteVisibleOnlyMeReqVO reqVO) {
        return noteService.updateVisible(reqVO);
    }

    @Log("置顶/取消置顶笔记")
    @PostMapping(value = "/top")
    public Result topNote(@Validated @RequestBody TopNoteReqVO topNoteReqVO) {
        return noteService.top(topNoteReqVO);
    }

}
