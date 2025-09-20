package com.aoao.xiaoaoshu.kv.biz.service.impl;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.kv.biz.domain.entity.NoteContentDO;
import com.aoao.xiaoaoshu.kv.biz.domain.repository.NoteContentRepository;
import com.aoao.xiaoaoshu.kv.biz.service.NoteContentService;
import com.aoao.xiaoaoshu.kv.model.dto.req.AddNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.DeleteNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.req.FindNoteContentReqDTO;
import com.aoao.xiaoaoshu.kv.model.dto.rsp.FindNoteContentRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-20-13:47
 */
@Service
public class NoteContentServiceImpl implements NoteContentService {

    @Autowired
    private NoteContentRepository noteContentRepository;

    @Override
    public Result add(AddNoteContentReqDTO addNoteContentReqDTO) {
        // 笔记 ID
        Long noteId = addNoteContentReqDTO.getNoteId();
        // 笔记内容
        String content = addNoteContentReqDTO.getContent();

        // 构建数据库 DO 实体类
        NoteContentDO nodeContent = NoteContentDO.builder()
                .id(UUID.randomUUID()) // TODO: 暂时用 UUID, 目的是为了下一章讲解压测，不用动态传笔记 ID。后续改为笔记服务传过来的笔记 ID
                .content(content)
                .build();

        // 插入数据
        noteContentRepository.save(nodeContent);
        return Result.success();
    }

    @Override
    public Result<FindNoteContentRspDTO> find(FindNoteContentReqDTO findNoteContentReqDTO) {
        String noteId = findNoteContentReqDTO.getNoteId();
        // 根据笔记 ID 查询笔记内容
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(noteId));
        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }
        NoteContentDO noteContentDO = optional.get();
        // 构建返参 DTO
        FindNoteContentRspDTO findNoteContentRspDTO = FindNoteContentRspDTO.builder()
                .noteId(noteContentDO.getId())
                .content(noteContentDO.getContent())
                .build();
        return Result.success(findNoteContentRspDTO);
    }

    @Override
    public Result delete(DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        String noteId = deleteNoteContentReqDTO.getNoteId();
        // 根据笔记 ID 查询笔记内容
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(noteId));
        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }
        noteContentRepository.deleteById(UUID.fromString(noteId));
        return Result.success();
    }
}
