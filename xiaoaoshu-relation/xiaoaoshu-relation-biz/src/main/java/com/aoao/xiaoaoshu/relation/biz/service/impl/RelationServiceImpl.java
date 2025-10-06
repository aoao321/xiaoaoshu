package com.aoao.xiaoaoshu.relation.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.PageResult;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.DateUtils;
import com.aoao.framework.common.util.JsonUtil;
import com.aoao.xiaoaoshu.relation.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.relation.biz.domain.entity.FollowingDO;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FansDOMapper;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FollowingDOMapper;
import com.aoao.xiaoaoshu.relation.biz.enums.LuaResultEnum;
import com.aoao.xiaoaoshu.relation.biz.model.dto.FollowUnfollowUserMqDTO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FindFollowingListReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.UnfollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.rsp.FindFollowingUserRspVO;
import com.aoao.xiaoaoshu.relation.biz.rpc.UserRpcService;
import com.aoao.xiaoaoshu.relation.biz.service.RelationService;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author aoao
 * @create 2025-10-03-17:13
 */
@Service
public class RelationServiceImpl implements RelationService {

    @Autowired
    private FollowingDOMapper followingDOMapper;
    @Autowired
    private FansDOMapper fansDOMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;


    @Override
    public Result follow(FollowUserReqVO reqVO) {
        // 1.判断关注和被关注的用户是否相同
        Long followUserId = reqVO.getFollowUserId();
        Long currentId = LoginUserContextHolder.getCurrentId();
        if (Objects.equals(followUserId, currentId)) {
            throw new BizException(ResponseCodeEnum.CANT_FOLLOW_YOUR_SELF);
        }
        // 2.调用userRpc判断被关注用户是否存在
        FindNoteCreatorByIdRspDTO findUserById = userRpcService.findNoteCreatorById(followUserId);
        if (Objects.isNull(findUserById)) {
            throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);
        }
        // 3.验证关注数是否上限,写入redis中
        // 构建当前用户关注列表的 Redis Key
        String followingRedisKey = RedisKeyConstants.buildUserFollowingKey(currentId);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        // Lua 脚本路径
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_check_and_add.lua")));
        // 返回值类型
        script.setResultType(Long.class);
        // 当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long timestamp = DateUtils.localDateTime2Timestamp(now);
        // 执行 Lua 脚本，拿到返回结果
        Long result = stringRedisTemplate.execute(
                script,
                Collections.singletonList(followingRedisKey),
                String.valueOf(followUserId),
                String.valueOf(timestamp)
        );
        // 根据结果判断
        LuaResultEnum resultEnum = LuaResultEnum.valueOf(result);
        switch (resultEnum) {
            case FOLLOW_LIMIT:
                throw new BizException(ResponseCodeEnum.FOLLOWING_COUNT_LIMIT);
            case ALREADY_FOLLOWED:
                throw new BizException(ResponseCodeEnum.ALREADY_FOLLOWED);
            case ZSET_NOT_EXIST: // 不存在zset查询数据库所有正在关注
                List<FollowingDO> followingDOS = followingDOMapper.selectByUserId(currentId);
                // 随机过期时间
                // 保底1天+随机秒数
                long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
                // 若记录为空，直接 ZADD 对象, 并设置过期时间
                if (CollUtil.isEmpty(followingDOS)) {
                    DefaultRedisScript<Long> script2 = new DefaultRedisScript<>();
                    script2.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_add_and_expire.lua")));
                    script2.setResultType(Long.class);
                    // TODO: 可以根据用户类型，设置不同的过期时间，若当前用户为大V, 则可以过期时间设置的长些或者不设置过期时间；如不是，则设置的短些
                    // 如何判断呢？可以从计数服务获取用户的粉丝数，目前计数服务还没创建，则暂时采用统一的过期策略
                    stringRedisTemplate.execute(
                            script2,
                            Collections.singletonList(followingRedisKey),
                            String.valueOf(followUserId),
                            String.valueOf(timestamp),
                            String.valueOf(expireSeconds)
                    );
                } else { // 若记录不为空，则将关注关系数据全量同步到 Redis 中，并设置过期时间；
                    // 构建 Lua 参数
                    Object[] luaArgs = buildLuaArgs(followingDOS, expireSeconds);
                    // 执行 Lua 脚本，批量同步关注关系数据到 Redis 中
                    DefaultRedisScript<Long> script3 = new DefaultRedisScript<>();
                    script3.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
                    script3.setResultType(Long.class);
                    stringRedisTemplate.execute(
                            script3,
                            Collections.singletonList(followingRedisKey),
                            luaArgs
                    );
                    // 再次调用上面的 Lua 脚本：follow_check_and_add.lua , 将最新的关注关系添加进去
                    result = stringRedisTemplate.execute(script, Collections.singletonList(followingRedisKey), followUserId, timestamp);
                    checkLuaScriptResult(result);
                }
                break;
        }
        // 4.发送mq,写入数据库，写入redis
        // 构建消息体 DTO
        FollowUnfollowUserMqDTO followUnfollowUserMqDTO = FollowUnfollowUserMqDTO.builder()
                .userId(currentId)
                .key(RabbitConfig.FOLLOW_ROUTING_KEY)
                .followUserId(followUserId)
                .createTime(now)
                .build();
        rabbitTemplate.convertAndSend(RabbitConfig.FOLLOW_UNFOLLOW_EXCHANGE,
                RabbitConfig.FOLLOW_ROUTING_KEY,
                followUnfollowUserMqDTO);
        return Result.success();
    }

    @Override
    public Result unfollow(UnfollowUserReqVO reqVO) {
        // 1.判断关注和被关注的用户是否相同
        Long unfollowUserId = reqVO.getUnfollowUserId();
        Long currentId = LoginUserContextHolder.getCurrentId();
        if (Objects.equals(unfollowUserId, currentId)) {
            throw new BizException(ResponseCodeEnum.CANT_FOLLOW_YOUR_SELF);
        }
        // 2.调用userRpc判断被关注用户是否存在
        FindNoteCreatorByIdRspDTO findUserById = userRpcService.findNoteCreatorById(unfollowUserId);
        if (Objects.isNull(findUserById)) {
            throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);
        }
        // 3.删除关注
        // 3.1判断是否关注，关注了才可以取关
        // redis
        String userFollowingKey = RedisKeyConstants.buildUserFollowingKey(currentId);
        // 定义脚本，验证是否存在zset，zset是否存在关注的用户
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/unfollow_check_and_delete.lua")));
        script.setResultType(Long.class);
        // 执行
        Long execute = stringRedisTemplate.execute(
                script,
                Collections.singletonList(userFollowingKey),
                String.valueOf(unfollowUserId));
        LuaResultEnum resultEnum = LuaResultEnum.valueOf(execute);
        switch (resultEnum) {
            case ZSET_NOT_EXIST:
                // 查询关注列表
                List<FollowingDO> followingDOS = followingDOMapper.selectByUserId(currentId);
                // 判空
                if (CollUtil.isEmpty(followingDOS)) {
                    throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);
                }
                // 随机过期时间
                // 保底1天+随机秒数
                long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
                // 构建 Lua 参数
                Object[] luaArgs = buildLuaArgs(followingDOS, expireSeconds);

                // 执行 Lua 脚本，批量同步关注关系数据到 Redis 中
                DefaultRedisScript<Long> script3 = new DefaultRedisScript<>();
                script3.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
                script3.setResultType(Long.class);
                stringRedisTemplate.execute(script3, Collections.singletonList(userFollowingKey), luaArgs);

                // 再次调用上面的 Lua 脚本：unfollow_check_and_delete.lua , 将取关的用户删除
                Long result = stringRedisTemplate.execute(script, Collections.singletonList(userFollowingKey), unfollowUserId);
                // 再次校验结果
                if (Objects.equals(result, LuaResultEnum.NOT_FOLLOWED.getCode())) {
                    throw new BizException(ResponseCodeEnum.NOT_FOLLOWED);
                }
                break;
            case NOT_FOLLOWED:
                throw new BizException(ResponseCodeEnum.NOT_FOLLOWED);
        }

        // 3.2发送mq,删除数据库，删除粉丝列表
        FollowUnfollowUserMqDTO followUnfollowUserMqDTO = FollowUnfollowUserMqDTO.builder()
                .followUserId(unfollowUserId)
                .userId(currentId)
                .key(RabbitConfig.UNFOLLOW_ROUTING_KEY)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitConfig.FOLLOW_UNFOLLOW_EXCHANGE,
                RabbitConfig.UNFOLLOW_ROUTING_KEY
                , followUnfollowUserMqDTO
        );
        return Result.success();
    }

    @Override
    public PageResult<FindFollowingUserRspVO> list(FindFollowingListReqVO reqVO) {
        // 查询关注列表的用户id
        Long userId = reqVO.getUserId();
        // 页码
        Integer pageNo = reqVO.getPageNo();
        // 1.获取redis中数据
        String key = RedisKeyConstants.buildUserInfoKey(userId);
        // 查询目标用户关注列表 ZSet 的总大小
        long total = stringRedisTemplate.opsForZSet().zCard(key);
        // 返参
        List<FindFollowingUserRspVO> findFollowingUserRspVOS = null;
        // 每页展示 10 条数据
        long limit = 10;
        if (total > 0) { // redis有数据
            // 计算一共多少页
            long totalPage = PageResult.getTotalPage(total, limit);
            // 请求的页码超出了总页数
            if (pageNo > totalPage) return PageResult.success(null, pageNo, total);
            // 准备从 Redis 中查询 ZSet 分页数据
            // 每页 10 个元素，计算偏移量
            long offset = (pageNo - 1) * limit;
            // 每页从offset后取10个
            // 使用 ZREVRANGEBYSCORE 命令按 score 降序获取元素，同时使用 LIMIT 子句实现分页
            Set<String> followingUserIdsSet = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                    key,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    offset,
                    limit
            );
            if (CollUtil.isNotEmpty(followingUserIdsSet)) {
                // 提取所有用户 ID 到集合中
                List<Long> userIds = followingUserIdsSet.stream().map(object -> Long.valueOf(object.toString())).toList();
                // RPC: 批量查询用户信息
                List<FindNoteCreatorByIdRspDTO> findNoteCreatorByIdRspDTOS = userRpcService.findByIds(userIds);
                // 若不为空，DTO 转 VO
                if (CollUtil.isNotEmpty(findNoteCreatorByIdRspDTOS)) {
                    findFollowingUserRspVOS = findNoteCreatorByIdRspDTOS.stream()
                            .map(dto -> FindFollowingUserRspVO.builder()
                                    .userId(dto.getId())
                                    .avatar(dto.getAvatar())
                                    .nickname(dto.getNickname())
                                    .introduction(dto.getIntroduction())
                                    .build())
                            .toList();
                }
            }
        } else { // redis中没有user了
            // 若 Redis 中没有数据，则从数据库查询
            // 先查询记录总量
            long count = followingDOMapper.selectCountByUserId(userId);
            // 计算一共多少页
            long totalPage = PageResult.getTotalPage(count, limit);
            // 请求的页码超出了总页数
            if (pageNo > totalPage) return PageResult.success(null, pageNo, count);
            // 偏移量
            long offset = PageResult.getOffset(pageNo, limit);
            // 分页查询
            List<FollowingDO> followingDOS = followingDOMapper.selectPageListByUserId(userId, offset, limit);
            // 赋值真实的记录总数
            total = count;
            // 若记录不为空
            if (CollUtil.isNotEmpty(followingDOS)) {
                // 提取所有关注用户 ID 到集合中
                List<Long> userIds = followingDOS.stream().map(FollowingDO::getFollowingUserId).toList();
                // RPC: 调用用户服务，并将 DTO 转换为 VO
                findFollowingUserRspVOS = rpcUserServiceAndDTO2VO(userIds, findFollowingUserRspVOS);
                // TODO: 异步将关注列表全量同步到 Redis
                taskExecutor.submit(() -> syncFollowingList2Redis(userId));
            }
        }

        return PageResult.success(findFollowingUserRspVOS, pageNo, total);
    }

    /**
     * 全量同步关注列表至 Redis 中
     * @param userId
     */
    private void syncFollowingList2Redis(Long userId) {
        // 查询全量关注用户列表（1000位用户）
        List<FollowingDO> followingDOS = followingDOMapper.selectAllByUserId(userId);
        if (CollUtil.isNotEmpty(followingDOS)) {
            // 用户关注列表 Redis Key
            String followingListRedisKey = RedisKeyConstants.buildUserFollowingKey(userId);
            // 随机过期时间
            // 保底1天+随机秒数
            long expireSeconds = 60*60*24 + RandomUtil.randomInt(60*60*24);
            // 构建 Lua 参数
            Object[] luaArgs = buildLuaArgs(followingDOS, expireSeconds);

            // 执行 Lua 脚本，批量同步关注关系数据到 Redis 中
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
            script.setResultType(Long.class);
            stringRedisTemplate.execute(script, Collections.singletonList(followingListRedisKey), luaArgs);
        }
    }

    /**
     * RPC: 调用用户服务，并将 DTO 转换为 VO
     * @param userIds
     * @param findFollowingUserRspVOS
     * @return
     */
    private List<FindFollowingUserRspVO> rpcUserServiceAndDTO2VO(List<Long> userIds, List<FindFollowingUserRspVO> findFollowingUserRspVOS) {
        // RPC: 批量查询用户信息
        List<FindNoteCreatorByIdRspDTO> findNoteCreatorByIdRspDTOS = userRpcService.findByIds(userIds);

        // 若不为空，DTO 转 VO
        if (CollUtil.isNotEmpty(findNoteCreatorByIdRspDTOS)) {
            findFollowingUserRspVOS = findNoteCreatorByIdRspDTOS.stream()
                    .map(dto -> FindFollowingUserRspVO.builder()
                            .userId(dto.getId())
                            .avatar(dto.getAvatar())
                            .nickname(dto.getNickname())
                            .introduction(dto.getIntroduction())
                            .build())
                    .toList();
        }
        return findFollowingUserRspVOS;
    }

    /**
     * 校验 Lua 脚本结果，根据状态码抛出对应的业务异常
     *
     * @param result
     */
    private static void checkLuaScriptResult(Long result) {
        LuaResultEnum luaResultEnum = LuaResultEnum.valueOf(result);

        if (Objects.isNull(luaResultEnum)) throw new RuntimeException("Lua 返回结果错误");
        // 校验 Lua 脚本执行结果
        switch (luaResultEnum) {
            // 关注数已达到上限
            case FOLLOW_LIMIT -> throw new BizException(ResponseCodeEnum.FOLLOWING_COUNT_LIMIT);
            // 已经关注了该用户
            case ALREADY_FOLLOWED -> throw new BizException(ResponseCodeEnum.ALREADY_FOLLOWED);
        }
    }

    /**
     * 构建 Lua 脚本参数
     *
     * @param followingDOS
     * @param expireSeconds
     * @return
     */
    private static Object[] buildLuaArgs(List<FollowingDO> followingDOS, long expireSeconds) {
        int argsLength = followingDOS.size() * 2 + 1;
        Object[] luaArgs = new Object[argsLength];

        int i = 0;
        for (FollowingDO following : followingDOS) {
            luaArgs[i] = String.valueOf(DateUtils.localDateTime2Timestamp(following.getCreateTime())); // score
            luaArgs[i + 1] = String.valueOf(following.getFollowingUserId()); // value
            i += 2;
        }

        luaArgs[argsLength - 1] = String.valueOf(expireSeconds); // expire
        return luaArgs;
    }

}
