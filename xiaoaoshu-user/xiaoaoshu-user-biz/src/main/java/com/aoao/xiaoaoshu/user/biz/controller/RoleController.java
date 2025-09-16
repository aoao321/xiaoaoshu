package com.aoao.xiaoaoshu.user.biz.controller;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.result.Result;
import com.aoao.xiaoaoshu.user.biz.service.RoleService;
import com.aoao.xiaoaoshu.user.model.dto.req.FindUserRoleByPhoneReqDTO;
import com.aoao.xiaoaoshu.user.model.dto.rsp.FindUserRoleByPhoneRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author aoao
 * @create 2025-09-16-23:00
 */
@RestController
@RequestMapping("/user/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Log(value = "手机号查询用户角色")
    @PostMapping("/findRoleByPhone")
    public Result<List<FindUserRoleByPhoneRspDTO>> findRoleByPhone(@RequestBody FindUserRoleByPhoneReqDTO findUserRoleByPhoneReqDTO) {
        return roleService.findRoleByPhone(findUserRoleByPhoneReqDTO);
    }
}
