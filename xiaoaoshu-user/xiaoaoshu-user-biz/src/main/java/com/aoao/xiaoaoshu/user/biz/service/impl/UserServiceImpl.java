package com.aoao.xiaoaoshu.user.biz.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.enums.SexEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.JsonUtil;
import com.aoao.framework.common.util.ParamUtils;
import com.aoao.xiaoaoshu.user.biz.constant.RoleConstants;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserRoleDO;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserRoleDOMapper;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.rpc.IdGeneratorRpcService;
import com.aoao.xiaoaoshu.user.biz.rpc.OssRpcService;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import com.aoao.xiaoaoshu.user.model.dto.req.FindNoteCreatorByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindNoteCreatorByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author aoao
 * @create 2025-09-11-15:09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private OssRpcService ossRpcService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserRoleDOMapper userRoleDOMapper;
    @Autowired
    private IdGeneratorRpcService idGeneratorRpcService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 用户信息本地缓存
     */
    private static final Cache<Long, FindNoteCreatorByIdRspDTO> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10000) // 设置初始容量为 10000 个条目
            .maximumSize(10000) // 设置缓存的最大容量为 10000 个条目
            .expireAfterWrite(1, TimeUnit.HOURS) // 设置缓存条目在写入后 1 小时过期
            .build();


    @Override
    public Result update(UpdateUserInfoReqVO updateUserInfoReqVO) {
        UserDO userDO = new UserDO();
        boolean needUpdate = false;
        // 从过滤器获取到context，设置当前用户id
        userDO.setId(LoginUserContextHolder.getCurrentId());

        // 头像
        MultipartFile avatarFile = updateUserInfoReqVO.getAvatar();
        if (Objects.nonNull(avatarFile)) {
            // 调用对象存储服务上传文件
            String url = ossRpcService.uploadFile(avatarFile);
            if (StringUtils.isBlank(url)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_AVATAR_FAIL);
            }
            userDO.setAvatar(url);
            needUpdate = true;
        }

        // 昵称
        String nickname = updateUserInfoReqVO.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            Preconditions.checkArgument(ParamUtils.checkNickname(nickname), ResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage());
            userDO.setNickname(nickname);
            needUpdate = true;
        }

        // 小哈书号
        String xiaoaoshuId = updateUserInfoReqVO.getXiaoaoshuId();
        if (StringUtils.isNotBlank(xiaoaoshuId)) {
            Preconditions.checkArgument(ParamUtils.checkXiaoaoshuId(xiaoaoshuId), ResponseCodeEnum.XIAOAOSHU_ID_VALID_FAIL.getErrorMessage());
            userDO.setXiaoaoshuId(xiaoaoshuId);
            needUpdate = true;
        }

        // 性别
        Integer sex = updateUserInfoReqVO.getSex();
        if (Objects.nonNull(sex)) {
            Preconditions.checkArgument(SexEnum.isValid(sex), ResponseCodeEnum.SEX_VALID_FAIL.getErrorMessage());
            userDO.setSex(sex);
            needUpdate = true;
        }

        // 生日
        LocalDate birthday = updateUserInfoReqVO.getBirthday();
        if (Objects.nonNull(birthday)) {
            userDO.setBirthday(birthday);
            needUpdate = true;
        }

        // 个人简介
        String introduction = updateUserInfoReqVO.getIntroduction();
        if (StringUtils.isNotBlank(introduction)) {
            Preconditions.checkArgument(ParamUtils.checkLength(introduction, 100), ResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage());
            userDO.setIntroduction(introduction);
            needUpdate = true;
        }

        // 背景图
        MultipartFile backgroundImg = updateUserInfoReqVO.getBackgroundImg();
        if (Objects.nonNull(backgroundImg)) {
            // 调用oss
            String url = ossRpcService.uploadFile(backgroundImg);
            if (StringUtils.isBlank(url)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_BACKGROUND_IMG_FAIL);
            }
            userDO.setBackgroundImg(url);
            needUpdate = true;
        }

        if (needUpdate) {
            // 更新用户信息
            userDO.setUpdateTime(LocalDateTime.now());
            userDOMapper.updateByPrimaryKeySelective(userDO);
        }

        return Result.success();
    }

    @Override
    public Result<Long> register(RegisterUserReqDTO registerUserReqDTO) {
        // 获取全局自增的小哈书 ID
        // Long xiaoaoshuId = stringRedisTemplate.opsForValue().increment(RedisKeyConstants.XIAOAOSHU_ID_GENERATOR_KEY);
        String xiaoaoshuId = idGeneratorRpcService.generateXiaoaoshuId();
        // RPC: 调用分布式 ID 生成服务生成用户 ID
        String userIdStr = idGeneratorRpcService.generateUserId();
        Long userId = Long.valueOf(userIdStr);
        UserDO userDO = new UserDO().builder()
                .id(userId)
                .phone(registerUserReqDTO.getPhone())
                .xiaoaoshuId(String.valueOf(xiaoaoshuId))
                .nickname("小红薯" + xiaoaoshuId)
                .password(new BCryptPasswordEncoder().encode("654321"))
                .status(0)
                .build();
        // 插入用户表
        userDOMapper.insert(userDO);
        // 给该用户分配一个默认角色
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .userId(userId)
                .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                .build();
        userRoleDOMapper.insert(userRoleDO);

        return Result.success(userId);
    }

    @Override
    public Result<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        String phone = findUserByPhoneReqDTO.getPhone();
        UserDO userDO = userDOMapper.getByPhone(phone);
        if (Objects.isNull(userDO)) {
            return Result.fail(ResponseCodeEnum.ABSENT_USER);
        }
        FindUserByPhoneRspDTO findUserByPhoneRspDTO = new FindUserByPhoneRspDTO();
        BeanUtils.copyProperties(userDO, findUserByPhoneRspDTO);
        return Result.success(findUserByPhoneRspDTO);
    }

    @Override
    public Result<FindUserByIdRspDTO> findById(FindUserByIdReqDTO findUserByIdReqDTO) {
        Long id = findUserByIdReqDTO.getId();
        UserDO userDO = userDOMapper.getById(id);
        if (Objects.isNull(userDO)) {
            return Result.fail(ResponseCodeEnum.ABSENT_USER);
        }
        FindUserByIdRspDTO findUserByIdRspDTO = new FindUserByIdRspDTO();
        BeanUtils.copyProperties(userDO, findUserByIdRspDTO);
        return Result.success(findUserByIdRspDTO);

    }

    @Override
    public Result<FindNoteCreatorByIdRspDTO> findNoteCreatorById(FindNoteCreatorByIdReqDTO findNoteCreatorByIdReqDTO) {
        Long id = findNoteCreatorByIdReqDTO.getId();
        // 1.先查询本地缓存
        FindNoteCreatorByIdRspDTO localCache = LOCAL_CACHE.getIfPresent(id);
        if (Objects.nonNull(localCache)) {
            // 返回
            return Result.success(localCache);
        }
        // 2.从redis中根据用户id查询用户基本信息
        // 构建key
        String key = RedisKeyConstants.buildUserInfoKey(id);
        // 查询
        String userInfoStr = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isBlank(userInfoStr)) {// 查询到，直接返回
            FindNoteCreatorByIdRspDTO rspDTO = JsonUtil.fromJson(userInfoStr, FindNoteCreatorByIdRspDTO.class);
            // 写入本地缓存
            taskExecutor.execute(()->{
                LOCAL_CACHE.put(id, rspDTO);
            });
            return Result.success(rspDTO);
        }
        // 3.未查询到，查询数据库
        // 从数据库中查询user
        UserDO userDO = userDOMapper.getById(id);
        // 如果为空，则直接返回null，并将null写入redis
        if (Objects.isNull(userDO)) {
            taskExecutor.execute(()->{
                // 防止缓存穿透，将空数据存入 Redis 缓存 (过期时间不宜设置过长)
                // 保底1分钟 + 随机秒数
                long expireSeconds = 60 + RandomUtil.randomInt(60);
                stringRedisTemplate.opsForValue().set(key, "null", expireSeconds, TimeUnit.SECONDS);
            });
            throw new BizException(ResponseCodeEnum.ABSENT_USER);
        }
        // 构造dto
        FindNoteCreatorByIdRspDTO rspDTO = new FindNoteCreatorByIdRspDTO();
        BeanUtils.copyProperties(userDO, rspDTO);
        // 4.写入redis中
        taskExecutor.submit(()->{
            // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
            long expireSeconds = 60*60*24 + RandomUtil.randomInt(60*60*24);
            stringRedisTemplate.opsForValue().set(key,JsonUtil.toJson(rspDTO),expireSeconds,TimeUnit.SECONDS);
        });

        return Result.success(rspDTO);
    }


}
