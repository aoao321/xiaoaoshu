package com.aoao.xiaoaoshu.user.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.aoao.xiaoaoshu.user.biz.service.UserService;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByIdReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.req.RegisterUserReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByIdRspDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserByPhoneRspDTO;
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

    @Log(value = "根据用户id查询用户信息")
    @PostMapping("/findById")
    public Result<FindUserByIdRspDTO> findById(@RequestBody FindUserByIdReqDTO findUserByIdReqDTO) {
        return userService.findById(findUserByIdReqDTO);
    }

    @Log(value = "手机号查询用户信息")
    @PostMapping("/findByPhone")
    public Result<FindUserByPhoneRspDTO> findByPhone(@RequestBody FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        return userService.findByPhone(findUserByPhoneReqDTO);
    }




}
