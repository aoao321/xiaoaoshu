package com.aoao.xiaoaoshu.user.biz.service.impl;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.biz.domain.entity.UserDO;
import com.aoao.xiaoaoshu.user.biz.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author aoao
 * @create 2025-09-11-15:09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Override
    public Result update(UpdateUserInfoReqVO updateUserInfoReqVO) {

        return null;
    }
}
