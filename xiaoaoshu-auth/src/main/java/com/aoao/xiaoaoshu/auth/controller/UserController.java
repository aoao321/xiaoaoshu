package com.aoao.xiaoaoshu.auth.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aoao
 * @create 2025-08-24-14:41
 */
@RestController
@RequestMapping()
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/logout")
    @Log(value = "账号登出")
    public Result logout() {
        return authService.logout();
    }

}
