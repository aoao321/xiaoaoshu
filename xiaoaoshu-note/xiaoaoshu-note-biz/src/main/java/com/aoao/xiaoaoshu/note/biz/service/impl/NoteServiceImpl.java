package com.aoao.xiaoaoshu.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.kv.model.dto.rsp.FindNoteContentRspDTO;
import com.aoao.xiaoaoshu.note.biz.domain.entity.NoteDO;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.NoteDOMapper;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.TopicDOMapper;
import com.aoao.xiaoaoshu.note.biz.enums.NoteStatusEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteTypeEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteVisibleEnum;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.rpc.IdGeneratorRpcService;
import com.aoao.xiaoaoshu.note.biz.rpc.KVRpcService;
import com.aoao.xiaoaoshu.note.biz.rpc.UserRpcService;
import com.aoao.xiaoaoshu.note.biz.service.NoteService;
import com.aoao.xiaoaoshu.note.biz.vo.req.FindNoteDetailReqVO;
import com.aoao.xiaoaoshu.note.biz.vo.rsp.FindNoteDetailRspVO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
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
    @Autowired
    private UserRpcService userRpcService;

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

    @Override
    public Result<FindNoteDetailRspVO> findDetail(FindNoteDetailReqVO reqVO) {
        // 获取noteId
        Long noteId = reqVO.getId();
        // 1.查询笔记
        NoteDO noteDO = noteDOMapper.selectByPrimaryKey(noteId);
        if (Objects.isNull(noteDO)) { // 不存在抛出异常
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }
        // 2.判断可见性
        Integer visible = noteDO.getVisible();
        Long currentId = LoginUserContextHolder.getCurrentId();
        Long creatorId = noteDO.getCreatorId();
        if (NoteVisibleEnum.PRIVATE.getCode().equals(visible) && !currentId.equals(creatorId)) { // 判断当前用户是否为作者
            // 私有并且不是作者本人，不可见
            throw new BizException(ResponseCodeEnum.NOTE_PRIVATE);
        }
        // 3.查询作者的个人信息
        FindNoteCreatorByIdRspDTO noteCreator = userRpcService.findNoteCreatorById(creatorId);
        if (Objects.isNull(noteCreator)) {
            noteCreator = FindNoteCreatorByIdRspDTO.builder()
                    .avatar("默认头像")
                    .id(-1l)
                    .nickname("未知用户")
                    .build();
        }
        // 4.判断笔记内容是否为空
        String content = null;
        if (noteDO.getIsContentEmpty().equals(Boolean.FALSE)) { // 调用kv模块查询笔记内容
            FindNoteContentRspDTO noteContent = kvRpcService.findNoteContent(noteDO.getContentUuid());
            content = noteContent.getContent();
        }
        // 5.图片或者视频
        List<String> imgUris = null;
        Integer noteType = noteDO.getType();
        // 如果查询的是图文笔记，需要将图片链接的逗号分隔开，转换成集合
        String imgUrisStr = noteDO.getImgUris();
        if (Objects.equals(noteType, NoteTypeEnum.IMAGE_TEXT.getCode())
                && StringUtils.isNotBlank(imgUrisStr)) {
            imgUris = List.of(imgUrisStr.split(","));
        }
        FindNoteDetailRspVO vo = FindNoteDetailRspVO.builder()
                .avatar(noteCreator.getAvatar())
                .creatorId(creatorId)
                .creatorName(noteCreator.getNickname())
                .content(content)
                .imgUris(imgUris)
                .videoUri(noteDO.getVideoUri())
                .type(noteType)
                .topicId(noteDO.getTopicId())
                .topicName(noteDO.getTopicName())
                .updateTime(noteDO.getUpdateTime())
                .visible(visible)
                .title(noteDO.getTitle())
                .id(noteDO.getId())
                .build();
        return Result.success(vo);
    }
}
