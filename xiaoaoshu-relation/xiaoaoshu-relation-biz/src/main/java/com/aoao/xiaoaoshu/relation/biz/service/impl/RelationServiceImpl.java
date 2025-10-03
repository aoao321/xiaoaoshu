package com.aoao.xiaoaoshu.relation.biz.service.impl;

import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FansDOMapper;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FollowDOMapper;
import com.aoao.xiaoaoshu.relation.biz.model.vo.req.FollowUserReqVO;
import com.aoao.xiaoaoshu.relation.biz.rpc.UserRpcService;
import com.aoao.xiaoaoshu.relation.biz.service.RelationService;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author aoao
 * @create 2025-10-03-17:13
 */
@Service
public class RelationServiceImpl implements RelationService {

    @Autowired
    private FollowDOMapper followDOMapper;
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
        // 3.验证关注数是否上限

        // 4.写入redis中

        // 5.写入数据库
        return Result.success();
    }
}
