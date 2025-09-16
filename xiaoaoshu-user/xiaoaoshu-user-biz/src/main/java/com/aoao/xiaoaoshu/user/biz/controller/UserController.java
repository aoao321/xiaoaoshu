package com.aoao.xiaoaoshu.user.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.model.dto.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author aoao
 * @create 2025-09-11-15:08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Log(value = "用户修改信息")
    @PostMapping(value = "/update" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateUserInfo(@ModelAttribute @Validated UpdateUserInfoReqVO updateUserInfoReqVO) {
        return userService.update(updateUserInfoReqVO);
    }

    @Log(value = "新用户注册")
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterUserReqDTO registerUserReqDTO) {
        return userService.register(registerUserReqDTO);
    }


}
