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
        String uuid = addNoteContentReqDTO.getUuid();
        // 笔记内容
        String content = addNoteContentReqDTO.getContent();

        // 构建数据库 DO 实体类
        NoteContentDO nodeContent = NoteContentDO.builder()
                .id(UUID.fromString(uuid))
                .content(content)
                .build();

        // 插入数据
        noteContentRepository.save(nodeContent);
        return Result.success();
    }

    @Override
    public Result<FindNoteContentRspDTO> find(FindNoteContentReqDTO findNoteContentReqDTO) {
        String uuid = findNoteContentReqDTO.getUuid();
        // 根据笔记 ID 查询笔记内容
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(uuid));
        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }
        NoteContentDO noteContentDO = optional.get();
        // 构建返参 DTO
        FindNoteContentRspDTO findNoteContentRspDTO = FindNoteContentRspDTO.builder()
                .uuid(noteContentDO.getId())
                .content(noteContentDO.getContent())
                .build();
        return Result.success(findNoteContentRspDTO);
    }

    @Override
    public Result delete(DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        String uuid = deleteNoteContentReqDTO.getUuid();
        // 根据笔记 ID 查询笔记内容
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(uuid));
        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }
        noteContentRepository.deleteById(UUID.fromString(uuid));
        return Result.success();
    }
}
