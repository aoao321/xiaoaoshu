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
import com.aoao.xiaoaoshu.note.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.note.biz.domain.entity.NoteDO;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.NoteDOMapper;
import com.aoao.xiaoaoshu.note.biz.domain.mapper.TopicDOMapper;
import com.aoao.xiaoaoshu.note.biz.enums.NoteStatusEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteTypeEnum;
import com.aoao.xiaoaoshu.note.biz.enums.NoteVisibleEnum;
import com.aoao.xiaoaoshu.note.biz.model.req.DeleteNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.model.req.PublishNoteReqVO;
import com.aoao.xiaoaoshu.note.biz.model.req.UpdateNoteReqVO;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.aoao.xiaoaoshu.note.biz.config.RabbitConfig.DELAY_DELETE_NOTE_REDIS_CACHE_EXCHANGE;

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
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
                Preconditions.checkArgument(StringUtils.isNotEmpty(videoUri), "笔记视频不能为空");
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
                stringRedisTemplate.opsForValue().set(key, "null", expiredTime, TimeUnit.SECONDS);
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
        // 3.调用rpc查询作者的个人信息（异步）
        CompletableFuture<FindNoteCreatorByIdRspDTO> creatorFuture =
                CompletableFuture.supplyAsync(() -> userRpcService.findNoteCreatorById(creatorId), taskExecutor);

        // 4.调用rpc查询文本内容（异步）
        CompletableFuture<String> contentFuture = CompletableFuture.supplyAsync(() -> {
            if (noteDO.getIsContentEmpty().equals(Boolean.FALSE)) {
                FindNoteContentRspDTO noteContent = kvRpcService.findNoteContent(noteDO.getContentUuid());
                return noteContent.getContent();
            }
            return null;
        }, taskExecutor);

        // 等待两个调用结束
        CompletableFuture.allOf(creatorFuture, contentFuture).join();
        // 处理异步调用
        FindNoteCreatorByIdRspDTO noteCreator = creatorFuture.join();
        String content = contentFuture.join();
        if (Objects.isNull(noteCreator)) {
            noteCreator = FindNoteCreatorByIdRspDTO.builder()
                    .avatar("默认头像")
                    .id(-1L)
                    .nickname("未知用户")
                    .build();
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
            long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
            stringRedisTemplate.opsForValue().set(key, noteDetailJson1, expireSeconds, TimeUnit.SECONDS);
        });
        return Result.success(findNoteDetailRspVO);
    }

    @Override
    public Result update(UpdateNoteReqVO reqVO) {
        // 1.查询该笔记
        Long id = reqVO.getId();
        NoteDO noteDO = noteDOMapper.selectByPrimaryKey(id);
        if (Objects.isNull(noteDO)) {
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }
        // 笔记类型
        Integer type = reqVO.getType();
        // 2.获取对应类型的枚举
        NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(type);
        // 若非图文、视频，抛出业务业务异常
        if (Objects.isNull(noteTypeEnum)) {
            throw new BizException(ResponseCodeEnum.NOTE_TYPE_ERROR);
        }
        String imgUrlsStr = null;
        String videoUri = null;
        switch (noteTypeEnum) {
            case IMAGE_TEXT: // 存放照片
                List<String> imgUris = reqVO.getImgUris();
                Preconditions.checkArgument(CollUtil.isNotEmpty(imgUris), "笔记图片不能为空");
                Preconditions.checkArgument(imgUris.size() <= 8, "图片不能超过8张");
                // 转换成字符串
                imgUrlsStr = StringUtils.join(imgUris, ",");
                break;
            case VIDEO:
                videoUri = reqVO.getVideoUri();
                Preconditions.checkArgument(StringUtils.isNotEmpty(videoUri), "笔记视频不能为空");
                break;
        }
        // 3.判断topic是否存在
        Long topicId = reqVO.getTopicId();
        String topicName = topicDOMapper.selectNameByPrimaryKey(topicId);
        if (StringUtils.isBlank(topicName)) {
            throw new BizException(ResponseCodeEnum.TOPIC_NOT_FOUND);
        }
        // 4.判断content是否为空
        String content = reqVO.getContent();
        String contentUuid = noteDO.getContentUuid();
        Boolean isContentEmpty = noteDO.getIsContentEmpty();
        // 笔记内容是否更新成功
        boolean isUpdateContentSuccess = false;
        if (StringUtils.isBlank(content)) {
            // 若笔记内容为空，则删除 K-V 存储
            isUpdateContentSuccess = kvRpcService.deleteNoteContent(contentUuid);
            isContentEmpty = Boolean.TRUE;
        } else {
            // 若将无内容的笔记，更新为了有内容的笔记，需要重新生成 UUID
            contentUuid = StringUtils.isBlank(contentUuid) ? UUID.randomUUID().toString() : contentUuid;
            // 调用 K-V 更新短文本
            isUpdateContentSuccess = kvRpcService.saveNoteContent(contentUuid, content);
            isContentEmpty = Boolean.FALSE;
        }
        if (!isUpdateContentSuccess) {
            throw new BizException(ResponseCodeEnum.NOTE_UPDATE_FAIL);
        }

        noteDO.setImgUris(imgUrlsStr);
        noteDO.setVideoUri(videoUri);
        noteDO.setTopicId(topicId);
        noteDO.setTopicName(topicName);
        noteDO.setUpdateTime(LocalDateTime.now());
        noteDO.setIsContentEmpty(isContentEmpty);
        noteDO.setTitle(reqVO.getTitle());
        noteDO.setType(type);

        // 5.更新数据库
        noteDOMapper.updateByPrimaryKey(noteDO);
        // 6.删除 Redis 缓存
        String noteDetailRedisKey = RedisKeyConstants.buildNoteDetailKey(id);
        stringRedisTemplate.delete(noteDetailRedisKey);
        // 7.删除本地缓存
        rabbitTemplate.convertAndSend(
                RabbitConfig.DELETE_NOTE_LOCAL_CACHE_EXCHANGE, // 交换机
                "",                                           // Fanout 模式不用 routing key
                id                                            // 发送笔记ID，消费者根据ID删除本地缓存
        );
        LOCAL_CACHE.invalidate(id);
        // 8.双删
        rabbitTemplate.convertAndSend(
                RabbitConfig.DELAY_DELETE_NOTE_REDIS_CACHE_EXCHANGE,
                RabbitConfig.DELAY_DELETE_NOTE_REDIS_ROUTING_KEY,
                noteDetailRedisKey);
        return Result.success();
    }

    @Override
    public Result delete(DeleteNoteReqVO reqVO) {
        Long id = reqVO.getId();
        // 1.查询数据库
        NoteDO noteDO = noteDOMapper.selectByPrimaryKey(id);
        if (Objects.isNull(noteDO)) {
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }
        // 2.逻辑删除
        noteDO.setStatus(NoteStatusEnum.DELETED.getCode());
        noteDO.setUpdateTime(LocalDateTime.now());
        noteDOMapper.deleteLogically(noteDO);
        // 3.删除redis缓存
        String noteDetailRedisKey = RedisKeyConstants.buildNoteDetailKey(id);
        stringRedisTemplate.delete(noteDetailRedisKey);
        // 4.删除本地缓存
        rabbitTemplate.convertAndSend(RabbitConfig.DELETE_NOTE_LOCAL_CACHE_EXCHANGE,"",id);
        LOCAL_CACHE.invalidate(id);

        return Result.success();
    }

    @Override
    public void deleteNoteLocalCache(Long id) {
        LOCAL_CACHE.invalidate(id);
    }

    @Override
    public void delayDeleteNoteRedisCache(String key) {
        stringRedisTemplate.delete(key);
    }
}
