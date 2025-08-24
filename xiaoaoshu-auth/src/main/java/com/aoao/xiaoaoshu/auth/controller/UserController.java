package com.aoao.xiaoaoshu.auth.controller;

import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.auth.model.vo.user.UserLoginReqVO;
import com.aoao.xiaoaoshu.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aoao
 * @create 2025-08-24-14:41
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


}
