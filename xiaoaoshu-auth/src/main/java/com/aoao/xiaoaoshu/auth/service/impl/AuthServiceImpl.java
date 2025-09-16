package com.aoao.xiaoaoshu.auth.service.impl;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @author aoao
 * @create 2025-08-24-14:42
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public Result logout() {
        return Result.success();
    }
}
