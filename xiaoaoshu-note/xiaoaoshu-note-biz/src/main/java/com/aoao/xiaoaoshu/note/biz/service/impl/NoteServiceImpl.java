package com.aoao.xiaoaoshu.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.note.biz.domain.entity.NoteDO;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.NoteDOMapper;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.TopicDOMapper;
import com.aoao.xiaoaoshu.note.biz.enums.NoteStatusEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteTypeEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteVisibleEnum;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.rpc.IdGeneratorRpcService;
import com.aoao.xiaoaoshu.note.biz.rpc.KVRpcService;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author aoao
 * @create 2025-09-23-0:26
 */
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private TopicDOMapper topicDOMapper;
    @Autowired
    private IdGeneratorRpcService idGeneratorRpcService;
    @Autowired
    private KVRpcService kvRpcService;
    @Autowired
    private NoteDOMapper noteDOMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publish(PublishNoteReqVO reqVO) {
        // 1.判断笔记类型
        Integer type = reqVO.getType();
        if (!NoteTypeEnum.isValid(type)) {
            throw new BizException(ResponseCodeEnum.NOTE_TYPE_ERROR);
        }
        NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(type);
        String imgUris = null;
        // 笔记内容是否为空，默认值为 true，即空
        Boolean isContentEmpty = true;
        String videoUri = null;
        switch (noteTypeEnum) {
            case IMAGE_TEXT: // 图片
                List<String> imgUriList = reqVO.getImgUris();
                // 校验图片是否为空
                Preconditions.checkArgument(CollUtil.isNotEmpty(imgUriList), "笔记图片不能为空");
                // 校验图片数量
                Preconditions.checkArgument(imgUriList.size() <= 8, "笔记图片不能多于 8 张");
                // 将图片链接拼接，以逗号分隔
                imgUris = StringUtils.join(imgUriList, ",");
                break;
            case VIDEO:
                videoUri = reqVO.getVideoUri();
                Preconditions.checkArgument(StringUtils.isNotEmpty(videoUri),"笔记视频不能为空");
                break;
            default:
                break;
        }
        // 调用id生成服务
        String noteId = idGeneratorRpcService.generateNoteId();

        // 笔记内容 UUID
        String contentUuid = null;
        // 笔记内容
        String content = reqVO.getContent();
        if (StringUtils.isNotBlank(content)) {
            isContentEmpty = false;
            contentUuid = UUID.randomUUID().toString();
            boolean isSuccess = kvRpcService.saveNoteContent(contentUuid, content);
            if (!isSuccess) {
                throw new BizException(ResponseCodeEnum.NOTE_PUBLISH_FAIL);
            }
        }

        // 话题
        Long topicId = reqVO.getTopicId();
        String topicName = null;
        if (Objects.nonNull(topicId)) {
            // 获取话题名称
            topicName = topicDOMapper.selectNameByPrimaryKey(topicId);
        }

        // 发布者用户 ID
        Long creatorId = LoginUserContextHolder.getCurrentId();

        // 构建笔记 DO 对象
        NoteDO noteDO = NoteDO.builder()
                .id(Long.valueOf(noteId))
                .isContentEmpty(isContentEmpty)
                .creatorId(creatorId)
                .imgUris(imgUris)
                .title(reqVO.getTitle())
                .topicId(reqVO.getTopicId())
                .topicName(topicName)
                .type(type)
                .visible(NoteVisibleEnum.PUBLIC.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .status(NoteStatusEnum.NORMAL.getCode())
                .isTop(Boolean.FALSE)
                .videoUri(videoUri)
                .contentUuid(contentUuid)
                .build();
        // 插入
        try {
            noteDOMapper.insert(noteDO);
        } catch (Exception e) {
            // RPC: 笔记保存失败，则删除笔记内容
            if (StringUtils.isNotBlank(contentUuid)) {
                kvRpcService.deleteNoteContent(contentUuid);
            }
        }

        return Result.success();
    }
}
