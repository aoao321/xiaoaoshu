package com.aoao.xiaoaoshu.auth.service.impl;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.domain.mapper.UserDOMapper;
import com.aoao.xiaoaoshu.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author aoao
 * @create 2025-08-24-14:42
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;


    @Override
    public Result logout() {
        return Result.success();
    }
}
