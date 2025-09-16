package com.aoao.xiaoaoshu.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.enums.SexEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.ParamUtils;
import com.aoao.xiaoaoshu.user.biz.constant.RoleConstants;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserRoleDO;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserRoleDOMapper;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.rpc.OssRpcService;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
        Long xiaoaoshuId = stringRedisTemplate.opsForValue().increment(RedisKeyConstants.XIAOAOSHU_ID_GENERATOR_KEY);
        UserDO userDO = new UserDO().builder()
                .phone(registerUserReqDTO.getPhone())
                .xiaoaoshuId(String.valueOf(xiaoaoshuId))
                .nickname("小红薯" + xiaoaoshuId)
                .password(new BCryptPasswordEncoder().encode("654321"))
                .status(0)
                .build();
        // 插入用户表
        userDOMapper.insert(userDO);
        // 获取刚刚添加入库的用户 ID
        Long userId = userDO.getId();
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


}
