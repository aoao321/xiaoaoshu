package com.aoao.xiaoaoshu.relation.biz.consumer;

import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.xiaoaoshu.relation.biz.config.RabbitConfig;
import com.aoao.xiaoaoshu.relation.biz.domain.entity.FansDO;
import com.aoao.xiaoaoshu.relation.biz.domain.entity.FollowingDO;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FansDOMapper;
import com.aoao.xiaoaoshu.relation.biz.domain.mapper.FollowingDOMapper;
import com.aoao.xiaoaoshu.relation.biz.model.dto.FollowUnfollowUserMqDTO;
import com.aoao.xiaoaoshu.relation.biz.service.RelationService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author aoao
 * @create 2025-10-04-11:22
 */
@Component
@Slf4j
public class FollowUnfollowConsumer {

    @Autowired
    private RelationService relationService;
    @Autowired
    private FollowingDOMapper followingDOMapper;
    @Autowired
    private FansDOMapper fansDOMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RateLimiter rateLimiter;

    @RabbitListener(queues = RabbitConfig.FOLLOW_UNFOLLOW_QUEUE)
    public void onMessage(FollowUnfollowUserMqDTO dto){
        // 流量削峰：通过获取令牌，如果没有令牌可用，将阻塞，直到获得
        rateLimiter.acquire();
        // 1.判断是取关还是关注
        String key = dto.getKey();
        if (Objects.equals(key,RabbitConfig.FOLLOW_ROUTING_KEY)){// 关注
            handleFollowTagMessage(dto);
        }else if(Objects.equals(key,RabbitConfig.UNFOLLOW_ROUTING_KEY)){// 取关

        }else {
            throw new BizException(ResponseCodeEnum.FOLLOW_UNFOLLOW_KEY_ERROR);
        }
    }

    /**
     * 关注
     * @param dto
     */
    private void handleFollowTagMessage(FollowUnfollowUserMqDTO dto) {
        // 判空
        if (Objects.isNull(dto)) return;

        // 幂等性：通过联合唯一索引保证

        Long userId = dto.getUserId();
        Long followUserId = dto.getFollowUserId();
        LocalDateTime createTime = dto.getCreateTime();

        // 编程式提交事务
        boolean isSuccess = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            try {
                // 关注成功需往数据库添加两条记录
                // 关注表：一条记录
                int count = followingDOMapper.insert(FollowingDO.builder()
                        .userId(userId)
                        .followingUserId(followUserId)
                        .createTime(createTime)
                        .build());

                // 粉丝表：一条记录
                if (count > 0) {
                    fansDOMapper.insert(FansDO.builder()
                            .userId(followUserId)
                            .fansUserId(userId)
                            .createTime(createTime)
                            .build());
                }
                return true;
            } catch (Exception ex) {
                status.setRollbackOnly(); // 标记事务为回滚
                log.error("", ex);
            }
            return false;
        }));

        log.info("## 数据库添加记录结果：{}", isSuccess);
        // TODO: 更新 Redis 中被关注用户的 ZSet 粉丝列表
    }

}
