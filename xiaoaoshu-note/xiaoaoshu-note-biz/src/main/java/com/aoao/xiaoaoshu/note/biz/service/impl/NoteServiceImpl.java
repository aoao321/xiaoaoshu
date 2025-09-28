package com.aoao.xiaoaoshu.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.JsonUtil;
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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 笔记详情本地缓存
     */
    private static final Cache<Long, FindNoteDetailRspVO> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10000) // 设置初始容量为 10000 个条目
            .maximumSize(10000) // 设置缓存的最大容量为 10000 个条目
            .expireAfterWrite(1, TimeUnit.HOURS) // 设置缓存条目在写入后 1 小时过期
            .build();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publish(PublishNoteReqVO reqVO) {
        // 判断笔记类型
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
        // 一、从本地缓存获取
        FindNoteDetailRspVO localCache = LOCAL_CACHE.getIfPresent(noteId);
        if (Objects.nonNull(localCache)) {
            return Result.success(localCache);
        }
        // 二、尝试从redis中获取笔记
        String key = RedisKeyConstants.buildNoteDetailKey(noteId);
        String noteDetailStr = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(noteDetailStr)) {
            FindNoteDetailRspVO findNoteDetailRspVO = JsonUtil.fromJson(noteDetailStr, FindNoteDetailRspVO.class);
            // 同时写入本地缓存
            taskExecutor.execute(() -> {
                LOCAL_CACHE.put(noteId, findNoteDetailRspVO);
            });
            return Result.success(findNoteDetailRspVO);
        }
        // 三、未命中，查询数据库
        // 1.查询笔记
        NoteDO noteDO = noteDOMapper.selectByPrimaryKey(noteId);
        if (Objects.isNull(noteDO)) { // 不存在抛出异常、写入redis
            taskExecutor.execute(() -> {
                // 1分钟+随机秒数
                long expiredTime = 60 + RandomUtil.randomInt(60);
                stringRedisTemplate.opsForValue().set(key, "null" , expiredTime, TimeUnit.SECONDS);
            });
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
        FindNoteDetailRspVO findNoteDetailRspVO = FindNoteDetailRspVO.builder()
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
        // 四、写入redis
        // 异步线程中将笔记详情存入 Redis
        taskExecutor.submit(() -> {
            String noteDetailJson1 = JsonUtil.toJson(findNoteDetailRspVO);
            // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
            long expireSeconds = 60*60*24 + RandomUtil.randomInt(60*60*24);
            stringRedisTemplate.opsForValue().set(key, noteDetailJson1, expireSeconds, TimeUnit.SECONDS);
        });
        return Result.success(findNoteDetailRspVO);
    }
}
