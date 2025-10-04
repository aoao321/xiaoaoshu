package com.aoao.xiaoaoshu.relation.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.DateUtils;
import com.aoao.xiaoaoshu.relation.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.relation.biz.domain.entity.FollowingDO;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FansDOMapper;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FollowingDOMapper;
import com.aoao.xiaoaoshu.relation.biz.enums.LuaResultEnum;
import com.aoao.xiaoaoshu.relation.biz.model.dto.FollowUnfollowUserMqDTO;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.rpc.UserRpcService;
import com.aoao.xiaoaoshu.relation.biz.service.RelationService;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
            case ZSET_NOT_EXIST: //不存在zset查询数据库所有正在关注
                List<FollowingDO> followingDOS = followingDOMapper.selectByUserId(currentId);
                // 随机过期时间
                // 保底1天+随机秒数
                long expireSeconds = 60*60*24 + RandomUtil.randomInt(60*60*24);
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
        // 4.发送mq,写入数据库
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

    /**
     * 校验 Lua 脚本结果，根据状态码抛出对应的业务异常
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
