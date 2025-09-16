package com.aoao.xiaoaoshu.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.aoao.framework.biz.context.holder.LoginUserContextHolder;
import com.aoao.framework.common.enums.ResponseCodeEnum;
import com.aoao.framework.common.enums.SexEnum;
import com.aoao.framework.common.exception.BizException;
import com.aoao.framework.common.result.Result;
import com.aoao.framework.common.util.ParamUtils;
import com.aoao.xiaoaoshu.oss.api.FileFeignApi;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.rpc.OssRpcService;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
            Preconditions.checkArgument(ParamUtils.checkXiaoaoshuId(xiaoaoshuId), ResponseCodeEnum.XIAOHASHU_ID_VALID_FAIL.getErrorMessage());
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
}
